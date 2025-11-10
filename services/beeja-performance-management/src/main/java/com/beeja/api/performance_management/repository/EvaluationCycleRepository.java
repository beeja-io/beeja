package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.model.EvaluationCycle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link EvaluationCycle} documents in MongoDB.
 *
 * <p>
 * Every query explicitly includes <b>organizationId</b> to ensure
 * data isolation between tenants and prevent data leakage.
 * </p>
 */
@Repository
public interface EvaluationCycleRepository extends MongoRepository<EvaluationCycle, String> {

    /**
     * Retrieves all evaluation cycles for a specific organization.
     *
     * @param organizationId the organization ID
     * @param status the cycle status (e.g., ACTIVE, COMPLETED)
     * @return a list of EvaluationCycle objects with the specified status
     */
    List<EvaluationCycle> findByOrganizationIdAndStatus(String organizationId, CycleStatus status);

    /**
     * Finds all evaluation cycles within a start date range for the given organization.
     *
     * @param organizationId the organization ID
     * @param startDate the lower bound of the start date range
     * @param feedBackDeadLine the upper bound of the start date range
     * @return a list of evaluation cycles within the given range
     */
    List<EvaluationCycle> findByOrganizationIdAndStartDateBetween(
            String organizationId, LocalDate startDate, LocalDate feedBackDeadLine);

    /**
     * Finds an active evaluation cycle for an organization where:
     * startDate <= currentDate <= feedbackDeadline
     *
     * @param organizationId the organization ID
     * @param status the cycle status (e.g., ACTIVE)
     * @param startDate current or reference date (used for comparison)
     * @param feedBackDeadLine current or reference date (used for comparison)
     * @return an Optional containing the matching EvaluationCycle, if found
     */
    Optional<EvaluationCycle> findByOrganizationIdAndStatusAndStartDateLessThanEqualAndFeedbackDeadlineGreaterThanEqual(
            String organizationId, CycleStatus status, LocalDate startDate, LocalDate feedBackDeadLine);

    /**
     * Retrieves all evaluation cycles by IDs for a specific organization.
     *
     * @param ids   the list of cycle IDs
     * @param orgId
     * @return a list of matching evaluation cycles
     */
    List<EvaluationCycle> findByIdInAndOrganizationId(List<String> ids, String orgId);

    /**
     * Finds a single evaluation cycle by ID and organization ID.
     *
     * @param id the cycle ID
     * @param organizationId the organization ID
     * @return an Optional containing the EvaluationCycle, if found
     */
    Optional<EvaluationCycle> findByIdAndOrganizationId(String id, String organizationId);

    /**
     * Retrieves all evaluation cycles for a given organization.
     *
     * @param organizationId the organization ID
     * @return list of all cycles for that organization
     */
    List<EvaluationCycle> findByOrganizationId(String organizationId);

    /**
     * Finds all evaluation cycles for a given organization with specific statuses.
     *
     * @param organizationId the organization ID
     * @param statuses list of cycle statuses (e.g., ACTIVE, COMPLETED)
     * @return list of matching EvaluationCycles
     */
    List<EvaluationCycle> findByOrganizationIdAndStatusIn(String organizationId, List<CycleStatus> statuses);
}