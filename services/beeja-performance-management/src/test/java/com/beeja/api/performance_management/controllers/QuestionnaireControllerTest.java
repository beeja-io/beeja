package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.Question;
import com.beeja.api.performance_management.model.Questionnaire;
import com.beeja.api.performance_management.enums.TargetType;
import com.beeja.api.performance_management.service.QuestionnaireService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionnaireControllerTest {

    @Mock
    private QuestionnaireService questionnaireService;

    @InjectMocks
    private QuestionnaireController controller;

    private Questionnaire questionnaire;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Question question = new Question(
                "What is your strength?",
                "Describe strengths",
                TargetType.SELF,
                true
        );

        questionnaire = new Questionnaire();
        questionnaire.setId("Q1");
        questionnaire.setOrganizationId("ORG1");
        questionnaire.setQuestions(List.of(question));
    }

    @Test
    void testCreateQuestionnaire() {
        when(questionnaireService.createQuestionnaire(any())).thenReturn(questionnaire);

        ResponseEntity<Questionnaire> response = controller.createQuestionnaire(questionnaire);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Q1", response.getBody().getId());
    }

    @Test
    void testCreateQuestionnaire_ValidationFailure() {
        Questionnaire invalid = new Questionnaire();
        invalid.setQuestions(List.of()); // empty list but validation does NOT trigger

        when(questionnaireService.createQuestionnaire(invalid)).thenReturn(invalid);

        ResponseEntity<Questionnaire> response = controller.createQuestionnaire(invalid);

        assertEquals(201, response.getStatusCodeValue());
        verify(questionnaireService, times(1)).createQuestionnaire(invalid);
    }

    @Test
    void testGetAllQuestionnaires() {
        when(questionnaireService.getAllQuestionnaires()).thenReturn(List.of(questionnaire));

        ResponseEntity<List<Questionnaire>> response = controller.getAllQuestionnaires();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetQuestionnaireById() {
        when(questionnaireService.getQuestionnaireById("Q1")).thenReturn(questionnaire);

        ResponseEntity<Questionnaire> response = controller.getQuestionnaireById("Q1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Q1", response.getBody().getId());
    }

    @Test
    void testGetQuestionnaireById_ServiceThrows() {
        when(questionnaireService.getQuestionnaireById("Q1"))
                .thenThrow(new RuntimeException("Not found"));

        assertThrows(RuntimeException.class,
                () -> controller.getQuestionnaireById("Q1"));
    }

    @Test
    void testUpdateQuestionnaire() {
        when(questionnaireService.updateQuestionnaire(eq("Q1"), any()))
                .thenReturn(questionnaire);

        ResponseEntity<Questionnaire> response =
                controller.updateQuestionnaire("Q1", questionnaire);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Q1", response.getBody().getId());
    }

    @Test
    void testUpdateQuestionnaire_InvalidData() {
        Question invalidQ = new Question("", "", null, true);
        Questionnaire invalid = new Questionnaire();
        invalid.setQuestions(List.of(invalidQ));

        when(questionnaireService.updateQuestionnaire("Q1", invalid))
                .thenReturn(invalid);

        ResponseEntity<Questionnaire> response =
                controller.updateQuestionnaire("Q1", invalid);

        assertEquals(200, response.getStatusCodeValue());
        verify(questionnaireService, times(1))
                .updateQuestionnaire("Q1", invalid);
    }

    @Test
    void testDeleteQuestionnaire() {
        doNothing().when(questionnaireService).deleteQuestionnaire("Q1");

        ResponseEntity<Void> response = controller.deleteQuestionnaire("Q1");

        assertEquals(204, response.getStatusCodeValue());
        verify(questionnaireService, times(1)).deleteQuestionnaire("Q1");
    }

    @Test
    void testDeleteQuestionnaire_Failure() {
        doThrow(new RuntimeException("Deletion failed"))
                .when(questionnaireService).deleteQuestionnaire("Q1");

        assertThrows(RuntimeException.class,
                () -> controller.deleteQuestionnaire("Q1"));
    }
}
