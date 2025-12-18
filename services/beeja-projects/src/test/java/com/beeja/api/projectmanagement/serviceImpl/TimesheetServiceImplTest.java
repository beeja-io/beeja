package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.exceptions.InvalidOperationException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.ContractDropdownDto;
import com.beeja.api.projectmanagement.model.dto.ProjectDropdownDto;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.model.dto.WeekTimesheetResponse;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.repository.TimesheetRepository;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimesheetServiceImplTest {

    @Mock
    private TimesheetRepository timesheetRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private TimesheetServiceImpl timesheetService;

    private TimesheetRequestDto buildRequestDto(String projectId, String contractId) {
        return TimesheetRequestDto.builder()
                .projectId(projectId)
                .contractId(contractId)
                .startDate(Instant.parse("2025-01-01T10:00:00Z"))
                .timeInMinutes(120)
                .description("Test work")
                .build();
    }

    private Timesheet buildExistingTimesheet(String id, String orgId, String empId) {
        return Timesheet.builder()
                .id(id)
                .organizationId(orgId)
                .employeeId(empId)
                .projectId("P1")
                .contractId("C1")
                .startDate(Instant.parse("2025-01-01T10:00:00Z"))
                .timeInMinutes(60)
                .description("Existing")
                .build();
    }

    @Test
    void saveTimesheet_shouldSave_whenProjectAndContractValid() {
        TimesheetRequestDto dto = buildRequestDto("P1", "C1");

        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");

            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            when(projectRepository.existsByProjectIdAndOrganizationId("P1", "ORG1")).thenReturn(true);
            when(contractRepository.existsByContractIdAndOrganizationId("C1", "ORG1")).thenReturn(true);

            Timesheet saved = Timesheet.builder().id("T1").build();
            when(timesheetRepository.save(any(Timesheet.class))).thenReturn(saved);

            Timesheet result = timesheetService.saveTimesheet(dto);

            assertNotNull(result);
            assertEquals("T1", result.getId());
            verify(timesheetRepository, times(1)).save(any(Timesheet.class));
        }
    }

    @Test
    void saveTimesheet_shouldSave_whenContractIdNull_skipsContractValidation() {
        TimesheetRequestDto dto = buildRequestDto("P1", null);

        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            when(projectRepository.existsByProjectIdAndOrganizationId("P1", "ORG1")).thenReturn(true);

            Timesheet saved = Timesheet.builder().id("T2").build();
            when(timesheetRepository.save(any(Timesheet.class))).thenReturn(saved);

            Timesheet result = timesheetService.saveTimesheet(dto);

            assertEquals("T2", result.getId());
            verify(contractRepository, never()).existsByContractIdAndOrganizationId(anyString(), anyString());
        }
    }

    @Test
    void saveTimesheet_shouldThrow_whenProjectNotFound() {
        TimesheetRequestDto dto = buildRequestDto("INVALID", "C1");

        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            when(projectRepository.existsByProjectIdAndOrganizationId("INVALID", "ORG1")).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> timesheetService.saveTimesheet(dto));
        }
    }

    @Test
    void saveTimesheet_shouldThrow_whenContractNotFound() {
        TimesheetRequestDto dto = buildRequestDto("P1", "INVALID");

        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            when(projectRepository.existsByProjectIdAndOrganizationId("P1", "ORG1")).thenReturn(true);
            when(contractRepository.existsByContractIdAndOrganizationId("INVALID", "ORG1")).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> timesheetService.saveTimesheet(dto));
        }
    }

    @Test
    void updateLog_shouldUpdateAllFields_whenAuthorized() {
        TimesheetRequestDto dto = buildRequestDto("P2", "C2");

        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Timesheet existing = buildExistingTimesheet("TS1", "ORG1", "EMP1");
            when(timesheetRepository.findByIdAndOrganizationId("TS1", "ORG1"))
                    .thenReturn(Optional.of(existing));

            when(timesheetRepository.save(any(Timesheet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Timesheet updated = timesheetService.updateLog(dto, "TS1");

            assertEquals("P2", updated.getProjectId());
            assertEquals("C2", updated.getContractId());
            assertEquals(120, updated.getTimeInMinutes());
            assertEquals("Test work", updated.getDescription());
            assertNotNull(updated.getModifiedAt());
            assertEquals("EMP1", updated.getModifiedBy());
        }
    }

    @Test
    void updateLog_shouldUpdateMandatoryOnly_whenOptionalNull() {
        TimesheetRequestDto dto = TimesheetRequestDto.builder()
                .projectId(null)
                .contractId(null)
                .startDate(null)
                .timeInMinutes(30)
                .description("Updated desc")
                .build();

        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Timesheet existing = buildExistingTimesheet("TS1", "ORG1", "EMP1");
            when(timesheetRepository.findByIdAndOrganizationId("TS1", "ORG1"))
                    .thenReturn(Optional.of(existing));
            when(timesheetRepository.save(any(Timesheet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Timesheet updated = timesheetService.updateLog(dto, "TS1");

            assertEquals("P1", updated.getProjectId());
            assertEquals("C1", updated.getContractId());
            assertEquals(30, updated.getTimeInMinutes());
            assertEquals("Updated desc", updated.getDescription());
        }
    }

    @Test
    void updateLog_shouldThrow_whenTimesheetNotFound() {
        TimesheetRequestDto dto = buildRequestDto("P1", "C1");

        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            when(timesheetRepository.findByIdAndOrganizationId("TS_MISSING", "ORG1"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> timesheetService.updateLog(dto, "TS_MISSING"));
        }
    }

    @Test
    void updateLog_shouldThrow_whenUnauthorizedUser() {
        TimesheetRequestDto dto = buildRequestDto("P1", "C1");

        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("OTHER_EMP");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Timesheet existing = buildExistingTimesheet("TS1", "ORG1", "EMP1");
            when(timesheetRepository.findByIdAndOrganizationId("TS1", "ORG1"))
                    .thenReturn(Optional.of(existing));

            assertThrows(InvalidOperationException.class,
                    () -> timesheetService.updateLog(dto, "TS1"));
        }
    }

    @Test
    void getTimesheets_shouldFilterByDay() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Timesheet ts = buildExistingTimesheet("TS1", "ORG1", "EMP1");

            when(mongoTemplate.find(any(Query.class), eq(Timesheet.class)))
                    .thenReturn(List.of(ts));
            when(mongoTemplate.count(any(Query.class), eq(Timesheet.class)))
                    .thenReturn(1L);

            Page<Timesheet> page = timesheetService.getTimesheets(
                    "2025-01-01", null, null, null, null, 0, 10);

            assertEquals(1, page.getTotalElements());
            assertEquals("TS1", page.getContent().get(0).getId());
        }
    }

    @Test
    void getTimesheets_shouldThrow_whenWeekProvidedWithoutYear() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> timesheetService.getTimesheets(
                            null, 1, null, null, null, 0, 10));
            assertTrue(ex.getMessage().contains("weekYear is required"));
        }
    }

    @Test
    void getTimesheets_shouldFilterByWeekAndYear() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Timesheet ts = buildExistingTimesheet("TS1", "ORG1", "EMP1");

            when(mongoTemplate.find(any(Query.class), eq(Timesheet.class)))
                    .thenReturn(List.of(ts));
            when(mongoTemplate.count(any(Query.class), eq(Timesheet.class)))
                    .thenReturn(1L);

            Page<Timesheet> page = timesheetService.getTimesheets(
                    null, 1, 2025, null, null, 0, 10);

            assertEquals(1, page.getTotalElements());
        }
    }

    @Test
    void getTimesheets_shouldFilterByMonth() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Timesheet ts = buildExistingTimesheet("TS1", "ORG1", "EMP1");

            when(mongoTemplate.find(any(Query.class), eq(Timesheet.class)))
                    .thenReturn(List.of(ts));
            when(mongoTemplate.count(any(Query.class), eq(Timesheet.class)))
                    .thenReturn(1L);

            Page<Timesheet> page = timesheetService.getTimesheets(
                    null, null, null, "2025-01", null, 0, 10);

            assertEquals(1, page.getTotalElements());
        }
    }

    @Test
    void getTimesheets_shouldWorkWithoutFilters() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Timesheet ts = buildExistingTimesheet("TS1", "ORG1", "EMP1");

            when(mongoTemplate.find(any(Query.class), eq(Timesheet.class)))
                    .thenReturn(List.of(ts));
            when(mongoTemplate.count(any(Query.class), eq(Timesheet.class)))
                    .thenReturn(1L);

            Page<Timesheet> page = timesheetService.getTimesheets(
                    null, null, null, null, null, 0, 10);

            assertEquals(1, page.getTotalElements());
        }
    }

    @Test
    void getTimesheets_shouldThrow_onInvalidDayFormat() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            assertThrows(IllegalArgumentException.class,
                    () -> timesheetService.getTimesheets(
                            "01-01-2025", null, null, null, null, 0, 10));
        }
    }

    @Test
    void getTimesheets_shouldThrow_onInvalidMonthFormat() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            assertThrows(IllegalArgumentException.class,
                    () -> timesheetService.getTimesheets(
                            null, null, null, "2025/01", null, 0, 10));
        }
    }


    @Test
    void getTimesheetsGroupedByWeek_shouldThrow_onInvalidMonth() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            assertThrows(IllegalArgumentException.class,
                    () -> timesheetService.getTimesheetsGroupedByWeek("2025/01"));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void getTimesheetsGroupedByWeek_shouldAggregateCorrectly() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            ZoneId zone = ZoneId.systemDefault();
            Instant date1 = LocalDate.of(2025, 1, 1).atStartOfDay(zone).toInstant(); // Week 1
            Instant date2 = LocalDate.of(2025, 1, 5).atStartOfDay(zone).toInstant(); // Same week

            Timesheet ts1 = Timesheet.builder()
                    .id("TS1")
                    .organizationId("ORG1")
                    .employeeId("EMP1")
                    .startDate(date1)
                    .timeInMinutes(60)
                    .build();

            Timesheet ts2 = Timesheet.builder()
                    .id("TS2")
                    .organizationId("ORG1")
                    .employeeId("EMP1")
                    .startDate(date2)
                    .timeInMinutes(120)
                    .build();

            when(mongoTemplate.find(any(Query.class), eq(Timesheet.class)))
                    .thenReturn(List.of(ts1, ts2));

            Map<String, Object> result = timesheetService.getTimesheetsGroupedByWeek("2025-01");

            assertTrue(result.containsKey("weekTimesheets"));
            assertTrue(result.containsKey("monthlyTotalHours"));

            Map<String, WeekTimesheetResponse> weekMap =
                    (Map<String, WeekTimesheetResponse>) result.get("weekTimesheets");
            assertFalse(weekMap.isEmpty());

            double monthlyTotal = (double) result.get("monthlyTotalHours");
            assertEquals(3.0, monthlyTotal);
        }
    }


    @Test
    void deleteTimesheet_shouldDelete_whenAuthorized() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Timesheet existing = buildExistingTimesheet("TS1", "ORG1", "EMP1");
            when(timesheetRepository.findByIdAndOrganizationId("TS1", "ORG1"))
                    .thenReturn(Optional.of(existing));

            timesheetService.deleteTimesheet("TS1");

            verify(timesheetRepository).deleteById("TS1");
        }
    }

    @Test
    void deleteTimesheet_shouldThrow_whenNotFound() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            when(timesheetRepository.findByIdAndOrganizationId("MISSING", "ORG1"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> timesheetService.deleteTimesheet("MISSING"));
        }
    }

    @Test
    void deleteTimesheet_shouldThrow_whenUnauthorized() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("OTHER_EMP");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Timesheet existing = buildExistingTimesheet("TS1", "ORG1", "EMP1");
            when(timesheetRepository.findByIdAndOrganizationId("TS1", "ORG1"))
                    .thenReturn(Optional.of(existing));

            assertThrows(InvalidOperationException.class,
                    () -> timesheetService.deleteTimesheet("TS1"));
        }
    }


    @Test
    void getMyProjects_shouldReturnUniqueProjects_fromResourcesAndManagers() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Project p1 = new Project();
            p1.setId("1");
            p1.setProjectId("P1");
            p1.setName("Proj1");

            Project p2 = new Project();
            p2.setId("2");
            p2.setProjectId("P2");
            p2.setName("Proj2");

            Project p1Duplicate = new Project();
            p1Duplicate.setId("3");
            p1Duplicate.setProjectId("P1");
            p1Duplicate.setName("Proj1 Duplicate");

            when(projectRepository.findByOrganizationIdAndProjectResourcesContaining("ORG1", "EMP1"))
                    .thenReturn(List.of(p1, p2));
            when(projectRepository.findByOrganizationIdAndProjectManagersContaining("ORG1", "EMP1"))
                    .thenReturn(List.of(p1Duplicate));

            List<ProjectDropdownDto> result = timesheetService.getMyProjects();

            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(p -> p.getProjectId().equals("P1")));
            assertTrue(result.stream().anyMatch(p -> p.getProjectId().equals("P2")));
        }
    }

    @Test
    void getMyProjects_shouldHandleNullLists() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInEmployeeId).thenReturn("EMP1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            when(projectRepository.findByOrganizationIdAndProjectResourcesContaining("ORG1", "EMP1"))
                    .thenReturn(null);
            when(projectRepository.findByOrganizationIdAndProjectManagersContaining("ORG1", "EMP1"))
                    .thenReturn(null);

            List<ProjectDropdownDto> result = timesheetService.getMyProjects();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }


    @Test
    void getContractsForProject_shouldReturnEmptyList_whenNullFromRepo() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            when(contractRepository.findByProjectIdAndOrganizationId("P1", "ORG1"))
                    .thenReturn(null);

            List<ContractDropdownDto> result = timesheetService.getContractsForProject("P1");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getContractsForProject_shouldMapToDropdownDtos() {
        try (MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put(Constants.ID, "ORG1");
            userContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

            Contract c1 = new Contract();
            c1.setId("1");
            c1.setContractId("C1");
            c1.setContractTitle("Title1");

            Contract c2 = new Contract();
            c2.setId("2");
            c2.setContractId("C2");
            c2.setContractTitle("Title2");

            when(contractRepository.findByProjectIdAndOrganizationId("P1", "ORG1"))
                    .thenReturn(List.of(c1, c2));

            List<ContractDropdownDto> result = timesheetService.getContractsForProject("P1");

            assertEquals(2, result.size());
            assertEquals("C1", result.get(0).getContractId());
            assertEquals("C2", result.get(1).getContractId());
        }
    }
}
