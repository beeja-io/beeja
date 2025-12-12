package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.ContractDropdownDto;
import com.beeja.api.projectmanagement.model.dto.ProjectDropdownDto;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Service interface defining operations related to timesheet management.
 * <p>
 * This service is responsible for creating, updating, retrieving, grouping,
 * and deleting timesheet entries. It also provides project and contract
 * metadata required for timesheet submissions.
 * </p>
 */
public interface TimesheetService {

    /**
     * Creates and saves a new timesheet entry.
     *
     * @param requestDto the DTO containing timesheet details such as project, contract,
     * logged hours, and date information
     * @return the saved {@link Timesheet} entity
     */
    Timesheet saveTimesheet(TimesheetRequestDto requestDto);

    /**
     * Updates an existing timesheet entry based on the provided ID.
     *
     * @param dto the DTO containing updated timesheet information
     * @param id  the unique identifier of the timesheet to update
     * @return the updated {@link Timesheet} entity
     */
    Timesheet updateLog(TimesheetRequestDto dto, String id);

    /**
     * Retrieves a paginated list of timesheets filtered by various optional date
     * and employee-based parameters.
     *
     * @param day an optional day filter (format: yyyy-MM-dd)
     * @param week an optional ISO week number filter
     * @param weekYear an optional year corresponding to the ISO week filter
     * @param month an optional month filter (format: yyyy-MM)
     * @param employeeId the ID of the employee whose timesheets should be fetched
     * @param page the page number for pagination (0-indexed)
     * @param size the number of records per page
     * @return a paginated list of {@link Timesheet} entries
     */
    Page<Timesheet> getTimesheets(String day,
                                  Integer week,
                                  Integer weekYear,
                                  String month,
                                  String employeeId,
                                  int page,
                                  int size);

    /**
     * Retrieves all timesheets for the given month and groups them by week.
     *
     * @param month the month to filter timesheets (format: yyyy-MM)
     * @return a map containing week numbers as keys and grouped timesheet data as values
     */
    Map<String, Object> getTimesheetsGroupedByWeek(String month);

    /**
     * Deletes a timesheet by its unique identifier.
     *
     * @param id the ID of the timesheet to delete
     */
    void deleteTimesheet(String id);

    /**
     * Retrieves a list of projects associated with the currently authenticated user.
     *
     * @return a list of {@link ProjectDropdownDto} objects for UI dropdowns
     */
    List<ProjectDropdownDto> getMyProjects();

    /**
     * Retrieves a list of contracts associated with a specific project.
     *
     * @param projectId the ID of the project
     * @return a list of {@link ContractDropdownDto} objects for UI dropdowns
     */
    List<ContractDropdownDto> getContractsForProject(String projectId);

}
