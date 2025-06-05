package com.beeja.api.projectmanagement.controllers;


import com.beeja.api.projectmanagement.model.CustomPageResponse;
import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.service.TimesheetService;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimesheetControllerTest {

    @Mock
    private TimesheetService timesheetService;

    @InjectMocks
    private TimesheetController timesheetController;

    private TimesheetRequestDto timesheetRequestDto;
    private Timesheet timesheet;
    private MockedStatic<UserContext> mockedUserContext;

    @BeforeEach
    void setUp() {
        timesheetRequestDto = TimesheetRequestDto.builder()
                .clientId("client001")
                .projectId("project001")
                .contractId("contract001")
                .startDate(new Date())
                .timeInMinutes(60)
                .description("Working on feature X")
                .build();

        timesheet = Timesheet.builder()
                .id("ts123")
                .employeeId("emp123")
                .organizationId("org456")
                .clientId("client001")
                .projectId("project001")
                .contractId("contract001")
                .startDate(new Date())
                .timeInMinutes(60)
                .description("Working on feature X")
                .createdAt(new Date())
                .createdBy("emp123")
                .build();

        mockedUserContext = Mockito.mockStatic(UserContext.class);
        mockedUserContext.when(UserContext::getLoggedInEmployeeId).thenReturn("emp123");
        mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org456"));
        mockedUserContext.when(() -> UserContext.hasPermission(anyString())).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        mockedUserContext.close();
    }

    @Test
    void saveTimesheet_ReturnsCreatedStatusAndTimesheet() {
        when(timesheetService.saveTimesheet(any(TimesheetRequestDto.class))).thenReturn(timesheet);

        ResponseEntity<Timesheet> response = timesheetController.saveTimesheet(timesheetRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(timesheet, response.getBody());
        verify(timesheetService, times(1)).saveTimesheet(timesheetRequestDto);
    }

    @Test
    void updateLog_ReturnsUpdatedTimesheet() {
        String timesheetId = "ts123";
        Timesheet updatedTimesheet = Timesheet.builder()
                .id(timesheetId)
                .clientId("newClient")
                .projectId("newProject")
                .contractId("newContract")
                .timeInMinutes(90)
                .description("updated description")
                .build();

        when(timesheetService.updateLog(any(TimesheetRequestDto.class), eq(timesheetId))).thenReturn(updatedTimesheet);

        Timesheet result = timesheetController.updateLog(timesheetRequestDto, timesheetId);

        assertNotNull(result);
        assertEquals(updatedTimesheet.getId(), result.getId());
        assertEquals(updatedTimesheet.getDescription(), result.getDescription());
        verify(timesheetService, times(1)).updateLog(timesheetRequestDto, timesheetId);
    }

    @Test
    void getTimesheets_FilterByMonth_ReturnsGroupedData() {
        String month = "2025-05";
        Map<String, Object> groupedData = new HashMap<>();
        groupedData.put("week-1", Map.of("totalHours", 10.0));
        groupedData.put("monthlyTotalHours", 40.0);

        when(timesheetService.getTimesheetsGroupedByWeek(month)).thenReturn(groupedData);

        ResponseEntity<?> response = timesheetController.getTimesheets(null, null, month, 0, 10, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(groupedData, response.getBody());
        verify(timesheetService, times(1)).getTimesheetsGroupedByWeek(month);
        verify(timesheetService, never()).getTimesheets(any(), any(), any(), any(), anyInt(), anyInt()); // Ensure other method isn't called
    }

    @Test
    void getTimesheets_FilterByDay_ReturnsPaginatedTimesheets() {
        String day = "2025-06-03";
        int page = 0;
        int size = 10;
        List<Timesheet> timesheetList = Collections.singletonList(timesheet);
        Page<Timesheet> mockPage = new PageImpl<>(timesheetList, org.springframework.data.domain.PageRequest.of(page, size), 1);

        when(timesheetService.getTimesheets(eq(day), isNull(), isNull(), isNull(), eq(page), eq(size))).thenReturn(mockPage);

        ResponseEntity<?> response = timesheetController.getTimesheets(day, null, null, page, size, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CustomPageResponse);
        CustomPageResponse<Timesheet> customResponse = (CustomPageResponse<Timesheet>) response.getBody();
        assertEquals(1, customResponse.getContent().size());
        assertEquals(timesheet, customResponse.getContent().get(0));
        assertEquals(page, customResponse.getPageNumber());
        assertEquals(size, customResponse.getPageSize());
        assertEquals(1, customResponse.getTotalElements());
        assertEquals(1, customResponse.getTotalPages());
        verify(timesheetService, times(1)).getTimesheets(eq(day), isNull(), isNull(), isNull(), eq(page), eq(size));
        verify(timesheetService, never()).getTimesheetsGroupedByWeek(anyString());
    }

    @Test
    void getTimesheets_FilterByWeek_ReturnsPaginatedTimesheets() {
        Integer week = 23;
        int page = 0;
        int size = 10;
        List<Timesheet> timesheetList = Collections.singletonList(timesheet);
        Page<Timesheet> mockPage = new PageImpl<>(timesheetList, org.springframework.data.domain.PageRequest.of(page, size), 1);

        when(timesheetService.getTimesheets(isNull(), eq(week), isNull(), isNull(), eq(page), eq(size))).thenReturn(mockPage);

        ResponseEntity<?> response = timesheetController.getTimesheets(null, week, null, page, size, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CustomPageResponse);
        verify(timesheetService, times(1)).getTimesheets(isNull(), eq(week), isNull(), isNull(), eq(page), eq(size));
    }

    @Test
    void getTimesheets_NoFilters_ReturnsPaginatedTimesheets() {
        int page = 0;
        int size = 10;
        List<Timesheet> timesheetList = Collections.singletonList(timesheet);
        Page<Timesheet> mockPage = new PageImpl<>(timesheetList, org.springframework.data.domain.PageRequest.of(page, size), 1);


        when(timesheetService.getTimesheets(isNull(), isNull(), isNull(), isNull(), eq(page), eq(size))).thenReturn(mockPage);

        ResponseEntity<?> response = timesheetController.getTimesheets(null, null, null, page, size, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CustomPageResponse);
        verify(timesheetService, times(1)).getTimesheets(isNull(), isNull(), isNull(), isNull(), eq(page), eq(size));
    }

    @Test
    void getTimesheets_WithEmployeeId_ReturnsPaginatedTimesheets() {
        String employeeId = "someOtherEmp";
        int page = 0;
        int size = 10;
        List<Timesheet> timesheetList = Collections.singletonList(timesheet);
        Page<Timesheet> mockPage = new PageImpl<>(timesheetList, org.springframework.data.domain.PageRequest.of(page, size), 1);

        when(timesheetService.getTimesheets(isNull(), isNull(), isNull(), eq(employeeId), eq(page), eq(size))).thenReturn(mockPage);

        ResponseEntity<?> response = timesheetController.getTimesheets(null, null, null, page, size, employeeId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CustomPageResponse);
        verify(timesheetService, times(1)).getTimesheets(isNull(), isNull(), isNull(), eq(employeeId), eq(page), eq(size));
    }

    @Test
    void deleteTimesheet_ReturnsOkStatus() {
        String timesheetId = "ts123";
        doNothing().when(timesheetService).deleteTimesheet(timesheetId);

        ResponseEntity<String> response = timesheetController.deleteTimesheet(timesheetId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Timesheet deleted successfully", response.getBody());
        verify(timesheetService, times(1)).deleteTimesheet(timesheetId);
    }
}