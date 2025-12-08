package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.FeedbackResponse;
import com.beeja.api.performance_management.model.dto.*;
import com.beeja.api.performance_management.response.MyFeedbackFormResponse;
import com.beeja.api.performance_management.service.FeedbackResponseService;
import com.beeja.api.performance_management.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedbackResponseControllerTest {

    @InjectMocks
    private FeedbackResponseController controller;

    @Mock
    private FeedbackResponseService responseService;

    private final String EMPLOYEE_ID = "E123";
    private final String CYCLE_ID = "C100";
    private final String FORM_ID = "F200";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        UserContext.setLoggedInEmployeeId(EMPLOYEE_ID);
        UserContext.setLoggedInUserOrganization(java.util.Map.of("id", "ORG123"));
    }

    @Test
    void testSubmitFeedback_Success() {
        SubmitFeedbackRequest req = new SubmitFeedbackRequest();
        FeedbackResponse resp = new FeedbackResponse();
        resp.setId("FR1");

        when(responseService.submitFeedback(req)).thenReturn(resp);

        ResponseEntity<FeedbackResponse> response = controller.submitFeedback(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FR1", response.getBody().getId());
        verify(responseService, times(1)).submitFeedback(req);
    }

    @Test
    void testGetResponsesByEmployeeCycle_Success() {
        FeedbackResponse resp = new FeedbackResponse();
        resp.setId("FR2");

        when(responseService.getByEmployeeAndCycle(EMPLOYEE_ID, CYCLE_ID))
                .thenReturn(List.of(resp));

        ResponseEntity<List<FeedbackResponse>> response =
                controller.getResponsesByEmployeeCycle(EMPLOYEE_ID, CYCLE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("FR2", response.getBody().get(0).getId());
    }

    @Test
    void testGetResponsesForForm_Success() {
        FeedbackResponse resp = new FeedbackResponse();
        resp.setId("FR3");

        when(responseService.getByFormId(FORM_ID))
                .thenReturn(List.of(resp));

        ResponseEntity<List<FeedbackResponse>> response =
                controller.getResponsesForForm(FORM_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FR3", response.getBody().get(0).getId());
    }

    @Test
    void testGetResponsesByEmployee_Success() {
        FeedbackResponse resp = new FeedbackResponse();
        resp.setId("FR4");

        when(responseService.getByEmployee(EMPLOYEE_ID)).thenReturn(List.of(resp));

        ResponseEntity<List<FeedbackResponse>> response =
                controller.getResponsesByEmployee(EMPLOYEE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("FR4", response.getBody().get(0).getId());
    }

    @Test
    void testGetGroupedResponsesByEmployee_Success() {
        EmployeeGroupedResponsesDTO dto = new EmployeeGroupedResponsesDTO();
        dto.setEvaluationCycle(null);
        dto.setQuestions(List.of());

        when(responseService.getGroupedResponsesWithCycleByEmployee(EMPLOYEE_ID))
                .thenReturn(dto);

        ResponseEntity<EmployeeGroupedResponsesDTO> response =
                controller.getGroupedResponsesByEmployee(EMPLOYEE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetResponsesForCycle_Success() {
        CycleWithResponsesDTO dto = new CycleWithResponsesDTO();
        dto.setEvaluationCycle(null);

        when(responseService.getResponsesForCycle(CYCLE_ID)).thenReturn(dto);

        ResponseEntity<CycleWithResponsesDTO> response =
                controller.getResponsesForCycle(CYCLE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetMyFeedbackForms_Success() {
        MyFeedbackFormResponse form = new MyFeedbackFormResponse();
        form.setCycleId(CYCLE_ID);

        when(responseService.getMyFeedbackForms()).thenReturn(List.of(form));

        ResponseEntity<List<MyFeedbackFormResponse>> response =
                controller.getMyFeedbackForms();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(CYCLE_ID, response.getBody().get(0).getCycleId());
    }

    @Test
    void testGetMyResponsesByCycle_Success() {
        EmployeeGroupedResponsesDTO dto = new EmployeeGroupedResponsesDTO();
        dto.setEvaluationCycle(null);

        when(responseService.getMyResponsesByCycle(CYCLE_ID)).thenReturn(dto);

        ResponseEntity<EmployeeGroupedResponsesDTO> response =
                controller.getMyResponsesByCycle(CYCLE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
