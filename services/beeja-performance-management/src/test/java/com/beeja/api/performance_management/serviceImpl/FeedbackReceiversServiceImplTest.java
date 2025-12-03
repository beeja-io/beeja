package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.exceptions.BadRequestException;
import com.beeja.api.performance_management.exceptions.DuplicateDataException;
import com.beeja.api.performance_management.model.FeedbackProvider;
import com.beeja.api.performance_management.model.FeedbackReceivers;
import com.beeja.api.performance_management.model.dto.AssignedReviewer;
import com.beeja.api.performance_management.model.dto.ReceiverDetails;
import com.beeja.api.performance_management.repository.FeedbackProviderRepository;
import com.beeja.api.performance_management.repository.FeedbackReceiverRepository;
import com.beeja.api.performance_management.request.ReceiverRequest;
import com.beeja.api.performance_management.response.ReceiverResponse;
import com.beeja.api.performance_management.utils.UserContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackReceiversServiceImplTest {

    @InjectMocks
    FeedbackReceiversServiceImpl service;

    @Mock
    FeedbackReceiverRepository receiverRepo;

    @Mock
    FeedbackProviderRepository providerRepo;

    MockedStatic<UserContext> userCtx;

    final String ORG_ID = "ORG1";
    final String CYCLE = "C1";
    final String QID = "Q1";
    final String EMP1 = "EMP1";

    @BeforeEach
    void setup() {
        userCtx = mockStatic(UserContext.class);
        userCtx.when(UserContext::getLoggedInUserOrganization)
                .thenReturn(Collections.singletonMap("id", ORG_ID));
    }

    @AfterEach
    void tearDown() {
        userCtx.close();
    }

    @Test
    void testAddFeedbackReceivers_Success() {
        ReceiverDetails rd = ReceiverDetails.builder()
                .employeeId(EMP1)
                .fullName("John")
                .department("IT")
                .email("a@a.com")
                .build();

        ReceiverRequest req = new ReceiverRequest(CYCLE, QID, List.of(rd));

        when(receiverRepo.findByOrganizationIdAndCycleIdAndQuestionnaireId(ORG_ID, CYCLE, QID))
                .thenReturn(List.of());

        when(receiverRepo.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        List<FeedbackReceivers> result = service.addFeedbackReceivers(req);

        assertEquals(1, result.size());
        assertEquals(EMP1, result.get(0).getEmployeeId());
    }

    @Test
    void testAddFeedbackReceivers_DuplicateEmployee() {
        ReceiverDetails r1 = new ReceiverDetails(EMP1, "A", "IT", "a@a.com", null);
        ReceiverDetails r2 = new ReceiverDetails(EMP1, "B", "HR", "b@b.com", null);
        ReceiverRequest req = new ReceiverRequest(CYCLE, QID, List.of(r1, r2));

        assertThrows(DuplicateDataException.class, () -> service.addFeedbackReceivers(req));
    }

    @Test
    void testAddFeedbackReceivers_MissingFields() {
        ReceiverDetails r1 = new ReceiverDetails(null, "A", "IT", "a@a.com", null);
        ReceiverRequest req = new ReceiverRequest(CYCLE, QID, List.of(r1));

        assertThrows(BadRequestException.class, () -> service.addFeedbackReceivers(req));
    }

    @Test
    void testAddFeedbackReceivers_MissingCycleOrQuestionnaire() {
        ReceiverDetails rd = ReceiverDetails.builder()
                .employeeId(EMP1)
                .fullName("John")
                .department("IT")
                .build();

        ReceiverRequest req = new ReceiverRequest(null, QID, List.of(rd));

        assertThrows(BadRequestException.class,
                () -> service.addFeedbackReceivers(req));
    }

    @Test
    void testAddFeedbackReceivers_EmptyList() {
        ReceiverRequest req = new ReceiverRequest(CYCLE, QID, List.of());
        assertThrows(BadRequestException.class,
                () -> service.addFeedbackReceivers(req));
    }

    @Test
    void testAddFeedbackReceivers_DuplicateInDatabase() {
        ReceiverDetails rd = ReceiverDetails.builder()
                .employeeId(EMP1)
                .fullName("John")
                .department("IT")
                .build();

        FeedbackReceivers existing = FeedbackReceivers.builder()
                .employeeId(EMP1)
                .build();

        when(receiverRepo.findByOrganizationIdAndCycleIdAndQuestionnaireId(ORG_ID, CYCLE, QID))
                .thenReturn(List.of(existing));

        ReceiverRequest req = new ReceiverRequest(CYCLE, QID, List.of(rd));

        assertThrows(DuplicateDataException.class,
                () -> service.addFeedbackReceivers(req));
    }

    @Test
    void testUpdateFeedbackReceivers_Success() {
        FeedbackReceivers existing = FeedbackReceivers.builder()
                .employeeId(EMP1)
                .fullName("Old")
                .department("OldDept")
                .cycleId(CYCLE)
                .questionnaireId(QID)
                .organizationId(ORG_ID)
                .build();

        ReceiverDetails update = ReceiverDetails.builder()
                .employeeId(EMP1)
                .fullName("New Name")
                .department("NewDept")
                .email("x@x.com")
                .build();

        ReceiverRequest req = new ReceiverRequest(CYCLE, QID, List.of(update));

        when(receiverRepo.findByOrganizationIdAndCycleIdAndQuestionnaireId(ORG_ID, CYCLE, QID))
                .thenReturn(List.of(existing));

        when(receiverRepo.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        List<FeedbackReceivers> result = service.updateFeedbackReceivers(CYCLE, req);

        assertEquals(1, result.size());
        assertEquals("New Name", result.get(0).getFullName());
    }

    @Test
    void testUpdateFeedbackReceivers_MissingRequiredFields() {
        ReceiverDetails invalid = ReceiverDetails.builder()
                .employeeId(EMP1)
                .fullName("")
                .department("IT")
                .build();

        ReceiverRequest req = new ReceiverRequest(CYCLE, QID, List.of(invalid));

        assertThrows(BadRequestException.class,
                () -> service.updateFeedbackReceivers(CYCLE, req));
    }

    @Test
    void testUpdateFeedbackReceivers_RemoveExistingReceiver() {
        FeedbackReceivers existing = FeedbackReceivers.builder()
                .employeeId("OLD")
                .fullName("Old Name")
                .department("Dept")
                .organizationId(ORG_ID)
                .cycleId(CYCLE)
                .questionnaireId(QID)
                .build();

        ReceiverDetails newReceiver = ReceiverDetails.builder()
                .employeeId(EMP1)
                .fullName("John")
                .department("IT")
                .build();

        ReceiverRequest req = new ReceiverRequest(CYCLE, QID, List.of(newReceiver));

        when(receiverRepo.findByOrganizationIdAndCycleIdAndQuestionnaireId(ORG_ID, CYCLE, QID))
                .thenReturn(List.of(existing));

        when(receiverRepo.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        List<FeedbackReceivers> result = service.updateFeedbackReceivers(CYCLE, req);

        assertEquals(1, result.size());
        verify(receiverRepo, times(1)).deleteAll(any());
    }

    @Test
    void testUpdateFeedbackReceivers_MissingCycleOrQuestionnaire() {
        ReceiverRequest req = new ReceiverRequest(null, QID, List.of());
        assertThrows(BadRequestException.class,
                () -> service.updateFeedbackReceivers(null, req));
    }

    @Test
    void testGetFeedbackReceiversList_NoReceivers() {
        when(receiverRepo.findByOrganizationIdAndCycleIdAndQuestionnaireId(ORG_ID, CYCLE, QID))
                .thenReturn(List.of());

        ReceiverResponse res = service.getFeedbackReceiversList(CYCLE, QID);

        assertTrue(res.getReceivers().isEmpty());
        assertEquals(CYCLE, res.getCycleId());
    }

    @Test
    void testGetFeedbackReceiversList_StatusNotAssigned() {
        FeedbackReceivers fr = FeedbackReceivers.builder()
                .employeeId(EMP1)
                .fullName("John")
                .department("IT")
                .build();

        when(receiverRepo.findByOrganizationIdAndCycleIdAndQuestionnaireId(ORG_ID, CYCLE, QID))
                .thenReturn(List.of(fr));

        when(providerRepo.findByOrganizationIdAndEmployeeIdAndCycleId(ORG_ID, EMP1, CYCLE))
                .thenReturn(Optional.empty());

        ReceiverResponse res = service.getFeedbackReceiversList(CYCLE, QID);

        assertEquals(ProviderStatus.NOT_ASSIGNED, res.getReceivers().get(0).getProviderStatus());
    }

    @Test
    void testGetFeedbackReceiversList_ReviewerListEmpty() {
        FeedbackReceivers fr = FeedbackReceivers.builder()
                .employeeId(EMP1)
                .fullName("John")
                .department("IT")
                .build();

        FeedbackProvider fp = new FeedbackProvider();
        fp.setAssignedReviewers(List.of());

        when(receiverRepo.findByOrganizationIdAndCycleIdAndQuestionnaireId(ORG_ID, CYCLE, QID))
                .thenReturn(List.of(fr));

        when(providerRepo.findByOrganizationIdAndEmployeeIdAndCycleId(ORG_ID, EMP1, CYCLE))
                .thenReturn(Optional.of(fp));

        ReceiverResponse res = service.getFeedbackReceiversList(CYCLE, QID);

        assertEquals(ProviderStatus.NOT_ASSIGNED, res.getReceivers().get(0).getProviderStatus());
    }

    @Test
    void testGetFeedbackReceiversList_NoReviewerStatusMatched() {

        FeedbackReceivers fr = FeedbackReceivers.builder()
                .employeeId(EMP1)
                .fullName("John")
                .department("IT")
                .build();

        AssignedReviewer reviewer = new AssignedReviewer("REV1", "Role", null);

        FeedbackProvider fp = new FeedbackProvider();
        fp.setAssignedReviewers(List.of(reviewer));

        when(receiverRepo.findByOrganizationIdAndCycleIdAndQuestionnaireId(ORG_ID, CYCLE, QID))
                .thenReturn(List.of(fr));

        when(providerRepo.findByOrganizationIdAndEmployeeIdAndCycleId(ORG_ID, EMP1, CYCLE))
                .thenReturn(Optional.of(fp));

        ReceiverResponse res = service.getFeedbackReceiversList(CYCLE, QID);

        assertEquals(ProviderStatus.NOT_ASSIGNED, res.getReceivers().get(0).getProviderStatus());
    }

    @Test
    void testGetFeedbackReceiversList_StatusInProgress() {
        FeedbackReceivers fr = FeedbackReceivers.builder()
                .employeeId(EMP1)
                .fullName("John")
                .department("IT")
                .build();

        AssignedReviewer reviewer = new AssignedReviewer("REV1", "Role", ProviderStatus.IN_PROGRESS);

        FeedbackProvider fp = new FeedbackProvider();
        fp.setAssignedReviewers(List.of(reviewer));

        when(receiverRepo.findByOrganizationIdAndCycleIdAndQuestionnaireId(ORG_ID, CYCLE, QID))
                .thenReturn(List.of(fr));

        when(providerRepo.findByOrganizationIdAndEmployeeIdAndCycleId(ORG_ID, EMP1, CYCLE))
                .thenReturn(Optional.of(fp));

        ReceiverResponse res = service.getFeedbackReceiversList(CYCLE, QID);

        assertEquals(ProviderStatus.IN_PROGRESS, res.getReceivers().get(0).getProviderStatus());
    }

    @Test
    void testGetFeedbackReceiversList_StatusCompleted() {
        FeedbackReceivers fr = FeedbackReceivers.builder()
                .employeeId(EMP1)
                .fullName("John")
                .department("IT")
                .build();

        AssignedReviewer reviewer = new AssignedReviewer("REV1", "Role", ProviderStatus.COMPLETED);

        FeedbackProvider fp = new FeedbackProvider();
        fp.setAssignedReviewers(List.of(reviewer));

        when(receiverRepo.findByOrganizationIdAndCycleIdAndQuestionnaireId(ORG_ID, CYCLE, QID))
                .thenReturn(List.of(fr));

        when(providerRepo.findByOrganizationIdAndEmployeeIdAndCycleId(ORG_ID, EMP1, CYCLE))
                .thenReturn(Optional.of(fp));

        ReceiverResponse res = service.getFeedbackReceiversList(CYCLE, QID);

        assertEquals(ProviderStatus.COMPLETED, res.getReceivers().get(0).getProviderStatus());
    }
}
