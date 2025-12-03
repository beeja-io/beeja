package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.enums.CycleType;
import com.beeja.api.performance_management.exceptions.BadRequestException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.*;
import com.beeja.api.performance_management.model.dto.*;
import com.beeja.api.performance_management.repository.EvaluationCycleRepository;
import com.beeja.api.performance_management.repository.FeedbackProviderRepository;
import com.beeja.api.performance_management.repository.FeedbackResponseRepository;
import com.beeja.api.performance_management.service.QuestionnaireService;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackResponseServiceImplTest {

    @InjectMocks
    private FeedbackResponseServiceImpl service;

    @Mock
    private FeedbackResponseRepository repository;

    @Mock
    private EvaluationCycleRepository cycleRepository;

    private MockedStatic<UserContext> userContextMock;

    private static final String ORG_ID = "ORG123";

    @BeforeEach
    void setup() {
        userContextMock = mockStatic(UserContext.class);
        userContextMock.when(UserContext::getLoggedInUserOrganization)
                .thenReturn(Map.of(Constants.ID, ORG_ID));
        userContextMock.when(UserContext::getLoggedInEmployeeId)
                .thenReturn("EMP456");
    }

    @AfterEach
    void teardown() {
        userContextMock.close();
    }

    @Test
    void getByFormId_ShouldReturnResponses() {
        FeedbackResponse resp = new FeedbackResponse();
        resp.setId("R1");

        when(repository.findByFormIdAndOrganizationId("FORM1", ORG_ID))
                .thenReturn(List.of(resp));

        List<FeedbackResponse> result = service.getByFormId("FORM1");

        assertEquals(1, result.size());
        assertEquals("R1", result.get(0).getId());
    }

    @Test
    void getByFormId_ShouldThrowBadRequest() {
        assertThrows(BadRequestException.class, () -> service.getByFormId(null));
    }

    @Test
    void getByEmployeeAndCycle_ShouldReturnResponses() {
        FeedbackResponse resp = new FeedbackResponse();
        resp.setId("R2");

        when(repository.findByEmployeeIdAndCycleIdAndOrganizationId("EMP456", "CYCLE1", ORG_ID))
                .thenReturn(List.of(resp));

        List<FeedbackResponse> result = service.getByEmployeeAndCycle("EMP456", "CYCLE1");

        assertEquals(1, result.size());
        assertEquals("R2", result.get(0).getId());
    }

    @Test
    void getByEmployeeAndCycle_ShouldThrowBadRequest() {
        assertThrows(BadRequestException.class, () -> service.getByEmployeeAndCycle(null, "CYCLE1"));
    }

    @Test
    void getByEmployee_ShouldReturnResponses() {
        FeedbackResponse resp = new FeedbackResponse();
        resp.setId("R3");

        when(repository.findByEmployeeIdAndOrganizationId("EMP456", ORG_ID))
                .thenReturn(List.of(resp));

        List<FeedbackResponse> result = service.getByEmployee("EMP456");

        assertEquals(1, result.size());
        assertEquals("R3", result.get(0).getId());
    }

    @Test
    void getByEmployee_ShouldThrowBadRequest() {
        assertThrows(BadRequestException.class, () -> service.getByEmployee(""));
    }

    @Test
    void getMyFeedbackForms_ShouldReturnForms() {
        FeedbackResponse resp = new FeedbackResponse();
        resp.setCycleId("CYCLE1");

        when(repository.findByEmployeeIdAndOrganizationId("EMP456", ORG_ID))
                .thenReturn(List.of(resp));
        when(cycleRepository.findByIdInAndOrganizationId(List.of("CYCLE1"), ORG_ID))
                .thenReturn(List.of());

        var result = service.getMyFeedbackForms();
        assertNotNull(result);
    }

    @Test
    void getMyResponsesByCycle_ShouldThrowNotFound() {
        when(repository.findByEmployeeIdAndCycleIdAndOrganizationId("EMP456", "CYCLE1", ORG_ID))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> service.getMyResponsesByCycle("CYCLE1"));
    }

    @Test
    void submitFeedback_ShouldThrowBadRequestWhenNull() {
        assertThrows(BadRequestException.class, () -> service.submitFeedback(null));
    }

    @Test
    void getGroupedResponsesWithCycleByEmployee_ShouldReturnGroupedResponses() {
        EvaluationCycle cycle = new EvaluationCycle();
        cycle.setId("CYCLE1");
        cycle.setName("Annual Cycle");
        cycle.setType(CycleType.ANNUAL);
        cycle.setFormDescription("Desc");
        cycle.setStartDate(LocalDate.now());
        cycle.setEndDate(LocalDate.now().plusDays(30));
        cycle.setFeedbackDeadline(LocalDate.now().plusDays(15));
        cycle.setSelfEvalDeadline(LocalDate.now().plusDays(10));
        cycle.setStatus(CycleStatus.IN_PROGRESS);

        QuestionAnswer qa = new QuestionAnswer();
        qa.setQuestionId("Q1");
        qa.setAnswer("Answer 1");

        FeedbackResponse resp = new FeedbackResponse();
        resp.setCycleId("CYCLE1");
        resp.setResponses(List.of(qa));

        when(repository.findByEmployeeIdAndOrganizationId("EMP456", ORG_ID))
                .thenReturn(List.of(resp));
        when(cycleRepository.findByIdAndOrganizationId("CYCLE1", ORG_ID))
                .thenReturn(Optional.of(cycle));

        EmployeeGroupedResponsesDTO dto = service.getGroupedResponsesWithCycleByEmployee("EMP456");

        assertEquals("CYCLE1", dto.getEvaluationCycle().getId());
        assertEquals(1, dto.getQuestions().size());
        assertEquals("Q1", dto.getQuestions().get(0).getQuestionId());
        assertEquals("Answer 1", dto.getQuestions().get(0).getResponses().get(0));
    }


    @Test
    void getGroupedResponsesWithCycleByEmployee_ShouldThrowNotFound() {
        when(repository.findByEmployeeIdAndOrganizationId("EMP456", ORG_ID))
                .thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getGroupedResponsesWithCycleByEmployee("EMP456"));
    }

    @Test
    void getResponsesForCycle_ShouldThrowNotFound() {
        when(cycleRepository.findByIdAndOrganizationId("CYCLE1", ORG_ID))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getResponsesForCycle("CYCLE1"));
    }

    @Test
    void getMyResponsesByCycle_ShouldReturnGroupedResponses() {
        EvaluationCycle cycle = new EvaluationCycle();
        cycle.setId("CYCLE1");
        cycle.setName("Annual");
        cycle.setType(CycleType.ANNUAL);
        cycle.setFormDescription("Desc");
        cycle.setStartDate(LocalDate.now());
        cycle.setEndDate(LocalDate.now().plusDays(10));
        cycle.setFeedbackDeadline(LocalDate.now().plusDays(5));
        cycle.setSelfEvalDeadline(LocalDate.now().plusDays(2));
        cycle.setStatus(CycleStatus.IN_PROGRESS);

        QuestionAnswer qa = new QuestionAnswer();
        qa.setQuestionId("Q1");
        qa.setAnswer("Answer 1");

        FeedbackResponse resp = new FeedbackResponse();
        resp.setCycleId("CYCLE1");
        resp.setResponses(List.of(qa));

        when(repository.findByEmployeeIdAndCycleIdAndOrganizationId("EMP456", "CYCLE1", ORG_ID))
                .thenReturn(List.of(resp));

        when(cycleRepository.findByIdAndOrganizationId("CYCLE1", ORG_ID))
                .thenReturn(Optional.of(cycle));

        EmployeeGroupedResponsesDTO dto = service.getMyResponsesByCycle("CYCLE1");

        assertNotNull(dto);
        assertEquals("CYCLE1", dto.getEvaluationCycle().getId());
        assertEquals(1, dto.getQuestions().size());
        assertEquals("Q1", dto.getQuestions().get(0).getQuestionId());
    }

    @Test
    void getMyFeedbackForms_ShouldReturnNonEmptyForms() {
        FeedbackResponse resp = new FeedbackResponse();
        resp.setCycleId("CYCLE1");

        EvaluationCycle cycle = new EvaluationCycle();
        cycle.setId("CYCLE1");

        when(repository.findByEmployeeIdAndOrganizationId("EMP456", ORG_ID))
                .thenReturn(List.of(resp));
        when(cycleRepository.findByIdInAndOrganizationId(List.of("CYCLE1"), ORG_ID))
                .thenReturn(List.of(cycle));

        var result = service.getMyFeedbackForms();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getGroupedResponsesWithCycleByEmployee_ShouldReturnEmptyQuestionsWhenNoResponses() {
        EvaluationCycle cycle = new EvaluationCycle();
        cycle.setId("CYCLE2");
        cycle.setName("Cycle 2");
        cycle.setType(CycleType.HALFYEARLY);
        cycle.setFormDescription("Desc");
        cycle.setStartDate(LocalDate.now());
        cycle.setEndDate(LocalDate.now().plusDays(10));
        cycle.setFeedbackDeadline(LocalDate.now().plusDays(5));
        cycle.setSelfEvalDeadline(LocalDate.now().plusDays(2));
        cycle.setStatus(CycleStatus.IN_PROGRESS);

        FeedbackResponse resp = new FeedbackResponse();
        resp.setCycleId("CYCLE2");
        resp.setResponses(Collections.emptyList());

        when(repository.findByEmployeeIdAndOrganizationId("EMP456", ORG_ID))
                .thenReturn(List.of(resp));
        when(cycleRepository.findByIdAndOrganizationId("CYCLE2", ORG_ID))
                .thenReturn(Optional.of(cycle));

        EmployeeGroupedResponsesDTO dto = service.getGroupedResponsesWithCycleByEmployee("EMP456");

        assertNotNull(dto);
        assertEquals("CYCLE2", dto.getEvaluationCycle().getId());
        assertEquals(0, dto.getQuestions().size());
    }

}
