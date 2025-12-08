package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.TargetType;
import com.beeja.api.performance_management.exceptions.BadRequestException;
import com.beeja.api.performance_management.exceptions.DuplicateDataException;
import com.beeja.api.performance_management.exceptions.InvalidOperationException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.Question;
import com.beeja.api.performance_management.model.Questionnaire;
import com.beeja.api.performance_management.repository.QuestionnaireRepository;
import com.beeja.api.performance_management.utils.UserContext;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireServiceImplTest {

    @InjectMocks
    private QuestionnaireServiceImpl service;

    @Mock
    private QuestionnaireRepository questionnaireRepository;

    private static MockedStatic<UserContext> userContextMock;

    Questionnaire questionnaire;
    Question question;

    @BeforeAll
    static void init() {
        userContextMock = mockStatic(UserContext.class);
    }

    @AfterAll
    static void close() {
        userContextMock.close();
    }

    @BeforeEach
    void setup() {
        Map<String, Object> org = Map.of("id", "ORG1");
        userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(org);

        question = new Question("Q1", "Desc", TargetType.SELF, false);

        questionnaire = new Questionnaire();
        questionnaire.setId("Q1");
        questionnaire.setQuestions(List.of(question));
        questionnaire.setOrganizationId("ORG1");
    }

    @Test
    void createQuestionnaire_Success_ShouldSetRequiredForSelfTarget() {
        when(questionnaireRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Questionnaire result = service.createQuestionnaire(questionnaire);

        assertTrue(result.getQuestions().get(0).isRequired(), "SELF target should be auto-required");
        verify(questionnaireRepository).save(any());
    }

    @Test
    void createQuestionnaire_EmptyQuestions_ShouldThrowBadRequest() {
        questionnaire.setQuestions(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> service.createQuestionnaire(questionnaire));
    }

    @Test
    void createQuestionnaire_RepositoryError_ShouldThrowInvalidOperation() {
        when(questionnaireRepository.save(any())).thenThrow(new RuntimeException("DB"));

        assertThrows(InvalidOperationException.class, () -> service.createQuestionnaire(questionnaire));
    }

    @Test
    void getAllQuestionnaires_ShouldReturnList() {
        when(questionnaireRepository.findByOrganizationId(eq("ORG1"), any(Sort.class)))
                .thenReturn(List.of(questionnaire));

        List<Questionnaire> result = service.getAllQuestionnaires();

        assertEquals(1, result.size());
    }

    @Test
    void getQuestionnairesByDepartment_ShouldCallGetAll() {
        when(questionnaireRepository.findByOrganizationId(eq("ORG1"), any()))
                .thenReturn(List.of(questionnaire));

        List<Questionnaire> result = service.getQuestionnairesByDepartment("ANY");

        assertEquals(1, result.size());
    }

    @Test
    void getQuestionnaireById_Success() {
        when(questionnaireRepository.findById("Q1")).thenReturn(Optional.of(questionnaire));

        Questionnaire result = service.getQuestionnaireById("Q1");

        assertEquals("Q1", result.getId());
    }

    @Test
    void getQuestionnaireById_NotFound_ShouldThrow() {
        when(questionnaireRepository.findById("X")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getQuestionnaireById("X"));
    }

    @Test
    void updateQuestionnaire_Success() {
        when(questionnaireRepository.findById("Q1")).thenReturn(Optional.of(questionnaire));
        when(questionnaireRepository.findAll()).thenReturn(List.of());
        when(questionnaireRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Questionnaire updated = new Questionnaire();
        updated.setQuestions(List.of(question));

        Questionnaire result = service.updateQuestionnaire("Q1", updated);

        assertEquals(1, result.getQuestions().size());
    }

    @Test
    void updateQuestionnaire_EmptyQuestions_ShouldThrowBadRequest() {
        when(questionnaireRepository.findById("Q1")).thenReturn(Optional.of(questionnaire));

        Questionnaire invalid = new Questionnaire();
        invalid.setQuestions(List.of());

        assertThrows(BadRequestException.class, () -> service.updateQuestionnaire("Q1", invalid));
    }

    @Test
    void updateQuestionnaire_DuplicateFound_ShouldThrow() {
        when(questionnaireRepository.findById("Q1")).thenReturn(Optional.of(questionnaire));

        Questionnaire duplicate = new Questionnaire();
        duplicate.setId("Q2");
        duplicate.setQuestions(questionnaire.getQuestions());

        when(questionnaireRepository.findAll()).thenReturn(List.of(duplicate));

        Questionnaire newQ = new Questionnaire();
        newQ.setQuestions(questionnaire.getQuestions());

        assertThrows(DuplicateDataException.class,
                () -> service.updateQuestionnaire("Q1", newQ));
    }


    @Test
    void updateQuestionnaire_RepositoryError_ShouldThrow() {
        when(questionnaireRepository.findById("Q1")).thenReturn(Optional.of(questionnaire));
        when(questionnaireRepository.findAll()).thenReturn(List.of());
        when(questionnaireRepository.save(any())).thenThrow(new RuntimeException("DB"));

        Questionnaire newQ = new Questionnaire();
        newQ.setQuestions(List.of(question));

        assertThrows(InvalidOperationException.class, () -> service.updateQuestionnaire("Q1", newQ));
    }

    @Test
    void updateQuestions_Success() {
        when(questionnaireRepository.findById("Q1")).thenReturn(Optional.of(questionnaire));
        when(questionnaireRepository.save(any())).thenReturn(questionnaire);

        Questionnaire result = service.updateQuestions("Q1", List.of(question));

        assertEquals(1, result.getQuestions().size());
    }

    @Test
    void updateQuestions_EmptyList_ShouldThrow() {
        when(questionnaireRepository.findById("Q1")).thenReturn(Optional.of(questionnaire));

        assertThrows(InvalidOperationException.class, () -> service.updateQuestions("Q1", List.of()));
    }

    @Test
    void deleteQuestionnaire_Success() {
        when(questionnaireRepository.existsById("Q1")).thenReturn(true);

        assertDoesNotThrow(() -> service.deleteQuestionnaire("Q1"));
        verify(questionnaireRepository).deleteById("Q1");
    }

    @Test
    void deleteQuestionnaire_NotFound_ShouldThrow() {
        when(questionnaireRepository.existsById("X")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteQuestionnaire("X"));
    }
}
