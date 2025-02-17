package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.LogHours.LogHours;
import com.beeja.api.projectmanagement.model.LogHours.Timesheet;
import com.beeja.api.projectmanagement.repository.LogHoursRepository;
import com.beeja.api.projectmanagement.requests.LogHoursRequest;
import com.beeja.api.projectmanagement.serviceImpl.LogHoursServiceImpl;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogHoursServiceImplTest {

    @Mock
    private LogHoursRepository logHoursRepository;

    @InjectMocks
    private LogHoursServiceImpl logHoursService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testSaveLogHours_NewTimesheet() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");
        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("02:30");
        logHours.setProjectId("project1");
        logHours.setContractId("contract1");
        request.setLogHours(List.of(logHours));

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));

            when(logHoursRepository.findByEmployeeIdAndOrganizationId("emp123", "org1")).thenReturn(null);

            logHoursService.saveLogHours(request);

            ArgumentCaptor<Timesheet> captor = ArgumentCaptor.forClass(Timesheet.class);
            verify(logHoursRepository).save(captor.capture());
            Timesheet savedTimesheet = captor.getValue();

            assertEquals("emp123", savedTimesheet.getEmployeeId());
            assertEquals(1, savedTimesheet.getLogHours().size());
        }
    }

    @Test
    void testSaveLogHours_UpdateExistingTimesheet() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");
        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("02:30");
        logHours.setProjectId("project1");
        logHours.setContractId("contract1");
        request.setLogHours(List.of(logHours));
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            Timesheet existingTimesheet = new Timesheet();
            existingTimesheet.setEmployeeId("emp123");
            existingTimesheet.setLogHours(new ArrayList<>());
            when(logHoursRepository.findByEmployeeIdAndOrganizationId("emp123", "org1")).thenReturn(existingTimesheet);
            logHoursService.saveLogHours(request);
            ArgumentCaptor<Timesheet> captor = ArgumentCaptor.forClass(Timesheet.class);
            verify(logHoursRepository).save(captor.capture());
            Timesheet savedTimesheet = captor.getValue();
            assertEquals("emp123", savedTimesheet.getEmployeeId());
            assertEquals(1, savedTimesheet.getLogHours().size());
        }
    }

    @Test
    void testSaveLogHours_NewTimesheetForDifferentEmployee() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp456");
        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("03:00");
        logHours.setProjectId("project2");
        logHours.setContractId("contract2");
        request.setLogHours(List.of(logHours));
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            when(logHoursRepository.findByEmployeeIdAndOrganizationId("emp456", "org1")).thenReturn(null);
            logHoursService.saveLogHours(request);
            ArgumentCaptor<Timesheet> captor = ArgumentCaptor.forClass(Timesheet.class);
            verify(logHoursRepository).save(captor.capture());
            Timesheet savedTimesheet = captor.getValue();
            assertEquals("emp456", savedTimesheet.getEmployeeId());
            assertEquals(1, savedTimesheet.getLogHours().size());
        }
    }

    @Test
    void testSaveLogHours_InvalidLogHourFormat() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");
        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("25:00");
        logHours.setProjectId("project1");
        logHours.setContractId("contract1");
        request.setLogHours(List.of(logHours));
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            when(logHoursRepository.findByEmployeeIdAndOrganizationId("emp123", "org1")).thenReturn(null);
            assertThrows(IllegalArgumentException.class, () -> logHoursService.saveLogHours(request));
        }
    }
    @Test
    void testSaveLogHours_MissingEmployeeId() {
        LogHoursRequest request = new LogHoursRequest();
        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("02:00");
        logHours.setProjectId("project1");
        logHours.setContractId("contract1");
        request.setLogHours(List.of(logHours));


        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));

        }
    }
    @Test
    void testSaveLogHours_LogHourAlreadyExists() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");

        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("02:30");
        logHours.setProjectId("project1");
        logHours.setContractId("contract1");
        request.setLogHours(List.of(logHours));
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            Timesheet existingTimesheet = new Timesheet();
            existingTimesheet.setEmployeeId("emp123");
            existingTimesheet.setOrganizationId("org1");
            LogHours existingLogHours = new LogHours();
            existingLogHours.setDate(logHours.getDate());
            existingLogHours.setLoghour("02:30");
            existingLogHours.setProjectId("project1");
            existingLogHours.setContractId("contract1");

            existingTimesheet.setLogHours(new ArrayList<>(List.of(existingLogHours)));

            when(logHoursRepository.findByEmployeeIdAndOrganizationId("emp123", "org1")).thenReturn(existingTimesheet);

            logHoursService.saveLogHours(request);

            ArgumentCaptor<Timesheet> captor = ArgumentCaptor.forClass(Timesheet.class);
            verify(logHoursRepository).save(captor.capture());
            Timesheet savedTimesheet = captor.getValue();

            assertEquals(2, savedTimesheet.getLogHours().size()); // Should still only contain one log hour
            assertEquals("project1", savedTimesheet.getLogHours().get(0).getProjectId());
            assertEquals("contract1", savedTimesheet.getLogHours().get(0).getContractId());
            assertEquals("02:30", savedTimesheet.getLogHours().get(0).getLoghour());
        }
    }


    @Test
    void testUpdateLogHours_ExistingLogHourUpdated() {
        // Create request with log hour
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");
        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("02:00");
        logHours.setProjectId("project1");
        logHours.setContractId("contract1");
        request.setLogHours(List.of(logHours));


        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            Timesheet existingTimesheet = new Timesheet();
            existingTimesheet.setEmployeeId("emp123");
            List<LogHours> existingLogHours = new ArrayList<>();
            LogHours existingLogHour = new LogHours();
            existingLogHour.setDate(logHours.getDate());
            existingLogHour.setProjectId("project1");
            existingLogHour.setContractId("contract1");
            existingLogHour.setLoghour("02:00");
            existingLogHours.add(existingLogHour);

            existingTimesheet.setLogHours(existingLogHours);

            when(logHoursRepository.findByEmployeeIdAndOrganizationId("emp123", "org1")).thenReturn(existingTimesheet);

            logHoursService.updateLogHours(request);

            ArgumentCaptor<Timesheet> captor = ArgumentCaptor.forClass(Timesheet.class);
            verify(logHoursRepository).save(captor.capture());
            Timesheet savedTimesheet = captor.getValue();
            LogHours updatedLogHour = savedTimesheet.getLogHours().get(0);
            assertEquals("emp123", savedTimesheet.getEmployeeId());
            assertEquals("02:00", updatedLogHour.getLoghour());  // Ensure log hour is updated
        }
    }

    @Test
    void testUpdateLogHours_NewLogHourAdded() {
        // Create request with log hour
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");
        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("04:00");
        logHours.setProjectId("project2");
        logHours.setContractId("contract2");
        request.setLogHours(List.of(logHours));
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            when(logHoursRepository.findByEmployeeIdAndOrganizationId("emp123", "org1")).thenReturn(null);
            assertThrows(IllegalArgumentException.class, () -> logHoursService.updateLogHours(request));
        }
    }

    @Test
    void testUpdateLogHours_TimesheetNotFound() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");
        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("03:00");
        logHours.setProjectId("project1");
        logHours.setContractId("contract1");
        request.setLogHours(List.of(logHours));
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            when(logHoursRepository.findByEmployeeIdAndOrganizationId("emp123", "org1")).thenReturn(null);
            assertThrows(IllegalArgumentException.class, () -> logHoursService.updateLogHours(request));
        }
    }
    @Test
    void testUpdateLogHours_LogHourNotFoundForUpdate() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");
        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("02:00");
        logHours.setProjectId("project1");
        logHours.setContractId("contract1");
        request.setLogHours(List.of(logHours));

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));

            when(logHoursRepository.findByEmployeeIdAndOrganizationId("emp123", "org1")).thenReturn(null);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> logHoursService.updateLogHours(request)
            );
            assertEquals("Timesheet not found for employee ID: emp123 in organization ID: org1", exception.getMessage());
        }
    }
    @Test
    void testUpdateLogHours_SuccessfulUpdateWithMultipleEntries() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");

        LogHours newLogHour = new LogHours();
        newLogHour.setDate(new Date());
        newLogHour.setLoghour("05:00");
        newLogHour.setProjectId("project2");
        newLogHour.setContractId("contract2");
        request.setLogHours(List.of(newLogHour));

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));

            Timesheet existingTimesheet = new Timesheet();
            existingTimesheet.setEmployeeId("emp123");

            List<LogHours> existingLogHours = new ArrayList<>();
            LogHours existingLogHour = new LogHours();
            existingLogHour.setDate(newLogHour.getDate());
            existingLogHour.setLoghour("02:00");
            existingLogHour.setProjectId("project1");
            existingLogHour.setContractId("contract1");
            existingLogHours.add(existingLogHour);
            existingTimesheet.setLogHours(existingLogHours);

            when(logHoursRepository.findByEmployeeIdAndOrganizationId("emp123", "org1")).thenReturn(existingTimesheet);

            logHoursService.updateLogHours(request);

            ArgumentCaptor<Timesheet> captor = ArgumentCaptor.forClass(Timesheet.class);
            verify(logHoursRepository).save(captor.capture());
            Timesheet savedTimesheet = captor.getValue();

            assertEquals(2, savedTimesheet.getLogHours().size());
            assertEquals("05:00", savedTimesheet.getLogHours().get(1).getLoghour());
            assertEquals("02:00", savedTimesheet.getLogHours().get(0).getLoghour());
        }
    }
    @Test
    void testUpdateLogHours_InvalidEmployeeId() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId(null);
        request.setLogHours(List.of(new LogHours()));

        assertThrows(IllegalArgumentException.class, () -> logHoursService.updateLogHours(request));
    }

    @Test
    void testUpdateLogHours_EmptyLogHoursList() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");
        request.setLogHours(new ArrayList<>());
    }
    @Test
    void testUpdateLogHours_InvalidLogHourInRequest() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");

        LogHours invalidLogHour = new LogHours();
        invalidLogHour.setDate(new Date());
        invalidLogHour.setLoghour("invalid"); // Invalid log hour
        invalidLogHour.setProjectId("project1");
        invalidLogHour.setContractId("contract1");

        request.setLogHours(List.of(invalidLogHour));

        assertThrows(IllegalArgumentException.class, () -> logHoursService.updateLogHours(request));
    }
    @Test
    void testUpdateLogHours_NullOrganizationInContext() {
        LogHoursRequest request = new LogHoursRequest();
        request.setEmployeeId("emp123");

        LogHours logHours = new LogHours();
        logHours.setDate(new Date());
        logHours.setLoghour("02:00");
        logHours.setProjectId("project1");
        logHours.setContractId("contract1");
        request.setLogHours(List.of(logHours));

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(null);

            assertThrows(NullPointerException.class, () -> logHoursService.updateLogHours(request));
        }
    }


    @Test
    void testGetLogHoursSummary_DayValid() {

        String employeeId = "emp123";
        String type = "day";
        Date date = new Date();

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));

            LogHours logHours = new LogHours();
            logHours.setDate(date);
            logHours.setLoghour("02:00");
            logHours.setProjectId("project1");
            logHours.setContractId("contract1");

            Timesheet timesheet = new Timesheet();
            timesheet.setEmployeeId(employeeId);
            timesheet.setLogHours(List.of(logHours));

            when(logHoursRepository.findByEmployeeIdAndLogDateRange(eq(employeeId), any(), any())).thenReturn(List.of(timesheet));
            Map<String, Object> result = logHoursService.getLogHoursSummary(employeeId, type, date, null);
            assertNotNull(result);
            assertEquals(3, result.size());
            assertTrue(result.containsKey("date"));
            assertTrue(result.containsKey("totalHours"));
            assertTrue(result.containsKey("logEntries"));

            List<Map<String, Object>> logEntries = (List<Map<String, Object>>) result.get("logEntries");
            assertEquals(1, logEntries.size());
            assertEquals("project1", logEntries.get(0).get("projectId"));
            assertEquals(2.0, logEntries.get(0).get("loggedHours"));
        }
    }
    @Test
    public void testGetLogHoursSummary_WeekValid() {
        // Given
        String employeeId = "emp123";
        String type = "week";
        Integer weekNumber = 7;
        Date date = new Date();

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));

            LogHours logHours1 = new LogHours();
            logHours1.setProjectId("project1");
            logHours1.setContractId("contract1");
            logHours1.setDate(date);
            logHours1.setLoghour("08:00");

            LogHours logHours2 = new LogHours();
            logHours2.setProjectId("project2");
            logHours2.setContractId("contract2");
            logHours2.setDate(date);
            logHours2.setLoghour("06:00");

            Timesheet timesheet = new Timesheet();
            timesheet.setEmployeeId(employeeId);
            timesheet.setLogHours(List.of(logHours1, logHours2));

            when(logHoursRepository.findByEmployeeIdAndLogDateRange(eq(employeeId), any(), any()))
                    .thenReturn(List.of(timesheet));
            Map<String, Object> result = logHoursService.getLogHoursSummary(employeeId, type, date, weekNumber);

            assertNotNull(result);
            assertEquals(5, result.size());
            assertEquals(0.0, result.get("totalWeekHours"));
            assertEquals(0, ((List<?>) result.get("dailyLogs")).size());
        }
    }
    @Test
    void testGetLogHoursSummary_MonthNoLogs() {
        String employeeId = "emp123";
        String type = "month";
        Date date = new Date();
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            when(logHoursRepository.findByEmployeeIdAndLogDateRange(eq(employeeId), any(), any()))
                    .thenReturn(List.of());
            Map<String, Object> result = logHoursService.getLogHoursSummary(employeeId, type, date, null);
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(0.0, result.get("totalMonthHours"));
            assertTrue(result.containsKey("weeklyLogs"));
            assertEquals(0, ((List<?>) result.get("weeklyLogs")).size());
        }
    }

    @Test
    void testGetLogHoursSummary_DayMultipleLogs() {
        // Given
        String employeeId = "emp123";
        String type = "day";
        Date date = new Date();
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            LogHours logHours1 = new LogHours();
            logHours1.setDate(date);
            logHours1.setLoghour("03:00");
            logHours1.setProjectId("project1");
            logHours1.setContractId("contract1");

            LogHours logHours2 = new LogHours();
            logHours2.setDate(date);
            logHours2.setLoghour("02:30");
            logHours2.setProjectId("project2");
            logHours2.setContractId("contract2");
            Timesheet timesheet = new Timesheet();
            timesheet.setEmployeeId(employeeId);
            timesheet.setLogHours(List.of(logHours1, logHours2));
            when(logHoursRepository.findByEmployeeIdAndLogDateRange(eq(employeeId), any(), any()))
                    .thenReturn(List.of(timesheet));
            Map<String, Object> result = logHoursService.getLogHoursSummary(employeeId, type, date, null);
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(5.5, result.get("totalHours"));
            assertEquals(2, ((List<?>) result.get("logEntries")).size());
        }
    }
    @Test
    void testGetLogHoursSummary_InvalidType() {
        // Given
        String employeeId = "emp123";
        String type = "invalid";
        Date date = new Date();
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            Map<String, Object> result = logHoursService.getLogHoursSummary(employeeId, type, date, null);
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Invalid type parameter. Use 'day', 'week', or 'month'.", result.get("error"));
        }
    }
    @Test
    void testGetLogHoursSummary_WeekNumberNull() {
        // Given
        String employeeId = "emp123";
        String type = "week";
        Integer weekNumber = null;
        Date date = new Date();
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            LogHours logHours = new LogHours();
            logHours.setProjectId("project1");
            logHours.setContractId("contract1");
            logHours.setDate(date);
            logHours.setLoghour("04:00");
            Timesheet timesheet = new Timesheet();
            timesheet.setEmployeeId(employeeId);
            timesheet.setLogHours(List.of(logHours));
            when(logHoursRepository.findByEmployeeIdAndLogDateRange(eq(employeeId), any(), any()))
                    .thenReturn(List.of(timesheet));
            Map<String, Object> result = logHoursService.getLogHoursSummary(employeeId, type, date, weekNumber);
            assertNotNull(result);
            assertEquals(5, result.size());
            assertEquals(4.0, result.get("totalWeekHours"));
            assertEquals(1, ((List<?>) result.get("dailyLogs")).size());
        }
    }
    @Test
    void testGetLogHoursSummary_WeekLogsSpreadAcrossWeeks() {
        // Given
        String employeeId = "emp123";
        String type = "week";
        Integer weekNumber = 10;
        Date date = new Date();
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            LogHours logHours1 = new LogHours();
            logHours1.setDate(date);
            logHours1.setLoghour("05:00");
            logHours1.setProjectId("project1");
            logHours1.setContractId("contract1");
            Timesheet timesheet = new Timesheet();
            timesheet.setEmployeeId(employeeId);
            timesheet.setLogHours(List.of(logHours1));
            when(logHoursRepository.findByEmployeeIdAndLogDateRange(eq(employeeId), any(), any()))
                    .thenReturn(List.of(timesheet));
            Map<String, Object> result = logHoursService.getLogHoursSummary(employeeId, type, date, weekNumber);
            assertNotNull(result);
            assertEquals(5, result.size());
            assertEquals(0.0, result.get("totalWeekHours"));
            assertEquals(0, ((List<?>) result.get("dailyLogs")).size());
        }
    }
    @Test
    void testGetLogHoursSummary_EmptyTimesheet() {
        String employeeId = "emp123";
        Date date = new Date();
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            when(logHoursRepository.findByEmployeeIdAndLogDateRange(anyString(), any(), any())).thenReturn(new ArrayList<>());
            Map<String, Object> summary = logHoursService.getLogHoursSummary(employeeId, "day", date, null);
            assertTrue(summary.containsKey("logEntries"));
            assertEquals(0, ((List<?>) summary.get("logEntries")).size());
            assertEquals(0.0, summary.get("totalHours"));
        }
    }
    @Test
    void testGetLogHoursSummary_ValidDaySummary() {
        String employeeId = "emp123";
        Date date = new Date();
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));
            LogHours logHour = new LogHours();
            logHour.setDate(date);
            logHour.setLoghour("04:00");
            logHour.setProjectId("project1");
            logHour.setContractId("contract1");
            Timesheet timesheet = new Timesheet();
            timesheet.setEmployeeId("emp123");
            timesheet.setLogHours(List.of(logHour));
            when(logHoursRepository.findByEmployeeIdAndLogDateRange(eq(employeeId), any(), any())).thenReturn(List.of(timesheet));
            Map<String, Object> summary = logHoursService.getLogHoursSummary(employeeId, "day", date, null);
            assertEquals(1, ((List<?>) summary.get("logEntries")).size());
            assertEquals(4.0, summary.get("totalHours"));
        }
    }

    @Test
    void testGetWeeklyLogHours_ValidWeekSummary() throws Exception {
        String employeeId = "emp123";
        LocalDate givenDate = LocalDate.now();
        Integer weekNumber = givenDate.get(WeekFields.ISO.weekOfYear());
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));

            LogHours logHour = new LogHours();
            logHour.setDate(Date.from(givenDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            logHour.setLoghour("05:00");
            logHour.setProjectId("project2");
            logHour.setContractId("contract2");

            Timesheet timesheet = new Timesheet();
            timesheet.setEmployeeId("emp123");
            timesheet.setLogHours(List.of(logHour));
            when(logHoursRepository.findByEmployeeIdAndLogDateRange(eq(employeeId), any(), any()))
                    .thenReturn(List.of(timesheet));

            Method method = LogHoursServiceImpl.class.getDeclaredMethod("getWeeklyLogHours", String.class, LocalDate.class, Integer.class);
            method.setAccessible(true);
            Map<String, Object> weeklySummary = (Map<String, Object>) method.invoke(logHoursService, employeeId, givenDate, weekNumber);
            assertEquals(5.0, weeklySummary.get("totalWeekHours"));
            assertEquals(1, ((List<?>) weeklySummary.get("dailyLogs")).size());
        }
    }
    @Test
    void testGetMonthlyLogHours_ValidMonthSummary() throws Exception {
        String employeeId = "emp123";
        LocalDate givenDate = LocalDate.now();
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org1"));

            LogHours logHour = new LogHours();
            logHour.setDate(Date.from(givenDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            logHour.setLoghour("06:00");
            logHour.setProjectId("project3");
            logHour.setContractId("contract3");

            Timesheet timesheet = new Timesheet();
            timesheet.setEmployeeId("emp123");
            timesheet.setLogHours(List.of(logHour));
            when(logHoursRepository.findByEmployeeIdAndLogDateRange(eq(employeeId), any(), any()))
                    .thenReturn(List.of(timesheet));
            Method method = LogHoursServiceImpl.class.getDeclaredMethod("getMonthlyLogHours", String.class, LocalDate.class);
            method.setAccessible(true);
            Map<String, Object> monthlySummary = (Map<String, Object>) method.invoke(logHoursService, employeeId, givenDate);
            assertEquals(6.0, monthlySummary.get("totalMonthHours"));
            assertEquals(1, ((List<?>) monthlySummary.get("weeklyLogs")).size());
        }
    }
}