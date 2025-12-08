package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.dto.ContractDropdownDto;
import com.beeja.api.projectmanagement.model.dto.CustomPageResponse;
import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.ProjectDropdownDto;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * REST controller for timesheet APIs. Permission enforcement is disabled per request.
 */
@RestController
@RequestMapping("/v1/api/timesheets")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TimesheetController {

    private final TimesheetService timesheetService;

    /**
     * Create a new timesheet.
     *
     * @param requestDto request body
     * @return created Timesheet
     */
    @PostMapping
    public ResponseEntity<Timesheet> saveTimesheet(@Valid @RequestBody TimesheetRequestDto requestDto) {
        Timesheet saved = timesheetService.saveTimesheet(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Update an existing timesheet by id.
     *
     * @param dto update DTO
     * @param id  timesheet id
     * @return updated Timesheet
     */
    @PutMapping("/{id}")
    public ResponseEntity<Timesheet> updateLog(@Valid @RequestBody TimesheetRequestDto dto, @PathVariable("id") String id) {
        Timesheet updated = timesheetService.updateLog(dto, id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get timesheets with optional filters.
     *
     * @param day      yyyy-MM-dd
     * @param week     ISO week number
     * @param weekYear ISO week year (required if week provided)
     * @param month    yyyy-MM
     * @param page     page index
     * @param size     page size
     * @param employeeId optional employee id (defaults to logged-in)
     */
    @GetMapping
    public ResponseEntity<?> getTimesheets(
            @RequestParam(required = false) String day,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) Integer weekYear,
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String employeeId
    ) {
        if (week != null && weekYear == null) {
            return ResponseEntity.badRequest().body("Parameter 'weekYear' is required when using 'week'. Use ISO week year (e.g. 2025).");
        }

        try {
            if (month != null && day == null && week == null) {
                Map<String, Object> grouped = timesheetService.getTimesheetsGroupedByWeek(month);
                return ResponseEntity.ok(grouped);
            } else {
                Page<Timesheet> pageResult = timesheetService.getTimesheets(day, week, weekYear, month, employeeId, page, size);
                CustomPageResponse<Timesheet> response = new CustomPageResponse<>(
                        pageResult.getContent(),
                        pageResult.getNumber(),
                        pageResult.getSize(),
                        pageResult.getTotalElements(),
                        pageResult.getTotalPages()
                );
                return ResponseEntity.ok(response);
            }
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            log.warn("Invalid date parameter: {}", ex.getMessage());
            return ResponseEntity.badRequest().body("Invalid date parameter: " + ex.getMessage());
        }
    }

    /**
     * Delete a timesheet by id.
     *
     * @param id timesheet id
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTimesheet(@PathVariable String id) {
        timesheetService.deleteTimesheet(id);
        return ResponseEntity.ok("Timesheet deleted successfully");
    }

    /**
     * Retrieve projects associated with the logged-in user.
     *
     * @return {@link ResponseEntity} containing a list of {@link ProjectDropdownDto}
     */
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDropdownDto>> getMyProjects() {
        List<ProjectDropdownDto> projects = timesheetService.getMyProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Retrieve contracts for a given project.
     *
     * @param projectId the ID of the project
     * @return {@link ResponseEntity} containing a list of {@link ContractDropdownDto}
     */
    @GetMapping("/projects/{projectId}/contracts")
    public ResponseEntity<List<ContractDropdownDto>> getContracts(@PathVariable String projectId) {
        List<ContractDropdownDto> contracts = timesheetService.getContractsForProject(projectId);
        return ResponseEntity.ok(contracts);
    }

}