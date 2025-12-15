package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.ContractDropdownDto;
import com.beeja.api.projectmanagement.model.dto.ProjectDropdownDto;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.service.TimesheetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TimesheetController.class)
class TimesheetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimesheetService timesheetService;

    @Autowired
    private ObjectMapper objectMapper;

    private TimesheetRequestDto buildRequestDto() {
        return TimesheetRequestDto.builder()
                .projectId("P1")
                .contractId("C1")
                .startDate(Instant.parse("2025-01-01T10:00:00Z"))
                .timeInMinutes(120)
                .description("Test work")
                .build();
    }
    

    @Test
    void saveTimesheet_shouldReturnCreated() throws Exception {
        TimesheetRequestDto dto = buildRequestDto();

        Timesheet saved = Timesheet.builder()
                .id("TS1")
                .projectId("P1")
                .build();

        when(timesheetService.saveTimesheet(any(TimesheetRequestDto.class))).thenReturn(saved);

        mockMvc.perform(post("/v1/api/timesheets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("TS1"));
    }


    @Test
    void updateLog_shouldReturnOk() throws Exception {
        TimesheetRequestDto dto = buildRequestDto();

        Timesheet updated = Timesheet.builder()
                .id("TS1")
                .projectId("P2")
                .build();

        when(timesheetService.updateLog(any(TimesheetRequestDto.class), eq("TS1")))
                .thenReturn(updated);

        mockMvc.perform(put("/v1/api/timesheets/TS1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value("P2"));
    }


    @Test
    void getTimesheets_shouldReturnBadRequest_whenWeekWithoutWeekYear() throws Exception {
        mockMvc.perform(get("/v1/api/timesheets")
                        .param("week", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("weekYear is required")));
    }


    @Test
    void getTimesheets_shouldReturnGroupedByWeek_whenMonthOnly() throws Exception {
        Map<String, Object> fakeResult = Map.of(
                "weekTimesheets", Map.of(),
                "monthlyTotalHours", 0.0
        );

        when(timesheetService.getTimesheetsGroupedByWeek("2025-01")).thenReturn(fakeResult);

        mockMvc.perform(get("/v1/api/timesheets")
                        .param("month", "2025-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekTimesheets").exists())
                .andExpect(jsonPath("$.monthlyTotalHours").value(0.0));
    }

    @Test
    void getTimesheets_shouldReturnPagedResponse_whenNotOnlyMonth() throws Exception {
        Timesheet ts = Timesheet.builder()
                .id("TS1")
                .projectId("P1")
                .build();

        Page<Timesheet> page = new PageImpl<>(List.of(ts),
                PageRequest.of(0, 10),
                1);

        when(timesheetService.getTimesheets(
                any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/v1/api/timesheets")
                        .param("day", "2025-01-01")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTimesheets_shouldReturnBadRequest_onIllegalArgumentException() throws Exception {
        when(timesheetService.getTimesheets(
                any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Invalid 'day' format."));

        mockMvc.perform(get("/v1/api/timesheets")
                        .param("day", "bad-date"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid date parameter")));
    }

    @Test
    void getTimesheets_shouldReturnBadRequest_onDateTimeParseExceptionFromGroupedEndpoint() throws Exception {
        when(timesheetService.getTimesheetsGroupedByWeek("2025-01"))
                .thenThrow(new DateTimeParseException("Bad", "2025-01", 0));

        mockMvc.perform(get("/v1/api/timesheets")
                        .param("month", "2025-01"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid date parameter")));
    }

    @Test
    void deleteTimesheet_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/v1/api/timesheets/TS1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Timesheet deleted successfully")));
    }

    @Test
    void getMyProjects_shouldReturnList() throws Exception {
        ProjectDropdownDto p1 = new ProjectDropdownDto("1", "P1", "Proj1");
        ProjectDropdownDto p2 = new ProjectDropdownDto("2", "P2", "Proj2");

        when(timesheetService.getMyProjects()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/v1/api/timesheets/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].projectId").value("P1"));
    }

    @Test
    void getContracts_shouldReturnList() throws Exception {
        ContractDropdownDto c1 = new ContractDropdownDto("1", "C1", "Title1");
        ContractDropdownDto c2 = new ContractDropdownDto("2", "C2", "Title2");

        when(timesheetService.getContractsForProject("P1")).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/v1/api/timesheets/projects/P1/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].contractId").value("C1"));
    }
}
