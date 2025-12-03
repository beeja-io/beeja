package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.QuestionAnswer;
import com.beeja.api.performance_management.model.SelfEvaluation;
import com.beeja.api.performance_management.model.dto.SelfEvaluationRequest;
import com.beeja.api.performance_management.service.SelfEvaluationService;
import com.beeja.api.performance_management.utils.UserContext;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SelfEvalControllerTest {

    @Mock
    private SelfEvaluationService selfService;

    @InjectMocks
    private SelfEvalController controller;

    private MockedStatic<UserContext> userContextMock;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        userContextMock = mockStatic(UserContext.class);
    }

    @AfterEach
    void close() {
        userContextMock.close();
    }

    @Test
    void testSubmitSelfEval_Success() {
        SelfEvaluationRequest req = new SelfEvaluationRequest();
        req.setEmployeeId("EMP1");
        req.setSubmittedBy("EMP1");

        QuestionAnswer qa = new QuestionAnswer();
        qa.setQuestionId("Q1");
        qa.setAnswer("Test");
        req.setResponses(List.of(qa));

        SelfEvaluation saved = new SelfEvaluation();
        saved.setEmployeeId("EMP1");
        saved.setSubmittedBy("EMP1");
        saved.setResponses(List.of(qa));

        when(selfService.submitSelfEvaluation(any(SelfEvaluation.class))).thenReturn(saved);

        ResponseEntity<SelfEvaluation> response = controller.submitSelfEval(req);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("EMP1", response.getBody().getEmployeeId());
        verify(selfService, times(1)).submitSelfEvaluation(any(SelfEvaluation.class));
    }

    @Test
    void testGetSelfEvals_Success() {
        SelfEvaluation se = new SelfEvaluation();
        se.setEmployeeId("EMP1");

        when(selfService.getByEmployee("EMP1")).thenReturn(List.of(se));

        ResponseEntity<List<SelfEvaluation>> response = controller.getSelfEvals("EMP1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetSelfEvalsForLoggedInEmployee_Success() {
        userContextMock.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");

        SelfEvaluation se = new SelfEvaluation();
        se.setEmployeeId("EMP1");

        when(selfService.getByEmployee("EMP1")).thenReturn(List.of(se));

        ResponseEntity<List<SelfEvaluation>> response = controller.getSelfEvalsForLoggedInEmployee();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(selfService, times(1)).getByEmployee("EMP1");
    }

    @Test
    void testGetSelfEvalsForLoggedInEmployee_NoEmployeeId_ShouldThrow() {
        userContextMock.when(UserContext::getLoggedInEmployeeId).thenReturn(null);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.getSelfEvalsForLoggedInEmployee()
        );

        assertEquals(401, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Employee not found"));
    }

    @Test
    void testGetSelfEvalsForLoggedInEmployee_BlankEmployeeId_ShouldThrow() {
        userContextMock.when(UserContext::getLoggedInEmployeeId).thenReturn("  ");

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.getSelfEvalsForLoggedInEmployee()
        );

        assertEquals(401, ex.getStatusCode().value());
    }
}
