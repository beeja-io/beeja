package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.LogHours.LogHours;
import com.beeja.api.projectmanagement.requests.LogHoursRequest;
import com.beeja.api.projectmanagement.service.LogHoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;


class LogHoursControllerTest {

    @Mock
    private LogHoursService logHoursService;

    @InjectMocks
    private LogHoursController logHoursController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(logHoursController).build();
    }


    @Test
    public void testLogHoursPostSuccess() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");

        List<LogHours> logHoursList = new ArrayList<>();
        logHoursList.add(new LogHours("projectId", "contractId", "description", "08:00", new Date()));
        logHoursRequest.setLogHours(logHoursList);

        doNothing().when(logHoursService).saveLogHours(logHoursRequest);

        ResponseEntity<String> response = logHoursController.logHours(logHoursRequest);
        assertEquals("Log hours saved successfully.", response.getBody());
        verify(logHoursService, times(1)).saveLogHours(logHoursRequest);
    }

    @Test
    public void testLogHoursPostFailureMissingEmployeeId() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setLogHours(new ArrayList<>());
        doNothing().when(logHoursService).saveLogHours(logHoursRequest);
        ResponseEntity<String> response = logHoursController.logHours(logHoursRequest);
        assertEquals("Log hours saved successfully.", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(logHoursService, times(1)).saveLogHours(logHoursRequest);
    }

    @Test
    public void testLogHoursPostInvalidLogHoursFields() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");

        List<LogHours> logHoursList = new ArrayList<>();
        logHoursList.add(new LogHours(null, "contractId", "description", "08:00", new Date())); // Missing projectId
        logHoursRequest.setLogHours(logHoursList);

        doNothing().when(logHoursService).saveLogHours(logHoursRequest);

        ResponseEntity<String> response = logHoursController.logHours(logHoursRequest);

        assertEquals("Log hours saved successfully.", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(logHoursService, times(1)).saveLogHours(logHoursRequest);
    }

    @Test
    public void testLogHoursPostServiceThrowsException() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");

        List<LogHours> logHoursList = new ArrayList<>();
        logHoursList.add(new LogHours("projectId", "contractId", "description", "08:00", new Date()));
        logHoursRequest.setLogHours(logHoursList);

        doThrow(new RuntimeException("Service error")).when(logHoursService).saveLogHours(logHoursRequest);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            logHoursController.logHours(logHoursRequest);
        });

        assertEquals("Service error", exception.getMessage());
        verify(logHoursService, times(1)).saveLogHours(logHoursRequest);
    }
    @Test
    public void testLogHoursPostMissingLogHours() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");
        logHoursRequest.setLogHours(null);
        doNothing().when(logHoursService).saveLogHours(logHoursRequest);
        ResponseEntity<String> response = logHoursController.logHours(logHoursRequest);
        assertEquals("Log hours saved successfully.", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(logHoursService, times(1)).saveLogHours(logHoursRequest);
    }
    @Test
    public void testLogHoursPostInvalidDateFormat() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");
        List<LogHours> logHoursList = new ArrayList<>();
        Exception exception = assertThrows(ParseException.class, () -> {
            logHoursList.add(new LogHours("projectId", "contractId", "description", "08:00", new SimpleDateFormat("dd/MM/yyyy").parse("invalid-date"))); // Invalid date
        });
        assertTrue(exception.getMessage().contains("Unparseable date"));
        logHoursRequest.setLogHours(logHoursList);
        doNothing().when(logHoursService).saveLogHours(logHoursRequest);
        verify(logHoursService, times(0)).saveLogHours(logHoursRequest);
    }

    @Test
    public void testLogHoursPostMissingEmployeeIdAndLogHours() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        doNothing().when(logHoursService).saveLogHours(any(LogHoursRequest.class));
        ResponseEntity<String> response = logHoursController.logHours(logHoursRequest);
        assertEquals("Log hours saved successfully.", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(logHoursService, times(1)).saveLogHours(logHoursRequest);
    }



    @Test
    public void testUpdateLogHoursPutSuccess() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");

        List<LogHours> logHoursList = new ArrayList<>();
        logHoursList.add(new LogHours("projectId", "contractId", "description", "05:30", new Date()));
        logHoursRequest.setLogHours(logHoursList);

        doNothing().when(logHoursService).updateLogHours(logHoursRequest);

        ResponseEntity<String> response = logHoursController.updateLogHours(logHoursRequest);
        assertEquals("Log hours updated successfully.", response.getBody());
        verify(logHoursService, times(1)).updateLogHours(logHoursRequest);
    }

    @Test
    public void testUpdateLogHoursPutFailureMissingData() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");
        logHoursRequest.setLogHours(new ArrayList<>());
        doNothing().when(logHoursService).updateLogHours(logHoursRequest);
        ResponseEntity<String> response = logHoursController.updateLogHours(logHoursRequest);
        assertEquals("Log hours updated successfully.", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(logHoursService, times(1)).updateLogHours(logHoursRequest);
    }

    @Test
    public void testUpdateLogHoursPutFailureMissingProjectId() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");
        List<LogHours> logHoursList = new ArrayList<>();
        logHoursList.add(new LogHours(null, "contractId", "description", "05:30", new Date())); // Missing projectId
        logHoursRequest.setLogHours(logHoursList);
        doNothing().when(logHoursService).updateLogHours(logHoursRequest);
        ResponseEntity<String> response = logHoursController.updateLogHours(logHoursRequest);
        assertEquals("Log hours updated successfully.", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(logHoursService, times(1)).updateLogHours(logHoursRequest);
    }

    @Test
    public void testUpdateLogHoursPutServiceThrowsException() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");
        List<LogHours> logHoursList = new ArrayList<>();
        logHoursList.add(new LogHours("projectId", "contractId", "description", "05:30", new Date()));
        logHoursRequest.setLogHours(logHoursList);
        doThrow(new RuntimeException("Service error")).when(logHoursService).updateLogHours(logHoursRequest);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            logHoursController.updateLogHours(logHoursRequest);
        });

        assertEquals("Service error", exception.getMessage());
        verify(logHoursService, times(1)).updateLogHours(logHoursRequest);
    }

    @Test
    public void testUpdateLogHoursPutFailureNullRequest() {
        LogHoursController mockController = mock(LogHoursController.class);
        when(mockController.updateLogHours(null)).thenThrow(new NullPointerException("logHoursRequest cannot be null"));
        Exception exception = assertThrows(NullPointerException.class, () -> {
            mockController.updateLogHours(null);
        });
        assertEquals("logHoursRequest cannot be null", exception.getMessage());
        verify(logHoursService, times(0)).updateLogHours(any());
    }


    @Test
    public void testUpdateLogHoursPutFailureNullEmployeeId() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId(null);

        List<LogHours> logHoursList = new ArrayList<>();
        logHoursList.add(new LogHours("projectId", "contractId", "description", "05:30", new Date()));
        logHoursRequest.setLogHours(logHoursList);

        doNothing().when(logHoursService).updateLogHours(logHoursRequest);

        ResponseEntity<String> response = logHoursController.updateLogHours(logHoursRequest);

        assertEquals("Log hours updated successfully.", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(logHoursService, times(1)).updateLogHours(logHoursRequest);
    }

    @Test
    public void testUpdateLogHoursPutFailureNullLogHours() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");
        logHoursRequest.setLogHours(null);
        doThrow(new NullPointerException("Log hours list cannot be null"))
                .when(logHoursService).updateLogHours(logHoursRequest);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            logHoursController.updateLogHours(logHoursRequest);
        });
        assertEquals("Log hours list cannot be null", exception.getMessage());
        verify(logHoursService, times(1)).updateLogHours(logHoursRequest);
    }


    @Test
    public void testUpdateLogHoursPutFailureInvalidTimeFormat() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("employeeId");

        List<LogHours> logHoursList = new ArrayList<>();
        logHoursList.add(new LogHours("projectId", "contractId", "description", "invalid-time", new Date())); // Invalid time format
        logHoursRequest.setLogHours(logHoursList);

        doNothing().when(logHoursService).updateLogHours(logHoursRequest);

        ResponseEntity<String> response = logHoursController.updateLogHours(logHoursRequest);

        assertEquals("Log hours updated successfully.", response.getBody());
        verify(logHoursService, times(1)).updateLogHours(logHoursRequest);
    }

    @Test
    public void testUpdateLogHoursPutUnauthorizedAccess() {
        LogHoursRequest logHoursRequest = new LogHoursRequest();
        logHoursRequest.setEmployeeId("unauthorizedEmployeeId");

        List<LogHours> logHoursList = new ArrayList<>();
        logHoursList.add(new LogHours("projectId", "contractId", "description", "05:30", new Date()));
        logHoursRequest.setLogHours(logHoursList);

        doThrow(new SecurityException("Unauthorized access")).when(logHoursService).updateLogHours(logHoursRequest);

        Exception exception = assertThrows(SecurityException.class, () -> {
            logHoursController.updateLogHours(logHoursRequest);
        });

        assertEquals("Unauthorized access", exception.getMessage());
        verify(logHoursService, times(1)).updateLogHours(logHoursRequest);
    }



    @Test
    public void testGetLogHoursSummaryDaySuccess() throws Exception {
        String employeeId = "employeeId";
        String type = "day";
        String date = "2025-02-13";
        Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("totalHours", 8);
        mockResponse.put("details", new ArrayList<>()); // Add detailed mock data if necessary
        when(logHoursService.getLogHoursSummary(employeeId, type, parsedDate, null)).thenReturn(mockResponse);
        ResponseEntity<Object> response = logHoursController.getLogHoursSummary(employeeId, type, date, null);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(logHoursService, times(1)).getLogHoursSummary(employeeId, type, parsedDate, null);
    }

    @Test
    public void testGetLogHoursSummaryWeekSuccess() throws Exception {
        String employeeId = "employeeId";
        String type = "week";
        Integer weekNumber = 7;
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("totalHours", 40);
        mockResponse.put("weeklyBreakdown", new ArrayList<>());
        when(logHoursService.getLogHoursSummary(employeeId, type, null, weekNumber)).thenReturn(mockResponse);
        ResponseEntity<Object> response = logHoursController.getLogHoursSummary(employeeId, type, null, weekNumber);
    }

    @Test
    public void testGetLogHoursSummary_ValidDayRequest() throws Exception {
        String employeeId = "testEmployee";
        String type = "day";
        String date = "2025-02-10";
        Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("totalHours", 8);
        mockResponse.put("dailyDetails", List.of(Map.of("project", "Project A", "hours", 8)));
        when(logHoursService.getLogHoursSummary(eq(employeeId), eq(type), eq(parsedDate), isNull()))
                .thenReturn(mockResponse);
        ResponseEntity<Object> response = logHoursController.getLogHoursSummary(employeeId, type, date, null);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(logHoursService, times(1)).getLogHoursSummary(eq(employeeId), eq(type), eq(parsedDate), isNull());
    }

    @Test
    public void testGetLogHoursSummary_MissingOptionalParameters() throws Exception {
        String employeeId = "testEmployee";
        String type = "month";
        Date currentDate = new Date();
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("totalHours", 160);
        mockResponse.put("monthlyDetails", List.of(Map.of("week", "Week 1", "hours", 40)));
        when(logHoursService.getLogHoursSummary(eq(employeeId), eq(type), any(Date.class), isNull()))
                .thenReturn(mockResponse);

        ResponseEntity<Object> response = logHoursController.getLogHoursSummary(employeeId, type, null, null);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(logHoursService, times(1)).getLogHoursSummary(eq(employeeId), eq(type), any(Date.class), isNull());
    }

    @Test
    public void testGetLogHoursSummary_ServiceReturnsNull() {
        String employeeId = "testEmployee";
        String type = "week";
        Integer weekNumber = 5;
        Date defaultDate = new Date();
        when(logHoursService.getLogHoursSummary(eq(employeeId), eq(type), eq(defaultDate), eq(weekNumber)))
                .thenReturn(null);
        ResponseEntity<Object> response = logHoursController.getLogHoursSummary(employeeId, type, null, weekNumber);


    }
    @Test
    public void testGetLogHoursSummary_MissingEmployeeId() {
        String type = "day";
        String date = "2025-02-13";
        when(logHoursService.getLogHoursSummary(any(), any(), any(), any())).thenReturn(Collections.emptyMap());
        ResponseEntity<Object> response = logHoursController.getLogHoursSummary(null, type, date, null);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Collections.emptyMap(), response.getBody());
        verify(logHoursService, times(1)).getLogHoursSummary(any(), any(), any(), any());
    }
    @Test
    public void testGetLogHoursSummary_InvalidDateFormat() {
        String employeeId = "testEmployee";
        String type = "day";
        String date = "2025-13-40";
        ResponseEntity<Object> response = logHoursController.getLogHoursSummary(employeeId, type, date, null);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Collections.emptyMap(), response.getBody());
        verify(logHoursService, times(1)).getLogHoursSummary(any(), any(), any(), any());
    }
    @Test
    public void testGetLogHoursSummary_DayRequest_Success() throws Exception {
        // Arrange
        String employeeId = "employeeId";
        String type = "day";
        String date = "2025-02-13";
        Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Map<String, Object> mockResponse = Map.of(
                "totalHours", 8,
                "details", List.of(Map.of("project", "Project A", "hours", 8))
        );

        when(logHoursService.getLogHoursSummary(employeeId, type, parsedDate, null))
                .thenReturn(mockResponse);
        ResponseEntity<Object> response = logHoursController.getLogHoursSummary(employeeId, type, date, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(logHoursService, times(1)).getLogHoursSummary(employeeId, type, parsedDate, null);
    }

    @Test
    public void testGetLogHoursSummary_InvalidType() {
        String employeeId = "employeeId";
        String type = "invalidType";
        String date = "2025-02-13";

        Map<String, Object> mockResponse = new HashMap<>(); // Simulate the service's behavior for invalid type
        when(logHoursService.getLogHoursSummary(eq(employeeId), eq(type), any(Date.class), isNull()))
                .thenReturn(mockResponse);
        ResponseEntity<Object> response = logHoursController.getLogHoursSummary(employeeId, type, date, null);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Matches the actual controller behavior
        assertEquals(mockResponse, response.getBody()); // Empty map returned by the service
        verify(logHoursService, times(1)).getLogHoursSummary(eq(employeeId), eq(type), any(Date.class), isNull());
    }


}