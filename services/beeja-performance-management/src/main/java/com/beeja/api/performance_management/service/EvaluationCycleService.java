package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.dto.EvaluationCycleCreateDto;
import com.beeja.api.performance_management.model.dto.EvaluationCycleDetailsDto;

import java.util.List;

/**
 * Service interface for managing performance evaluation cycles within an organization.
 * Provides methods to create, retrieve, update, and delete cycles,
 * as well as manage their statuses and questionnaires.
 */
public interface EvaluationCycleService {

    /**
     * Creates a new evaluation cycle.
     *
     * @param cycle the cycle entity to create
     * @return the created {@link EvaluationCycle}
     */
    EvaluationCycle createCycle(EvaluationCycle cycle);

    /**
     * Retrieves all evaluation cycles for the logged-in user's organization.
     *
     * @return list of {@link EvaluationCycle}
     */
    List<EvaluationCycle> getAllCycles();

    /**
     * Retrieves a cycle by its ID, scoped to the current organization.
     *
     * @param id the cycle ID
     * @return the matching {@link EvaluationCycle}
     */
    EvaluationCycle getCycleById(String id);

    /**
     * Retrieves a cycle along with its questionnaire details.
     *
     * @param id the cycle ID
     * @return the cycle details including questionnaire
     */
    EvaluationCycleDetailsDto getCycleWithQuestionnaire(String id);

    /**
     * Updates an existing evaluation cycle.
     *
     * @param id    the cycle ID
     * @param cycle the updated cycle data
     * @return the updated {@link EvaluationCycle}
     */
    EvaluationCycle updateCycle(String id, EvaluationCycle cycle);

    /**
     * Updates the status of a specific cycle.
     *
     * @param id     the cycle ID
     * @param status the new {@link CycleStatus}
     * @return the updated {@link EvaluationCycle}
     */
    EvaluationCycle updateCycleStatus(String id, CycleStatus status);

    /**
     * Gets the currently active cycle with the given status.
     *
     * @param inProgress the status representing an active cycle
     * @return the current active {@link EvaluationCycle}, or {@code null} if none
     */
    EvaluationCycle getCurrentActiveCycle(CycleStatus inProgress);

    /**
     * Deletes a cycle if it belongs to the current organization.
     *
     * @param id the cycle ID
     */
    void deleteCycle(String id);

    /**
     * Retrieves all cycles filtered by a specific status.
     *
     * @param cycleStatus the {@link CycleStatus} to filter by
     * @return list of {@link EvaluationCycle} with the given status
     */
    List<EvaluationCycle> getCyclesByStatus(CycleStatus cycleStatus);

    /**
     * Updates an entire cycle and its related details.
     *
     * @param id  the cycle ID
     * @param dto the updated cycle details
     * @return the updated {@link EvaluationCycleDetailsDto}
     */
    EvaluationCycleDetailsDto updateFullCycle(String id, EvaluationCycleDetailsDto dto);

    /**
     * Creates a new evaluation cycle along with its questionnaire.
     *
     * @param dto the cycle creation data
     * @return the created {@link EvaluationCycleDetailsDto}
     */
    EvaluationCycleDetailsDto createCycleWithQuestions(EvaluationCycleCreateDto dto);
}