package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.client.AccountClient;
import com.beeja.api.performance_management.client.EmployeeFeignClient;
import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.model.FeedbackProvider;
import com.beeja.api.performance_management.model.dto.AssignedReviewer;
import com.beeja.api.performance_management.model.dto.EmployeeIdNameDTO;
import com.beeja.api.performance_management.repository.FeedbackProviderRepository;
import com.beeja.api.performance_management.request.FeedbackProviderRequest;
import com.beeja.api.performance_management.response.FeedbackFormSummaryResponse;
import com.beeja.api.performance_management.response.FeedbackProviderDetails;
import com.beeja.api.performance_management.service.EvaluationCycleService;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackProviderServiceImplTest {

    @InjectMocks
    FeedbackProviderServiceImpl service;

    @Mock
    FeedbackProviderRepository repository;

    @Mock
    AccountClient accountClient;

    @Mock
    EmployeeFeignClient employeeFeignClient;

    @Mock
    EvaluationCycleService evaluationCycleService;

    private static final String ORG_ID = "ORG123";
    private static final String EMPLOYEE_ID = "EMP1";
    private static final String REVIEWER_ID = "REV1";
    private static final String CYCLE_ID = "C100";

    private MockedStatic<UserContext> userContextMock;

    @BeforeEach
    void setUp() {
        userContextMock = mockStatic(UserContext.class);
        userContextMock.when(() -> UserContext.getLoggedInUserOrganization())
                .thenReturn(Map.of(Constants.ID, ORG_ID));
        userContextMock.when(UserContext::getLoggedInEmployeeId)
                .thenReturn(REVIEWER_ID);
    }

    @AfterEach
    void tearDown() {
        userContextMock.close();
    }

    @Test
    void testAssignFeedbackProvider_Success() {
        AssignedReviewer reviewer = new AssignedReviewer(REVIEWER_ID, "ROLE", null);

        FeedbackProviderRequest request = new FeedbackProviderRequest();
        request.setCycleId(CYCLE_ID);
        request.setQuestionnaireId("Q1");
        request.setAssignedReviewers(List.of(reviewer));

        when(repository.findByOrganizationIdAndEmployeeIdAndCycleId(ORG_ID, EMPLOYEE_ID, CYCLE_ID))
                .thenReturn(Optional.empty());

        FeedbackProvider savedProvider = new FeedbackProvider();
        savedProvider.setEmployeeId(EMPLOYEE_ID);
        savedProvider.setCycleId(CYCLE_ID);
        savedProvider.setAssignedReviewers(List.of(reviewer));
        when(repository.save(any())).thenReturn(savedProvider);

        List<FeedbackProvider> result = service.assignFeedbackProvider(EMPLOYEE_ID, request);

        assertEquals(1, result.size());
        assertEquals(EMPLOYEE_ID, result.get(0).getEmployeeId());
        assertEquals(1, result.get(0).getAssignedReviewers().size());
    }

    @Test
    void testUpdateFeedbackProviders_Success() {
        AssignedReviewer oldReviewer = new AssignedReviewer(REVIEWER_ID, "ROLE", ProviderStatus.IN_PROGRESS);

        FeedbackProvider existing = new FeedbackProvider();
        existing.setEmployeeId(EMPLOYEE_ID);
        existing.setCycleId(CYCLE_ID);
        existing.setQuestionnaireId("Q1");
        existing.setAssignedReviewers(List.of(oldReviewer));

        when(repository.findByOrganizationIdAndEmployeeIdAndCycleId(ORG_ID, EMPLOYEE_ID, CYCLE_ID))
                .thenReturn(Optional.of(existing));

        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FeedbackProviderRequest request = new FeedbackProviderRequest();
        request.setCycleId(CYCLE_ID);
        request.setQuestionnaireId("Q1");
        request.setAssignedReviewers(List.of(new AssignedReviewer(REVIEWER_ID, "ROLE", null)));

        List<FeedbackProvider> result = service.updateFeedbackProviders(request, EMPLOYEE_ID);

        assertEquals(1, result.size());
        assertEquals(EMPLOYEE_ID, result.get(0).getEmployeeId());
        assertEquals(1, result.get(0).getAssignedReviewers().size());
        assertEquals(ProviderStatus.IN_PROGRESS, result.get(0).getAssignedReviewers().get(0).getStatus());
    }

    @Test
    void testGetFeedbackFormDetails_Success() {
        AssignedReviewer reviewer = new AssignedReviewer(REVIEWER_ID, "ROLE", ProviderStatus.IN_PROGRESS);

        FeedbackProvider provider = new FeedbackProvider();
        provider.setEmployeeId(EMPLOYEE_ID);
        provider.setAssignedReviewers(List.of(reviewer));

        when(repository.findByOrganizationIdAndEmployeeIdAndCycleId(ORG_ID, EMPLOYEE_ID, CYCLE_ID))
                .thenReturn(Optional.of(provider));

        when(accountClient.getEmployeeNamesById(List.of(EMPLOYEE_ID)))
                .thenReturn(List.of(new EmployeeIdNameDTO(EMPLOYEE_ID, "John Doe", "IT")));
        when(accountClient.getEmployeeNamesById(List.of(REVIEWER_ID)))
                .thenReturn(List.of(new EmployeeIdNameDTO(REVIEWER_ID, "Jane Smith", "HR")));

        FeedbackProviderDetails details = service.getFeedbackFormDetails(EMPLOYEE_ID, CYCLE_ID, null);

        assertNotNull(details);
        assertEquals("John Doe", details.getEmployeeName());
        assertEquals(1, details.getAssignedReviewers().size());
        assertEquals("Jane Smith", details.getAssignedReviewers().get(0).getReviewerName());
    }

    @Test
    void testGetEmployeesAssignedToReviewer_Success() {
        AssignedReviewer reviewer = new AssignedReviewer(REVIEWER_ID, "ROLE", ProviderStatus.IN_PROGRESS);

        FeedbackProvider provider = new FeedbackProvider();
        provider.setEmployeeId(EMPLOYEE_ID);
        provider.setAssignedReviewers(List.of(reviewer));

        when(repository.findByOrganizationId(ORG_ID))
                .thenReturn(List.of(provider));

        when(accountClient.getEmployeeNamesById(List.of(EMPLOYEE_ID)))
                .thenReturn(List.of(new EmployeeIdNameDTO(EMPLOYEE_ID, "John Doe", "IT")));
        when(accountClient.getEmployeeNamesById(List.of(REVIEWER_ID)))
                .thenReturn(List.of(new EmployeeIdNameDTO(REVIEWER_ID, "Jane Smith", "HR")));
        when(employeeFeignClient.getDepartmentsByEmployeeIds(List.of(EMPLOYEE_ID)))
                .thenReturn(List.of(new com.beeja.api.performance_management.model.dto.EmployeeDepartmentDTO(EMPLOYEE_ID, "IT")));

        Map<String, Object> jobDetails = Map.of("designation", "Developer");
        Map<String, Object> employeeMap = Map.of("jobDetails", jobDetails);
        Map<String, Object> responseMap = Map.of("employee", employeeMap);

        when(employeeFeignClient.getEmployeeByEmployeeId(EMPLOYEE_ID))
                .thenReturn(ResponseEntity.ok(responseMap));

        var response = service.getEmployeesAssignedToReviewer();

        assertNotNull(response);
        assertEquals(REVIEWER_ID, response.getReviewerId());
        assertEquals(1, response.getAssignedEmployees().size());

        assertEquals("Developer", response.getAssignedEmployees().get(0).getDesignation());
    }


    @Test
    void testGetFormsByEmployeeAndReviewer_Success() {
        AssignedReviewer reviewer = new AssignedReviewer(REVIEWER_ID, "ROLE", ProviderStatus.IN_PROGRESS);

        FeedbackProvider provider = new FeedbackProvider();
        provider.setEmployeeId(EMPLOYEE_ID);
        provider.setCycleId(CYCLE_ID);
        provider.setAssignedReviewers(List.of(reviewer));

        when(repository.findByOrganizationIdAndEmployeeId(ORG_ID, EMPLOYEE_ID))
                .thenReturn(List.of(provider));

        com.beeja.api.performance_management.model.EvaluationCycle cycle =
                new com.beeja.api.performance_management.model.EvaluationCycle();
        cycle.setId(CYCLE_ID);
        cycle.setName("Annual Cycle");

        when(evaluationCycleService.getCycleById(CYCLE_ID)).thenReturn(cycle);

        List<FeedbackFormSummaryResponse> result = service.getFormsByEmployeeAndReviewer(EMPLOYEE_ID, REVIEWER_ID);

        assertEquals(1, result.size());
        assertEquals("Annual Cycle", result.get(0).getCycleName());
        assertEquals("IN_PROGRESS", result.get(0).getStatus());
    }
}
