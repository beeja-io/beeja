package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.*;
import com.beeja.api.performance_management.exceptions.InvalidOperationException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.*;
import com.beeja.api.performance_management.model.dto.*;
import com.beeja.api.performance_management.repository.*;
import com.beeja.api.performance_management.response.ReceiverResponse;
import com.beeja.api.performance_management.service.*;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationCycleServiceImplTest {

    @InjectMocks
    private EvaluationCycleServiceImpl service;

    @Mock private EvaluationCycleRepository cycleRepo;
    @Mock private QuestionnaireService questionnaireService;
    @Mock private FeedbackProviderRepository providerRepo;
    @Mock private FeedbackReceiverRepository receiverRepo;
    @Mock private FeedbackResponseRepository responseRepo;
    @Mock private FeedbackReceiversService feedbackReceiversService;

    private EvaluationCycle cycle;

    private MockedStatic<UserContext> userContextMock;

    @BeforeEach
    void setup() {

        userContextMock = mockStatic(UserContext.class);
        userContextMock.when(() -> UserContext.getLoggedInUserOrganization())
                .thenReturn(Map.of(Constants.ID, "ORG1"));

        cycle = new EvaluationCycle();
        cycle.setId("C1");
        cycle.setOrganizationId("ORG1");
        cycle.setName("Cycle 1");
        cycle.setType(CycleType.ANNUAL);
        cycle.setFormDescription("Form");
        cycle.setStartDate(LocalDate.now());
        cycle.setEndDate(LocalDate.now().plusDays(5));
        cycle.setSelfEvalDeadline(LocalDate.now().plusDays(2));
        cycle.setFeedbackDeadline(LocalDate.now().plusDays(6));
        cycle.setStatus(CycleStatus.IN_PROGRESS);
    }

    @AfterEach
    void tearDown() {
        userContextMock.close();
    }

    @Test
    void testCreateCycleSuccess() {
        when(cycleRepo.save(any())).thenReturn(cycle);
        EvaluationCycle saved = service.createCycle(cycle);

        assertNotNull(saved);
        assertEquals("Cycle 1", saved.getName());
    }

    @Test
    void testCreateCycleInvalidDates() {
        cycle.setStartDate(LocalDate.now().plusDays(5));
        cycle.setEndDate(LocalDate.now());

        assertThrows(InvalidOperationException.class,
                () -> service.createCycle(cycle));
    }

    @Test
    void testCreateCycleWithQuestions() {
        EvaluationCycleCreateDto dto = new EvaluationCycleCreateDto();
        dto.setName("Cycle X");
        dto.setType(CycleType.ANNUAL);
        dto.setFormDescription("Desc");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(5));
        dto.setSelfEvalDeadline(LocalDate.now().plusDays(3));
        dto.setFeedbackDeadline(LocalDate.now().plusDays(5)); // must be >= endDate

        Question q1 = new Question("Q1", "D1", TargetType.SELF, true);
        dto.setQuestions(List.of(q1));

        Questionnaire q = new Questionnaire();
        q.setId("Q1");
        q.setQuestions(List.of(q1));

        when(questionnaireService.createQuestionnaire(any())).thenReturn(q);
        when(cycleRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EvaluationCycleDetailsDto res = service.createCycleWithQuestions(dto);

        assertEquals("Q1", res.getQuestionnaireId());
        assertEquals(1, res.getQuestions().size());
    }

    @Test
    void testGetAllCycles() {
        when(cycleRepo.findByOrganizationId("ORG1")).thenReturn(List.of(cycle));

        ReceiverDetails rd = new ReceiverDetails();
        rd.setProviderStatus(ProviderStatus.COMPLETED);

        ReceiverResponse rr = ReceiverResponse.builder()
                .receivers(List.of(rd))
                .build();

        when(feedbackReceiversService.getFeedbackReceiversList("C1", null))
                .thenReturn(rr);

        when(cycleRepo.saveAll(any())).thenReturn(List.of(cycle));

        List<EvaluationCycle> res = service.getAllCycles();

        assertEquals(1, res.size());
        assertEquals(CycleStatus.COMPLETED, res.get(0).getStatus());
    }

    @Test
    void testGetCycleByIdSuccess() {
        when(cycleRepo.findByIdAndOrganizationId("C1", "ORG1"))
                .thenReturn(Optional.of(cycle));

        EvaluationCycle res = service.getCycleById("C1");
        assertEquals("Cycle 1", res.getName());
    }

    @Test
    void testGetCycleByIdNotFound() {
        when(cycleRepo.findByIdAndOrganizationId("C1", "ORG1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getCycleById("C1"));
    }

    @Test
    void testUpdateCycleSuccess() {
        when(cycleRepo.findByIdAndOrganizationId("C1", "ORG1"))
                .thenReturn(Optional.of(cycle));

        when(cycleRepo.save(any())).thenReturn(cycle);

        cycle.setName("Updated");
        EvaluationCycle updated = service.updateCycle("C1", cycle);

        assertEquals("Updated", updated.getName());
    }

    @Test
    void testUpdateCompletedCycleThrows() {
        cycle.setStatus(CycleStatus.COMPLETED);
        when(cycleRepo.findByIdAndOrganizationId("C1", "ORG1"))
                .thenReturn(Optional.of(cycle));

        assertThrows(InvalidOperationException.class,
                () -> service.updateCycle("C1", cycle));
    }

    @Test
    void testUpdateCycleStatusSuccess() {
        when(cycleRepo.findByIdAndOrganizationId("C1", "ORG1"))
                .thenReturn(Optional.of(cycle));

        when(cycleRepo.save(any())).thenReturn(cycle);

        EvaluationCycle updated = service.updateCycleStatus("C1", CycleStatus.COMPLETED);

        assertEquals(CycleStatus.COMPLETED, updated.getStatus());
    }

    @Test
    void testInvalidCycleStatusTransition() {
        cycle.setStatus(CycleStatus.COMPLETED);
        when(cycleRepo.findByIdAndOrganizationId("C1", "ORG1"))
                .thenReturn(Optional.of(cycle));

        assertThrows(InvalidOperationException.class,
                () -> service.updateCycleStatus("C1", CycleStatus.IN_PROGRESS));
    }

    @Test
    void testDeleteCycleSuccess() {
        when(cycleRepo.findByIdAndOrganizationId("C1", "ORG1"))
                .thenReturn(Optional.of(cycle));

        Questionnaire q = new Questionnaire();
        q.setId("QID");
        q.setOrganizationId("ORG1");

        when(questionnaireService.getQuestionnaireById("QID"))
                .thenReturn(q);

        cycle.setQuestionnaireId("QID");

        assertDoesNotThrow(() -> service.deleteCycle("C1"));
        verify(cycleRepo, times(1)).deleteById("C1");
    }

    @Test
    void testGetCurrentActiveCycle() {
        when(cycleRepo.findByOrganizationIdAndStatusAndStartDateLessThanEqualAndFeedbackDeadlineGreaterThanEqual(
                eq("ORG1"), eq(CycleStatus.IN_PROGRESS), any(), any()
        )).thenReturn(Optional.of(cycle));

        EvaluationCycle res = service.getCurrentActiveCycle(CycleStatus.IN_PROGRESS);

        assertNotNull(res);
    }

    @Test
    void testGetCurrentActiveCycleNotFound() {
        when(cycleRepo.findByOrganizationIdAndStatusAndStartDateLessThanEqualAndFeedbackDeadlineGreaterThanEqual(
                eq("ORG1"), eq(CycleStatus.IN_PROGRESS), any(), any()
        )).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getCurrentActiveCycle(CycleStatus.IN_PROGRESS));
    }
}
