package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.repository.TimesheetRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private TimesheetRequestDto timesheetRequestDto;
    private Timesheet timesheet;
    private MockedStatic<UserContext> mockedUserContext;

    private final String EMPLOYEE_ID = "emp123";
    private final String ORGANIZATION_ID = "org456";

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
                .employeeId(EMPLOYEE_ID)
                .organizationId(ORGANIZATION_ID)
                .clientId("client001")
                .projectId("project001")
                .contractId("contract001")
                .startDate(new Date())
                .timeInMinutes(60)
                .description("Working on feature X")
                .createdAt(new Date())
                .createdBy(EMPLOYEE_ID)
                .build();

        mockedUserContext = Mockito.mockStatic(UserContext.class);
        mockedUserContext.when(UserContext::getLoggedInEmployeeId).thenReturn(EMPLOYEE_ID);
        mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", ORGANIZATION_ID));
        mockedUserContext.when(() -> UserContext.hasPermission(anyString())).thenReturn(false); // Default to no special permissions
    }

    @AfterEach
    void tearDown() {
        mockedUserContext.close();
    }

    @Test
    void saveTimesheet_Success() {
        when(projectRepository.existsByProjectId(timesheetRequestDto.getProjectId())).thenReturn(true);
        when(contractRepository.existsByContractId(timesheetRequestDto.getContractId())).thenReturn(true);
        when(timesheetRepository.save(any(Timesheet.class))).thenReturn(timesheet);

        Timesheet savedTimesheet = timesheetService.saveTimesheet(timesheetRequestDto);

        assertNotNull(savedTimesheet);
        assertEquals(timesheet.getId(), savedTimesheet.getId());
        assertEquals(EMPLOYEE_ID, savedTimesheet.getEmployeeId());
        assertEquals(ORGANIZATION_ID, savedTimesheet.getOrganizationId());
        verify(projectRepository, times(1)).existsByProjectId(timesheetRequestDto.getProjectId());
        verify(contractRepository, times(1)).existsByContractId(timesheetRequestDto.getContractId());
        verify(timesheetRepository, times(1)).save(any(Timesheet.class));
    }

    @Test
    void saveTimesheet_ProjectNotFound_ThrowsResourceNotFoundException() {
        when(projectRepository.existsByProjectId(timesheetRequestDto.getProjectId())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                timesheetService.saveTimesheet(timesheetRequestDto));

        assertTrue(exception.getMessage().contains("Project not found"));
        verify(projectRepository, times(1)).existsByProjectId(timesheetRequestDto.getProjectId());
        verify(contractRepository, never()).existsByContractId(anyString());
        verify(timesheetRepository, never()).save(any(Timesheet.class));
    }

    @Test
    void saveTimesheet_ContractNotFound_ThrowsResourceNotFoundException() {
        when(projectRepository.existsByProjectId(timesheetRequestDto.getProjectId())).thenReturn(true);
        when(contractRepository.existsByContractId(timesheetRequestDto.getContractId())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                timesheetService.saveTimesheet(timesheetRequestDto));

        assertTrue(exception.getMessage().contains("Contract not found"));
        verify(projectRepository, times(1)).existsByProjectId(timesheetRequestDto.getProjectId());
        verify(contractRepository, times(1)).existsByContractId(timesheetRequestDto.getContractId());
        verify(timesheetRepository, never()).save(any(Timesheet.class));
    }

    @Test
    void updateLog_Success() {
        String timesheetId = "ts123";
        Timesheet existingTimesheet = Timesheet.builder()
                .id(timesheetId)
                .clientId("oldClient")
                .projectId("oldProject")
                .contractId("oldContract")
                .timeInMinutes(30)
                .description("old description")
                .build();
        TimesheetRequestDto updatedDto = TimesheetRequestDto.builder()
                .clientId("newClient")
                .projectId("newProject")
                .contractId("newContract")
                .timeInMinutes(90)
                .description("new description")
                .build();
        Timesheet savedTimesheet = Timesheet.builder()
                .id(timesheetId)
                .clientId("newClient")
                .projectId("newProject")
                .contractId("newContract")
                .timeInMinutes(90)
                .description("new description")
                .modifiedAt(new Date())
                .build();

        when(timesheetRepository.findById(timesheetId)).thenReturn(Optional.of(existingTimesheet));
        when(timesheetRepository.save(any(Timesheet.class))).thenReturn(savedTimesheet);

        Timesheet result = timesheetService.updateLog(updatedDto, timesheetId);

        assertNotNull(result);
        assertEquals(updatedDto.getClientId(), result.getClientId());
        assertEquals(updatedDto.getProjectId(), result.getProjectId());
        assertEquals(updatedDto.getContractId(), result.getContractId());
        assertEquals(updatedDto.getTimeInMinutes(), result.getTimeInMinutes());
        assertEquals(updatedDto.getDescription(), result.getDescription());
        assertNotNull(result.getModifiedAt());
        verify(timesheetRepository, times(1)).findById(timesheetId);
        verify(timesheetRepository, times(1)).save(any(Timesheet.class));
    }

    @Test
    void updateLog_TimesheetNotFound_ThrowsNoSuchElementException() {
        String timesheetId = "nonExistentId";
        TimesheetRequestDto updatedDto = TimesheetRequestDto.builder().build();

        when(timesheetRepository.findById(timesheetId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> timesheetService.updateLog(updatedDto, timesheetId));
        verify(timesheetRepository, times(1)).findById(timesheetId);
        verify(timesheetRepository, never()).save(any(Timesheet.class));
    }

    @Test
    void getTimesheets_FilterByDay() {
        String day = "2025-01-15";
        int page = 0;
        int size = 10;
        List<Timesheet> timesheetList = Arrays.asList(timesheet);
        Page<Timesheet> timesheetPage = new PageImpl<>(timesheetList, PageRequest.of(page, size), 1);

        when(mongoTemplate.find(any(Query.class), eq(Timesheet.class))).thenReturn(timesheetList);
        when(mongoTemplate.count(any(Query.class), eq(Timesheet.class))).thenReturn(1L);

        Page<Timesheet> result = timesheetService.getTimesheets(day, null, null, null, page, size);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(timesheetList, result.getContent());
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Timesheet.class));
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Timesheet.class));
    }

    @Test
    void getTimesheets_FilterByWeek() {
        int week = LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int page = 0;
        int size = 10;
        List<Timesheet> timesheetList = Arrays.asList(timesheet);
        Page<Timesheet> timesheetPage = new PageImpl<>(timesheetList, PageRequest.of(page, size), 1);

        when(mongoTemplate.find(any(Query.class), eq(Timesheet.class))).thenReturn(timesheetList);
        when(mongoTemplate.count(any(Query.class), eq(Timesheet.class))).thenReturn(1L);

        Page<Timesheet> result = timesheetService.getTimesheets(null, week, null, null, page, size);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(timesheetList, result.getContent());
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Timesheet.class));
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Timesheet.class));
    }

    @Test
    void getTimesheets_FilterByMonth() {
        String month = "2025-01";
        int page = 0;
        int size = 10;
        List<Timesheet> timesheetList = Arrays.asList(timesheet);
        Page<Timesheet> timesheetPage = new PageImpl<>(timesheetList, PageRequest.of(page, size), 1);

        when(mongoTemplate.find(any(Query.class), eq(Timesheet.class))).thenReturn(timesheetList);
        when(mongoTemplate.count(any(Query.class), eq(Timesheet.class))).thenReturn(1L);

        Page<Timesheet> result = timesheetService.getTimesheets(null, null, month, null, page, size);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(timesheetList, result.getContent());
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Timesheet.class));
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Timesheet.class));
    }

    @Test
    void getTimesheets_NoFilters_ReturnsAllForLoggedInEmployee() {
        int page = 0;
        int size = 10;
        List<Timesheet> timesheetList = Arrays.asList(timesheet);
        Page<Timesheet> timesheetPage = new PageImpl<>(timesheetList, PageRequest.of(page, size), 1);

        when(mongoTemplate.find(any(Query.class), eq(Timesheet.class))).thenReturn(timesheetList);
        when(mongoTemplate.count(any(Query.class), eq(Timesheet.class))).thenReturn(1L);

        Page<Timesheet> result = timesheetService.getTimesheets(null, null, null, null, page, size);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(timesheetList, result.getContent());
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Timesheet.class));
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Timesheet.class));
    }

    @Test
    void getTimesheets_WithEmployeeId_HasReadAllTimesheetsPermission() {
        String otherEmployeeId = "otherEmp789";
        mockedUserContext.when(() -> UserContext.hasPermission(PermissionConstants.READ_ALL_TIMESHEETS)).thenReturn(true);

        int page = 0;
        int size = 10;
        List<Timesheet> timesheetList = Arrays.asList(timesheet);
        Page<Timesheet> timesheetPage = new PageImpl<>(timesheetList, PageRequest.of(page, size), 1);

        when(mongoTemplate.find(any(Query.class), eq(Timesheet.class))).thenReturn(timesheetList);
        when(mongoTemplate.count(any(Query.class), eq(Timesheet.class))).thenReturn(1L);

        Page<Timesheet> result = timesheetService.getTimesheets(null, null, null, otherEmployeeId, page, size);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(timesheetList, result.getContent());
        verify(mongoTemplate, times(1)).find(argThat(query -> query.getQueryObject().get("employeeId").equals(otherEmployeeId)), eq(Timesheet.class));
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Timesheet.class));
    }

    @Test
    void getTimesheets_WithEmployeeId_NoReadAllTimesheetsPermission() {
        String otherEmployeeId = "otherEmp789";
        mockedUserContext.when(() -> UserContext.hasPermission(PermissionConstants.READ_ALL_TIMESHEETS)).thenReturn(false);

        int page = 0;
        int size = 10;
        List<Timesheet> timesheetList = Arrays.asList(timesheet);
        Page<Timesheet> timesheetPage = new PageImpl<>(timesheetList, PageRequest.of(page, size), 1);

        when(mongoTemplate.find(any(Query.class), eq(Timesheet.class))).thenReturn(timesheetList);
        when(mongoTemplate.count(any(Query.class), eq(Timesheet.class))).thenReturn(1L);

        Page<Timesheet> result = timesheetService.getTimesheets(null, null, null, otherEmployeeId, page, size);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(timesheetList, result.getContent());
        verify(mongoTemplate, times(1)).find(argThat(query -> query.getQueryObject().get("employeeId").equals(EMPLOYEE_ID)), eq(Timesheet.class));
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Timesheet.class));
    }


    @Test
    void getTimesheetsGroupedByWeek_Success() {
        LocalDate firstDayOfMonth = LocalDate.of(2025, 1, 1);
        LocalDate thirdDayOfMonth = LocalDate.of(2025, 1, 3);
        LocalDate tenthDayOfMonth = LocalDate.of(2025, 1, 10);
        LocalDate twentiethDayOfMonth = LocalDate.of(2025, 1, 20);

        Timesheet ts1 = Timesheet.builder()
                .startDate(Date.from(thirdDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .timeInMinutes(120)
                .build();
        Timesheet ts2 = Timesheet.builder()
                .startDate(Date.from(tenthDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .timeInMinutes(180)
                .build();
        Timesheet ts3 = Timesheet.builder()
                .startDate(Date.from(twentiethDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .timeInMinutes(60)
                .build();
        Timesheet ts4 = Timesheet.builder()
                .startDate(null)
                .timeInMinutes(30)
                .build();

        List<Timesheet> timesheetsInMonth = Arrays.asList(ts1, ts2, ts3, ts4);

        when(mongoTemplate.find(any(Query.class), eq(Timesheet.class))).thenReturn(timesheetsInMonth);

        Map<String, Object> result = timesheetService.getTimesheetsGroupedByWeek("2025-01");

        assertNotNull(result);
        assertTrue(result.containsKey("weekTimesheets"));
        assertTrue(result.containsKey("monthlyTotalHours"));

        Map<String, Map<String, Object>> weekTimesheets = (Map<String, Map<String, Object>>) result.get("weekTimesheets");
        assertFalse(weekTimesheets.isEmpty());


        LocalDate jan3 = LocalDate.of(2025, 1, 3);
        int weekNumberJan3 = jan3.get(WeekFields.ISO.weekOfWeekBasedYear());
        assertTrue(weekTimesheets.containsKey("week-" + weekNumberJan3));
        Map<String, Object> week1Data = weekTimesheets.get("week-" + weekNumberJan3);
        assertEquals(2.0, week1Data.get("totalHours"));
        List<Timesheet> week1Timesheets = (List<Timesheet>) week1Data.get("timesheets");
        assertEquals(1, week1Timesheets.size());
        assertTrue(week1Timesheets.contains(ts1));


        LocalDate jan10 = LocalDate.of(2025, 1, 10);
        int weekNumberJan10 = jan10.get(WeekFields.ISO.weekOfWeekBasedYear());
        assertTrue(weekTimesheets.containsKey("week-" + weekNumberJan10));
        Map<String, Object> week2Data = weekTimesheets.get("week-" + weekNumberJan10);
        assertEquals(3.0, week2Data.get("totalHours"));
        List<Timesheet> week2Timesheets = (List<Timesheet>) week2Data.get("timesheets");
        assertEquals(1, week2Timesheets.size());
        assertTrue(week2Timesheets.contains(ts2));

        LocalDate jan20 = LocalDate.of(2025, 1, 20);
        int weekNumberJan20 = jan20.get(WeekFields.ISO.weekOfWeekBasedYear());
        assertTrue(weekTimesheets.containsKey("week-" + weekNumberJan20));
        Map<String, Object> week4Data = weekTimesheets.get("week-" + weekNumberJan20);
        assertEquals(1.0, week4Data.get("totalHours"));
        List<Timesheet> week4Timesheets = (List<Timesheet>) week4Data.get("timesheets");
        assertEquals(1, week4Timesheets.size());
        assertTrue(week4Timesheets.contains(ts3));

        assertEquals(6.0, result.get("monthlyTotalHours"));

        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Timesheet.class));
    }


    @Test
    void deleteTimesheet_Success() {
        String timesheetId = "ts123";
        when(timesheetRepository.findById(timesheetId)).thenReturn(Optional.of(timesheet));
        doNothing().when(timesheetRepository).delete(any(Timesheet.class));

        timesheetService.deleteTimesheet(timesheetId);

        verify(timesheetRepository, times(1)).findById(timesheetId);
        verify(timesheetRepository, times(1)).delete(timesheet);
    }

    @Test
    void deleteTimesheet_NotFound_ThrowsRuntimeException() {
        String timesheetId = "nonExistentId";
        when(timesheetRepository.findById(timesheetId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                timesheetService.deleteTimesheet(timesheetId));

        assertTrue(exception.getMessage().contains("Timesheet not found with ID: " + timesheetId));
        verify(timesheetRepository, times(1)).findById(timesheetId);
        verify(timesheetRepository, never()).delete(any(Timesheet.class));
    }
}