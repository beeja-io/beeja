package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.client.AccountClient;
import com.beeja.api.performance_management.model.*;
import com.beeja.api.performance_management.model.dto.*;
import com.beeja.api.performance_management.service.FeedbackResponseService;
import com.beeja.api.performance_management.service.MyTeamOverviewService;
import com.beeja.api.performance_management.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyTeamOverviewControllerTest {

    @InjectMocks
    private MyTeamOverviewController controller;

    @Mock
    private FeedbackResponseService responseService;

    @Mock
    private MyTeamOverviewService myTeamOverviewService;

    private static final String EMP_ID = "EMP1";
    private static final String CYCLE_ID = "CYCLE1";

    @BeforeEach
    void setup() {
    }

    @Test
    void getEmployeePerformanceData_ShouldReturnPaginatedResponse() {
        PaginatedEmployeePerformanceResponse mockResponse = new PaginatedEmployeePerformanceResponse();
        mockResponse.setTotalRecords(1);
        mockResponse.setPageNumber(1);
        mockResponse.setPageSize(10);
        mockResponse.setTotalPages(1);
        mockResponse.setData(Collections.emptyList());

        when(myTeamOverviewService.getEmployeePerformanceData(
                any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(mockResponse);

        ResponseEntity<PaginatedEmployeePerformanceResponse> response =
                controller.getEmployeePerformanceData(null, null, null, null, 1, 10);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void getGroupedResponsesByEmployeeCycle_ShouldReturnEmptyWhenNoResponses() {
        when(responseService.getByEmployeeAndCycle(EMP_ID, CYCLE_ID)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controller.getGroupedResponsesByEmployeeCycle(EMP_ID, CYCLE_ID);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof GroupedFeedbackResponse);

        GroupedFeedbackResponse body = (GroupedFeedbackResponse) response.getBody();
        assertNotNull(body.getQuestions());
        assertTrue(body.getQuestions().isEmpty());
    }

    @Test
    void createOrUpdateOverallRating_ShouldReturnSavedRating() {
        OverallRatingRequestDTO request = new OverallRatingRequestDTO();
        request.setRating(4.5);
        request.setComments("Good work");

        OverallRating saved = new OverallRating();
        saved.setEmployeeId(EMP_ID);
        saved.setRating(4.5);
        saved.setComments("Good work");

        when(myTeamOverviewService.createOrUpdateOverallRating(eq(EMP_ID), eq(4.5), eq("Good work")))
                .thenReturn(saved);

        ResponseEntity<?> response = controller.createOrUpdateOverallRating(EMP_ID, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(saved, response.getBody());
    }

    @Test
    void getOverallRating_ShouldReturnRating() {
        OverallRating rating = new OverallRating();
        rating.setEmployeeId(EMP_ID);
        rating.setRating(4.0);
        rating.setComments("Excellent");

        when(myTeamOverviewService.getOverallRatingByEmployeeId(EMP_ID)).thenReturn(rating);

        ResponseEntity<?> response = controller.getOverallRating(EMP_ID);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(rating, response.getBody());
    }

    @Test
    void deleteOverallRating_ShouldReturnNoContent() {
        doNothing().when(myTeamOverviewService).deleteOverallRatingByEmployeeId(EMP_ID);

        ResponseEntity<?> response = controller.deleteOverallRating(EMP_ID);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void getCycleInfoByEmployeeId_ShouldReturnCycleList() {
        List<EmployeeCycleInfo> cycles = List.of(
                new EmployeeCycleInfo(EMP_ID, CYCLE_ID, "Annual Cycle")
        );
        when(myTeamOverviewService.getCycleIdsByEmployeeId(EMP_ID)).thenReturn(cycles);

        ResponseEntity<?> response = controller.getCycleInfoByEmployeeId(EMP_ID);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cycles, response.getBody());
    }

    @Test
    void getGroupedResponsesByEmployeeCycle_ShouldReturnEmpty_WhenResponsesNull() {
        when(responseService.getByEmployeeAndCycle(EMP_ID, CYCLE_ID)).thenReturn(null);

        ResponseEntity<?> response = controller.getGroupedResponsesByEmployeeCycle(EMP_ID, CYCLE_ID);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof GroupedFeedbackResponse);
        assertTrue(((GroupedFeedbackResponse) response.getBody()).getQuestions().isEmpty());
    }

    @Test
    void getGroupedResponsesByEmployeeCycle_ShouldHandleServiceException() {
        when(responseService.getByEmployeeAndCycle(EMP_ID, CYCLE_ID)).thenThrow(new RuntimeException("Service failed"));

        ResponseEntity<?> response = controller.getGroupedResponsesByEmployeeCycle(EMP_ID, CYCLE_ID);

        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void createOrUpdateOverallRating_ShouldHandleServiceException() {
        OverallRatingRequestDTO request = new OverallRatingRequestDTO();
        request.setRating(4.0);
        request.setComments("Test");

        when(myTeamOverviewService.createOrUpdateOverallRating(eq(EMP_ID), anyDouble(), anyString()))
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = controller.createOrUpdateOverallRating(EMP_ID, request);

        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error saving feedback response", response.getBody());
    }

    @Test
    void getOverallRating_ShouldHandleServiceException() {
        when(myTeamOverviewService.getOverallRatingByEmployeeId(EMP_ID))
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = controller.getOverallRating(EMP_ID);

        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals(Constants.NO_RATINGS_FOUND_FOR_EMPLOYEE, response.getBody());
    }

    @Test
    void deleteOverallRating_ShouldHandleServiceException() {
        doThrow(new RuntimeException("DB error"))
                .when(myTeamOverviewService).deleteOverallRatingByEmployeeId(EMP_ID);

        ResponseEntity<?> response = controller.deleteOverallRating(EMP_ID);

        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals(Constants.FINAL_RATING_NOT_FOUND, response.getBody());
    }

    @Test
    void getCycleInfoByEmployeeId_ShouldHandleServiceException() {
        when(myTeamOverviewService.getCycleIdsByEmployeeId(EMP_ID))
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = controller.getCycleInfoByEmployeeId(EMP_ID);

        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals(Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND, response.getBody());
    }

    @Test
    void deleteOverallRating_ShouldReturnNoContentBody() {
        doNothing().when(myTeamOverviewService).deleteOverallRatingByEmployeeId(EMP_ID);

        ResponseEntity<?> response = controller.deleteOverallRating(EMP_ID);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getCycleInfoByEmployeeId_ShouldReturnEmptyList() {
        when(myTeamOverviewService.getCycleIdsByEmployeeId(EMP_ID))
                .thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controller.getCycleInfoByEmployeeId(EMP_ID);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

}
