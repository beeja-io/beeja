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
 * <p>
 * Provides query methods based on naming conventions for common operations
 * related to cycle type, status, and date range.
 * </p>
 */
@Repository
public interface EvaluationCycleRepository extends MongoRepository<EvaluationCycle, String> {

    /**
     * Finds all evaluation cycles with the given status.
     *
     * @param status the cycle status (e.g., ACTIVE, COMPLETED)
     * @return a list of EvaluationCycle objects with the specified status
     */
    List<EvaluationCycle> findByOrganizationIdAndStatus(String organizationId, CycleStatus status);

    /**
     * Finds all evaluation cycles that have a start date within the specified date range.
     *
     * @param startDate the lower bound of the start date range
     * @param feedBackDeadLine   the upper bound of the start date range
     * @return a list of EvaluationCycle objects whose start date falls within the range
     */
    List<EvaluationCycle> findByOrganizationIdAndStartDateBetween(String organizationId, LocalDate startDate, LocalDate feedBackDeadLine);

    /**
     * Finds an evaluation cycle with the specified status, where the current date falls
     * between its start and end dates.
     *
     * @param status           the cycle status to match
     * @param startDate        the current date or date to compare against startDate
     * @param feedBackDeadLine the current date or date to compare against endDate
     * @return an list containing the matching EvaluationCycle, if found
     */
    Optional<EvaluationCycle> findByOrganizationIdAndStatusAndStartDateLessThanEqualAndFeedbackDeadlineGreaterThanEqual(
            String organizationId, CycleStatus status, LocalDate startDate, LocalDate feedBackDeadLine);
}