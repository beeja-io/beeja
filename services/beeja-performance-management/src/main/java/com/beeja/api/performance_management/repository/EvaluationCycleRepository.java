package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.model.EvaluationCycle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link EvaluationCycle} documents in MongoDB.
 * Provides query methods to retrieve evaluation cycles based on organization, status, date range, and IDs.
 */
@Repository
public interface EvaluationCycleRepository extends MongoRepository<EvaluationCycle, String> {

    /**
     * Finds evaluation cycles by organization ID and status.
     *
     * @param organizationId the unique ID of the organization
     * @param status the status of the evaluation cycle
     * @return list of matching {@link EvaluationCycle} documents
     */
    List<EvaluationCycle> findByOrganizationIdAndStatus(String organizationId, CycleStatus status);

    /**
     * Finds evaluation cycles for an organization that start between the given dates.
     *
     * @param organizationId the unique ID of the organization
     * @param startDate the start date range lower bound
     * @param feedBackDeadLine the feedback deadline range upper bound
     * @return list of {@link EvaluationCycle} within the specified date range
     */
    List<EvaluationCycle> findByOrganizationIdAndStartDateBetween(
            String organizationId, LocalDate startDate, LocalDate feedBackDeadLine);

    /**
     * Finds an active evaluation cycle by organization, status, and date range.
     * Typically used to identify a currently active cycle.
     *
     * @param organizationId the unique ID of the organization
     * @param status the status of the evaluation cycle
     * @param startDate the date to compare against the cycle start date
     * @param feedBackDeadLine the date to compare against the feedback deadline
     * @return an {@link Optional} containing the active {@link EvaluationCycle} if found
     */
    Optional<EvaluationCycle> findByOrganizationIdAndStatusAndStartDateLessThanEqualAndFeedbackDeadlineGreaterThanEqual(
            String organizationId, CycleStatus status, LocalDate startDate, LocalDate feedBackDeadLine);

    /**
     * Finds all evaluation cycles by their IDs within a specific organization.
     *
     * @param ids list of evaluation cycle IDs
     * @param orgId the unique ID of the organization
     * @return list of matching {@link EvaluationCycle} documents
     */
    List<EvaluationCycle> findByIdInAndOrganizationId(List<String> ids, String orgId);

    /**
     * Finds an evaluation cycle by its ID and organization.
     *
     * @param id the unique ID of the evaluation cycle
     * @param organizationId the unique ID of the organization
     * @return an {@link Optional} containing the matching {@link EvaluationCycle} if found
     */
    Optional<EvaluationCycle> findByIdAndOrganizationId(String id, String organizationId);

    /**
     * Retrieves all evaluation cycles for a given organization.
     *
     * @param organizationId the unique ID of the organization
     * @return list of all {@link EvaluationCycle} documents belonging to the organization
     */
    List<EvaluationCycle> findByOrganizationId(String organizationId);

    /**
     * Finds evaluation cycles for an organization with any of the given statuses.
     *
     * @param organizationId the unique ID of the organization
     * @param statuses list of possible {@link CycleStatus} values
     * @return list of {@link EvaluationCycle} matching any of the specified statuses
     */
    List<EvaluationCycle> findByOrganizationIdAndStatusIn(String organizationId, List<CycleStatus> statuses);

    /**
     * Checks if an evaluation cycle exists by its ID and organization.
     *
     * @param id the unique ID of the evaluation cycle
     * @param organizationId the unique ID of the organization
     * @return {@code true} if the cycle exists; {@code false} otherwise
     */
    boolean existsByIdAndOrganizationId(String id, String organizationId);

    EvaluationCycle getCycleByOrganizationIdAndId(String organizationId, String id);
}
