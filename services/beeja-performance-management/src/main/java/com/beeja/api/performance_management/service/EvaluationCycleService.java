
package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.dto.EvaluationCycleCreateDto;
import com.beeja.api.performance_management.model.dto.EvaluationCycleDetailsDto;

import java.util.List;

/**
 * Service interface for managing evaluation cycles.
 * <p>
 * Provides operations for creating, retrieving, updating,
 * and managing the status of {@link EvaluationCycle} entities.
 * </p>
 */
public interface EvaluationCycleService {

    /**
     * Creates a new evaluation cycle.
     *
     * @param cycle the EvaluationCycle to create
     * @return the created EvaluationCycle
     */
    EvaluationCycle createCycle(EvaluationCycle cycle);

    /**
     * Retrieves all evaluation cycles.
     *
     * @return a list of all EvaluationCycle instances
     */
    List<EvaluationCycle> getAllCycles();

    /**
     * Retrieves an evaluation cycle by its unique ID.
     *
     * @param id the ID of the cycle
     * @return the EvaluationCycle with the given ID
     */
    EvaluationCycle getCycleById(String id);

    /**
     * Retrieves an evaluation cycle along with its associated questionnaire details.
     *
     * @param id the ID of the cycle
     * @return a DTO containing both cycle and questionnaire information
     */
    EvaluationCycleDetailsDto getCycleWithQuestionnaire(String id);

    /**
     * Updates an existing evaluation cycle.
     *
     * @param id    the ID of the cycle to update
     * @param cycle the updated EvaluationCycle data
     * @return the updated EvaluationCycle
     */
    EvaluationCycle updateCycle(String id, EvaluationCycle cycle);

    /**
     * Updates the status of an evaluation cycle.
     *
     * @param id     the ID of the cycle
     * @param status the new status to set
     * @return the updated EvaluationCycle
     */
    EvaluationCycle updateCycleStatus(String id, CycleStatus status);

    /**
     * Retrieves the currently active evaluation cycle.
     * An active cycle typically has a status of ACTIVE and a valid date range.
     *
     * @return the current active EvaluationCycle, if any
     */
    EvaluationCycle getCurrentActiveCycle(CycleStatus inProgress);

    /**
     * Deletes an evaluation cycle by its unique ID.
     *
     * @param id the ID of the cycle to delete
     */
    void deleteCycle(String id);

    /**
     * Retrieves a list of evaluation cycles filtered by their status.
     *
     * @param cycleStatus the status of the evaluation cycles to retrieve (e.g., ACTIVE, COMPLETED, UPCOMING)
     * @return a list of {@link EvaluationCycle} objects that match the specified status
     */
    List<EvaluationCycle> getCyclesByStatus(CycleStatus cycleStatus);

    /**
     * Fully updates an evaluation cycle and its questionnaire details by ID.
     *
     * @param id  the ID of the evaluation cycle to update
     * @param dto the updated cycle and questionnaire details
     * @return the updated {@link EvaluationCycleDetailsDto}
     */
    EvaluationCycleDetailsDto updateFullCycle(String id, EvaluationCycleDetailsDto dto);

    /**
     * Creates a new evaluation cycle along with optional associated questions.
     *
     * @param dto The DTO containing evaluation cycle details and optional questions.
     * @return An {@link EvaluationCycleDetailsDto} representing the created cycle and linked questionnaire.
     */
    EvaluationCycleDetailsDto createCycleWithQuestions(EvaluationCycleCreateDto dto);

}