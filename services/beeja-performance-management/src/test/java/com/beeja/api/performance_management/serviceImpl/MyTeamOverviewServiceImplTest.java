package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.client.AccountClient;
import com.beeja.api.performance_management.client.EmployeeFeignClient;
import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.model.*;
import com.beeja.api.performance_management.model.dto.*;
import com.beeja.api.performance_management.repository.EvaluationCycleRepository;
import com.beeja.api.performance_management.repository.FeedbackProviderRepository;
import com.beeja.api.performance_management.repository.FeedbackReceiverRepository;
import com.beeja.api.performance_management.repository.OverallRatingRepository;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyTeamOverviewServiceImplTest {

    @InjectMocks
    private MyTeamOverviewServiceImpl service;

    @Mock
    private OverallRatingRepository overallRatingRepository;

    @Mock
    private EvaluationCycleRepository evaluationCycleRepository;

    @Mock
    private FeedbackReceiverRepository feedbackReceiverRepository;

    @Mock
    private EmployeeFeignClient employeeFeignClient;

    @Mock
    private AccountClient accountClient;

    @Mock
    private FeedbackProviderRepository repository;

    private MockedStatic<UserContext> userContextMock;

    private static final String ORG_ID = "ORG_X";
    private static final String EMP_ID = "EMP_1";

    @BeforeEach
    void setUp() {
        userContextMock = mockStatic(UserContext.class);
        userContextMock.when(UserContext::getLoggedInUserOrganization)
                .thenReturn(Map.of(Constants.ID, ORG_ID));
        userContextMock.when(UserContext::getLoggedInUserName)
                .thenReturn("TestUser");
    }

    @AfterEach
    void tearDown() {
        userContextMock.close();
    }

    @Test
    void getFeedbackStatus_NoProviders_ReturnsZeros() {
        when(repository.findProvidersByOrganizationIdAndEmployeeId(ORG_ID, EMP_ID))
                .thenReturn(Collections.emptyList());

        FeedbackStatusResponse status = service.getFeedbackStatus(EMP_ID);

        assertNotNull(status);
        assertEquals(0, status.getTotalAssignedReviewers());
        assertEquals(0, status.getFeedbackGivenTillNow());
    }

    @Test
    void getFeedbackStatus_WithProviders_ComputesCounts() {
        AssignedReviewer r1 = new AssignedReviewer("R1", "role", ProviderStatus.COMPLETED);
        AssignedReviewer r2 = new AssignedReviewer("R2", "role", ProviderStatus.IN_PROGRESS);

        FeedbackProvider p1 = new FeedbackProvider();
        p1.setAssignedReviewers(List.of(r1, r2));

        FeedbackProvider p2 = new FeedbackProvider();
        p2.setAssignedReviewers(List.of(new AssignedReviewer("R3", "role", ProviderStatus.COMPLETED)));

        when(repository.findProvidersByOrganizationIdAndEmployeeId(ORG_ID, EMP_ID))
                .thenReturn(List.of(p1, p2));

        FeedbackStatusResponse status = service.getFeedbackStatus(EMP_ID);

        assertNotNull(status);
        assertEquals(3, status.getTotalAssignedReviewers());
        assertEquals(2, status.getFeedbackGivenTillNow()); // r1 and r3 completed
    }

    @Test
    void createOrUpdateOverallRating_CreatesNewWhenNotPresent() {
        when(overallRatingRepository.findByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID))
                .thenReturn(Optional.empty());

        OverallRating saved = new OverallRating();
        saved.setEmployeeId(EMP_ID);
        saved.setRating(4.5);
        saved.setComments("Good");
        saved.setOrganizationId(ORG_ID);

        when(overallRatingRepository.save(any(OverallRating.class))).thenReturn(saved);

        OverallRating res = service.createOrUpdateOverallRating(EMP_ID, 4.5, "Good");

        assertNotNull(res);
        assertEquals(EMP_ID, res.getEmployeeId());
        assertEquals(4.5, res.getRating());
        verify(overallRatingRepository, times(1)).save(any(OverallRating.class));
    }

    @Test
    void createOrUpdateOverallRating_UpdatesExisting() {
        OverallRating existing = new OverallRating();
        existing.setEmployeeId(EMP_ID);
        existing.setRating(3.0);
        existing.setComments("Old");
        existing.setOrganizationId(ORG_ID);

        when(overallRatingRepository.findByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID))
                .thenReturn(Optional.of(existing));

        when(overallRatingRepository.save(any(OverallRating.class))).thenAnswer(inv -> inv.getArgument(0));

        OverallRating res = service.createOrUpdateOverallRating(EMP_ID, 5.0, "Excellent");

        assertNotNull(res);
        assertEquals(5.0, res.getRating());
        assertEquals("Excellent", res.getComments());
        assertEquals("TestUser", res.getGivenBy());
        verify(overallRatingRepository, times(1)).save(existing);
    }

    @Test
    void createOrUpdateOverallRating_SaveThrows_RuntimeException() {
        when(overallRatingRepository.findByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID))
                .thenReturn(Optional.empty());
        when(overallRatingRepository.save(any())).thenThrow(new RuntimeException("DB down"));

        assertThrows(RuntimeException.class, () ->
                service.createOrUpdateOverallRating(EMP_ID, 4.0, "x"));
    }

    @Test
    void getOverallRatingByEmployeeId_Found() {
        OverallRating rating = new OverallRating();
        rating.setEmployeeId(EMP_ID);
        rating.setRating(4.0);
        rating.setOrganizationId(ORG_ID);

        when(overallRatingRepository.findByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID))
                .thenReturn(Optional.of(rating));

        OverallRating res = service.getOverallRatingByEmployeeId(EMP_ID);
        assertNotNull(res);
        assertEquals(4.0, res.getRating());
    }

    @Test
    void getOverallRatingByEmployeeId_RepositoryThrows_RuntimeException() {
        when(overallRatingRepository.findByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID))
                .thenThrow(new RuntimeException("db"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.getOverallRatingByEmployeeId(EMP_ID));
        assertTrue(ex.getMessage().contains("No ratings found for employee"));
    }

    @Test
    void deleteOverallRatingByEmployeeId_Success() {
        doNothing().when(overallRatingRepository).deleteByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID);
        service.deleteOverallRatingByEmployeeId(EMP_ID);
        verify(overallRatingRepository, times(1)).deleteByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID);
    }

    @Test
    void deleteOverallRatingByEmployeeId_RepositoryThrows_RuntimeException() {
        doThrow(new RuntimeException("db")).when(overallRatingRepository).deleteByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID);
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.deleteOverallRatingByEmployeeId(EMP_ID));
        assertTrue(ex.getMessage().contains("Final rating not found"));
    }

    @Test
    void getCycleIdsByEmployeeId_NoReceivers_ReturnsEmptyList() {
        when(feedbackReceiverRepository.findByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID))
                .thenReturn(Collections.emptyList());

        List<EmployeeCycleInfo> res = service.getCycleIdsByEmployeeId(EMP_ID);

        assertNotNull(res);
        assertTrue(res.isEmpty());
    }

    @Test
    void getCycleIdsByEmployeeId_ReceiverFound_CycleNull_ReturnsNullName() {
        FeedbackReceivers r = FeedbackReceivers.builder()
                .employeeId(EMP_ID)
                .cycleId("CYCLE_1")
                .build();

        when(feedbackReceiverRepository.findByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID))
                .thenReturn(List.of(r));

        when(evaluationCycleRepository.getCycleByOrganizationIdAndId(ORG_ID, "CYCLE_1"))
                .thenReturn(null);

        List<EmployeeCycleInfo> res = service.getCycleIdsByEmployeeId(EMP_ID);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("CYCLE_1", res.get(0).getCycleId());
        assertNull(res.get(0).getCycleName());
    }

    @Test
    void getCycleIdsByEmployeeId_ReceiverFound_CycleFound_ReturnsName() {
        FeedbackReceivers r = FeedbackReceivers.builder()
                .employeeId(EMP_ID)
                .cycleId("CYCLE_2")
                .build();

        EvaluationCycle c = new EvaluationCycle();
        c.setId("CYCLE_2");
        c.setName("Annual");

        when(feedbackReceiverRepository.findByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID))
                .thenReturn(List.of(r));

        when(evaluationCycleRepository.getCycleByOrganizationIdAndId(ORG_ID, "CYCLE_2"))
                .thenReturn(c);

        List<EmployeeCycleInfo> res = service.getCycleIdsByEmployeeId(EMP_ID);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Annual", res.get(0).getCycleName());
    }

    @Test
    void getCycleIdsByEmployeeId_RepositoryThrows_RuntimeException() {

        when(feedbackReceiverRepository.findByEmployeeIdAndOrganizationId(EMP_ID, ORG_ID))
                .thenThrow(new RuntimeException("db"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.getCycleIdsByEmployeeId(EMP_ID));

        assertTrue(ex.getMessage().contains(Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND));
        assertTrue(ex.getMessage().contains(EMP_ID));
    }

    @Test
    void getEmployeePerformanceData_EmptyEmployees_ReturnsEmptyPagination() {
        when(employeeFeignClient.getEmployeesByLoggedInUserOrganization())
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));
        when(accountClient.getUsersByLoggedInUserOrganization())
                .thenReturn(Collections.emptyList());

        PaginatedEmployeePerformanceResponse resp = service.getEmployeePerformanceData(null, null, null, null, 1, 10);

        assertNotNull(resp);
        assertEquals(0, resp.getTotalRecords());
        assertTrue(resp.getData().isEmpty());
    }

    @Test
    void getEmployeePerformanceData_WithOneEmployee_FiltersApplied_StatusFilter() {
        EmployeeSummaryDTO emp = new EmployeeSummaryDTO();
        emp.setEmployeeId("E1");
        emp.setOrganizationId(ORG_ID);
        JobDetailsCompressed jd = new JobDetailsCompressed();
        jd.setDesignation("Dev");
        jd.setDepartment("IT");
        jd.setEmployementType("Fulltime");
        emp.setJobDetails(jd);
        emp.setProfilePictureId("pic1");

        BasicUserInfoDTO user = new BasicUserInfoDTO();
        user.setEmployeeId("E1");
        user.setFirstName("F");
        user.setLastName("L");
        user.setEmail("a@b.com");
        user.setActive(true);

        when(employeeFeignClient.getEmployeesByLoggedInUserOrganization())
                .thenReturn(ResponseEntity.ok(List.of(emp)));
        when(accountClient.getUsersByLoggedInUserOrganization())
                .thenReturn(List.of(user));

        OverallRating rating = new OverallRating();
        rating.setEmployeeId("E1");
        rating.setRating(3.5);
        rating.setOrganizationId(ORG_ID);

        when(overallRatingRepository.findByEmployeeIdAndOrganizationId("E1", ORG_ID))
                .thenReturn(Optional.of(rating));

        AssignedReviewer rr = new AssignedReviewer("RV1", "role", ProviderStatus.COMPLETED);
        FeedbackProvider fp = new FeedbackProvider();
        fp.setAssignedReviewers(List.of(rr));

        when(repository.findProvidersByOrganizationIdAndEmployeeId(ORG_ID, "E1"))
                .thenReturn(List.of(fp));

        PaginatedEmployeePerformanceResponse resp = service.getEmployeePerformanceData(
                "IT", "Dev", "Fulltime", "completed", 1, 10);

        assertNotNull(resp);
        assertEquals(1, resp.getTotalRecords());
        assertEquals(1, resp.getData().size());
        EmployeePerformanceDTO dto = resp.getData().get(0);
        assertEquals("E1", dto.getEmployeeId());
        assertEquals(1, dto.getNumberOfReviewersAssigned());
        assertEquals(1, dto.getNumberOfReviewerResponses());
        assertEquals(3.5, dto.getOverallRating());
    }
}
