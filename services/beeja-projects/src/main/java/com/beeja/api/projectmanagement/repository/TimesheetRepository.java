package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Timesheet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link Timesheet} documents in MongoDB.
 * <p>
 * Provides organization-scoped query methods to enforce multi-tenancy and ensure
 * timesheets are retrieved or modified only within the correct organization context.
 * </p>
 */
public interface TimesheetRepository extends MongoRepository<Timesheet, String> {

    /**
     * Finds a timesheet by its ID within a specific organization.
     *
     * @param id  the unique identifier of the timesheet
     * @param organizationId  the organization to which the timesheet must belong
     * @return an {@link Optional} containing the matching {@link Timesheet}, or empty if not found
     */
    Optional<Timesheet> findByIdAndOrganizationId(String id, String organizationId);

    /**
     * Checks if a timesheet already exists for a given organization, employee, project,
     * and start date. This is typically used to prevent duplicate timesheet entries.
     *
     * @param organizationId the ID of the organization
     * @param employeeId the ID of the employee who logged the timesheet
     * @param startDate  the date/time at which the timesheet begins
     * @param projectId  the ID of the project associated with the timesheet
     * @return {@code true} if a matching timesheet exists, {@code false} otherwise
     */
    boolean existsByOrganizationIdAndEmployeeIdAndStartDateAndProjectId(
            String organizationId, String employeeId, Instant startDate, String projectId);

    /**
     * Checks if a timesheet exists by its ID within a specific organization.
     *
     * @param id  the unique identifier of the timesheet
     * @param organizationId the ID of the organization
     * @return {@code true} if a matching timesheet exists, {@code false} otherwise
     */
    boolean existsByIdAndOrganizationId(String id, String organizationId);

    /**
     * Finds all timesheets for an employee within a specific organization and
     * whose start date falls within the provided time range.
     *
     * @param organizationId the ID of the organization
     * @param employeeId  the employee whose timesheets are being queried
     * @param start the inclusive lower bound of the start date range
     * @param end the inclusive upper bound of the start date range
     * @return a list of matching {@link Timesheet} records
     */
    List<Timesheet> findAllByOrganizationIdAndEmployeeIdAndStartDateBetween(
            String organizationId, String employeeId, Instant start, Instant end);
}
