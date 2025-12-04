package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.FeedbackProvider;
import com.beeja.api.performance_management.model.dto.*;
import com.beeja.api.performance_management.request.FeedbackProviderRequest;
import com.beeja.api.performance_management.response.FeedbackFormSummaryResponse;
import com.beeja.api.performance_management.response.FeedbackProviderDetails;
import com.beeja.api.performance_management.response.ReviewerAssignedEmployeesResponse;
import com.beeja.api.performance_management.service.FeedbackProvidersService;
import com.beeja.api.performance_management.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedbackProvidersControllerTest {

    @InjectMocks
    private FeedbackProvidersController controller;

    @Mock
    private FeedbackProvidersService feedbackProvidersService;

    private final String EMPLOYEE_ID = "E123";
    private final String CYCLE_ID = "C100";
    private final String REVIEWER_ID = "R200";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        UserContext.setLoggedInEmployeeId(REVIEWER_ID);

        UserContext.setLoggedInUserOrganization(Map.of("id", "ORG123"));
    }

    @Test
    void testAssignFeedbackProvider_Success() {
        FeedbackProviderRequest request = new FeedbackProviderRequest();
        FeedbackProvider provider = new FeedbackProvider();
        provider.setId("FP1");

        when(feedbackProvidersService.assignFeedbackProvider(eq(EMPLOYEE_ID), any()))
                .thenReturn(List.of(provider));

        ResponseEntity<List<FeedbackProvider>> response =
                controller.assignFeedbackProvider(EMPLOYEE_ID, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("FP1", response.getBody().get(0).getId());

        verify(feedbackProvidersService, times(1)).assignFeedbackProvider(eq(EMPLOYEE_ID), any());
    }

    @Test
    void testUpdateFeedbackProviders_Success() {
        FeedbackProviderRequest request = new FeedbackProviderRequest();
        FeedbackProvider provider = new FeedbackProvider();
        provider.setId("FP2");

        when(feedbackProvidersService.updateFeedbackProviders(eq(request), eq(EMPLOYEE_ID)))
                .thenReturn(List.of(provider));

        ResponseEntity<List<FeedbackProvider>> response =
                controller.updateFeedbackProviders(EMPLOYEE_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FP2", response.getBody().get(0).getId());

        verify(feedbackProvidersService, times(1)).updateFeedbackProviders(eq(request), eq(EMPLOYEE_ID));
    }

    @Test
    void testGetFeedbackFormDetails_Found() {
        FeedbackProviderDetails details = new FeedbackProviderDetails();
        details.setEmployeeId(EMPLOYEE_ID);

        when(feedbackProvidersService.getFeedbackFormDetails(EMPLOYEE_ID, CYCLE_ID, null))
                .thenReturn(details);

        ResponseEntity<FeedbackProviderDetails> response =
                controller.getFeedbackFormDetails(EMPLOYEE_ID, CYCLE_ID, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EMPLOYEE_ID, response.getBody().getEmployeeId());
    }

    @Test
    void testGetFeedbackFormDetails_NotFound() {
        when(feedbackProvidersService.getFeedbackFormDetails(EMPLOYEE_ID, CYCLE_ID, null))
                .thenReturn(null);

        ResponseEntity<FeedbackProviderDetails> response =
                controller.getFeedbackFormDetails(EMPLOYEE_ID, CYCLE_ID, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetEmployeesAssignedToReviewer_Found() {
        ReviewerAssignedEmployeesResponse responseDTO = new ReviewerAssignedEmployeesResponse();
        responseDTO.setReviewerId(REVIEWER_ID);
        responseDTO.setAssignedEmployees(List.of(new ReviewerEmployeeDTO()));

        when(feedbackProvidersService.getEmployeesAssignedToReviewer())
                .thenReturn(responseDTO);

        ResponseEntity<ReviewerAssignedEmployeesResponse> response =
                controller.getEmployeesAssignedToReviewer();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(REVIEWER_ID, response.getBody().getReviewerId());
    }

    @Test
    void testGetEmployeesAssignedToReviewer_NotFound() {
        ReviewerAssignedEmployeesResponse responseDTO = new ReviewerAssignedEmployeesResponse();
        responseDTO.setReviewerId(REVIEWER_ID);
        responseDTO.setAssignedEmployees(List.of());

        when(feedbackProvidersService.getEmployeesAssignedToReviewer())
                .thenReturn(responseDTO);

        ResponseEntity<ReviewerAssignedEmployeesResponse> response =
                controller.getEmployeesAssignedToReviewer();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetEmployeesAssignedToReviewer_Exception() {
        when(feedbackProvidersService.getEmployeesAssignedToReviewer())
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<ReviewerAssignedEmployeesResponse> response =
                controller.getEmployeesAssignedToReviewer();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetFormsByEmployee_Found() {
        FeedbackFormSummaryResponse summary = FeedbackFormSummaryResponse.builder()
                .cycleId("C1")
                .cycleName("Cycle 1")
                .status("IN_PROGRESS")
                .build();

        when(feedbackProvidersService.getFormsByEmployeeAndReviewer(EMPLOYEE_ID, REVIEWER_ID))
                .thenReturn(List.of(summary));

        ResponseEntity<?> response = controller.getFormsByEmployee(EMPLOYEE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        List<FeedbackFormSummaryResponse> bodyList = (List<FeedbackFormSummaryResponse>) response.getBody();
        assertEquals(1, bodyList.size());
        assertEquals("C1", bodyList.get(0).getCycleId());
        assertEquals("Cycle 1", bodyList.get(0).getCycleName());
    }

    @Test
    void testGetFormsByEmployee_NotFound() {
        when(feedbackProvidersService.getFormsByEmployeeAndReviewer(EMPLOYEE_ID, REVIEWER_ID))
                .thenReturn(List.of());

        ResponseEntity<?> response = controller.getFormsByEmployee(EMPLOYEE_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetFormsByEmployee_Exception() {
        when(feedbackProvidersService.getFormsByEmployeeAndReviewer(EMPLOYEE_ID, REVIEWER_ID))
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = controller.getFormsByEmployee(EMPLOYEE_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
