package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.exceptions.InvalidOperationException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.Questionnaire;
import com.beeja.api.performance_management.model.dto.EvaluationCycleCreateDto;
import com.beeja.api.performance_management.model.dto.EvaluationCycleDetailsDto;
import com.beeja.api.performance_management.repository.EvaluationCycleRepository;
import com.beeja.api.performance_management.service.EvaluationCycleService;
import com.beeja.api.performance_management.service.QuestionnaireService;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for managing Evaluation Cycles.
 * Handles creation, updating, retrieval, and deletion of evaluation cycles,
 * including associated questionnaire data and status transitions.
 * Implements {@link EvaluationCycleService}.
 */
@Slf4j
@Service
public class EvaluationCycleServiceImpl implements EvaluationCycleService {

    @Autowired
    private EvaluationCycleRepository cycleRepository;

    @Autowired
    private QuestionnaireService questionnaireService;

    /**
     * Creates a new evaluation cycle with IN_PROGRESS status.
     * Validates date ranges and saves to the repository.
     *
     * @param cycle EvaluationCycle object to be created
     * @return Saved EvaluationCycle
     * @throws InvalidOperationException if validation fails or save fails
     */
    @Override
    public EvaluationCycle createCycle(EvaluationCycle cycle) {
        log.info(Constants.INFO_CREATING_EVALUATION_CYCLE, cycle.getName());

        validateCycleDates(cycle);

        EvaluationCycle newCycle = new EvaluationCycle();

        if (cycle.getName() != null) {
            newCycle.setName(cycle.getName());
        }
        if (cycle.getType() != null) {
            newCycle.setType(cycle.getType());
        }
        if (cycle.getFormDescription() != null) {
            newCycle.setFormDescription(cycle.getFormDescription());
        }
        if (cycle.getStartDate() != null) {
            newCycle.setStartDate(cycle.getStartDate());
        }
        if (cycle.getEndDate() != null) {
            newCycle.setEndDate(cycle.getEndDate());
        }
        if (cycle.getFeedbackDeadline() != null) {
            newCycle.setFeedbackDeadline(cycle.getFeedbackDeadline());
        }
        if (cycle.getSelfEvalDeadline() != null) {
            newCycle.setSelfEvalDeadline(cycle.getSelfEvalDeadline());
        }
        if (cycle.getQuestionnaireId() != null) {
            newCycle.setQuestionnaireId(cycle.getQuestionnaireId());
        }

        newCycle.setStatus(CycleStatus.IN_PROGRESS);

        try {
            newCycle = cycleRepository.save(newCycle);
        } catch (Exception e) {
            log.error(Constants.ERROR_SAVING_EVALUATION_CYCLE, e);
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.INTERNAL_SERVER_ERROR,
                            ErrorCode.DATABASE_ERROR,
                            Constants.ERROR_SAVING_EVALUATION_CYCLE
                    )
            );
        }

        log.info(Constants.INFO_EVALUATION_CYCLE_CREATED, newCycle.getId());
        return newCycle;
    }

    /**
     * Creates a new evaluation cycle along with an optional set of associated questions.
     * If the provided {@link EvaluationCycleCreateDto} includes a list of questions,
     * a new {@link Questionnaire} is created and linked to the evaluation cycle.
     * The method ensures that all cycle dates are valid before persisting the data.
     *
     * @param dto The data transfer object containing details of the evaluation cycle and optional questions.
     * @return An {@link EvaluationCycleDetailsDto} containing the persisted evaluation cycle and the linked questionnaire (if created).
     * @throws InvalidOperationException if there is an error during questionnaire creation or cycle persistence.
     */
    @Override
    public EvaluationCycleDetailsDto createCycleWithQuestions(EvaluationCycleCreateDto dto) {
        log.info(Constants.INFO_CREATING_CYCLE_WITH_QUESTIONS, dto.getName());

        Questionnaire savedQuestionnaire = null;

        if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setQuestions(dto.getQuestions());

            try {
                savedQuestionnaire = questionnaireService.createQuestionnaire(questionnaire);
            } catch (Exception e) {
                log.error(Constants.ERROR_CREATING_QUESTIONNAIRE, e);
                throw new InvalidOperationException(
                        ErrorUtils.formatError(
                                ErrorType.INTERNAL_SERVER_ERROR,
                                ErrorCode.DATABASE_ERROR,
                                Constants.ERROR_CREATING_QUESTIONNAIRE
                        )
                );
            }
        }

        EvaluationCycle cycle = new EvaluationCycle();
        cycle.setName(dto.getName());
        cycle.setType(dto.getType());
        cycle.setFormDescription(dto.getFormDescription());
        cycle.setStartDate(dto.getStartDate());
        cycle.setEndDate(dto.getEndDate());
        cycle.setFeedbackDeadline(dto.getFeedbackDeadline());
        cycle.setSelfEvalDeadline(dto.getSelfEvalDeadline());
        cycle.setStatus(dto.getStatus() != null ? dto.getStatus() : CycleStatus.IN_PROGRESS);

        if (savedQuestionnaire != null) {
            cycle.setQuestionnaireId(savedQuestionnaire.getId());
        }

        validateCycleDates(cycle);

        EvaluationCycle savedCycle;
        try {
            savedCycle = cycleRepository.save(cycle);
        } catch (Exception e) {
            log.error(Constants.ERROR_SAVING_EVALUATION_CYCLE, e);
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.INTERNAL_SERVER_ERROR,
                            ErrorCode.DATABASE_ERROR,
                            Constants.ERROR_SAVING_EVALUATION_CYCLE
                    )
            );
        }

        log.info(Constants.INFO_EVALUATION_CYCLE_CREATED, savedCycle.getId());

        return new EvaluationCycleDetailsDto(savedCycle, savedQuestionnaire);
    }

    /**
     * Retrieves all evaluation cycles.
     *
     * @return List of EvaluationCycle objects
     */
    @Override
    public List<EvaluationCycle> getAllCycles() {
        log.info(Constants.INFO_FETCHING_ALL_CYCLES);
        return cycleRepository.findAll();
    }

    /**
     * Retrieves a specific evaluation cycle by ID.
     *
     * @param id Cycle ID
     * @return EvaluationCycle object
     * @throws ResourceNotFoundException if not found
     */
    @Override
    public EvaluationCycle getCycleById(String id) {
        log.info(Constants.INFO_FETCHING_EVALUATIONCYCLE_BY_ID, id);
        return cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorUtils.formatError(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND + id)
                ));
    }

    /**
     * Retrieves a cycle and its associated questionnaire.
     *
     * @param id Cycle ID
     * @return DTO containing cycle and questions
     */
    @Override
    public EvaluationCycleDetailsDto getCycleWithQuestionnaire(String id) {
        log.info(Constants.INFO_FETCHING_EVALUATIONCYCLE_WITH_QUESTIONNAIRE_BY_ID, id);
        EvaluationCycle cycle = getCycleById(id);
        EvaluationCycleDetailsDto dto = new EvaluationCycleDetailsDto();

        dto.setId(cycle.getId());
        dto.setName(cycle.getName());
        dto.setType(cycle.getType());
        dto.setFormDescription(cycle.getFormDescription());
        dto.setStartDate(cycle.getStartDate());
        dto.setEndDate(cycle.getEndDate());
        dto.setFeedbackDeadline(cycle.getFeedbackDeadline());
        dto.setSelfEvalDeadline(cycle.getSelfEvalDeadline());
        dto.setStatus(cycle.getStatus());
        dto.setQuestionnaireId(cycle.getQuestionnaireId());

        try {
            if (cycle.getQuestionnaireId() != null) {
                Questionnaire questionnaire =
                        questionnaireService.getQuestionnaireById(cycle.getQuestionnaireId());
                dto.setQuestions(questionnaire.getQuestions());
            }
        } catch (ResourceNotFoundException e) {
            log.warn(Constants.ERROR_QUESTIONNAIRE_NOT_FOUND_FOR_CYCLE, id);
        }

        return dto;
    }

    /**
     * Updates an existing evaluation cycle if not in progress.
     *
     * @param id Cycle ID
     * @param cycle Updated cycle data
     * @return Updated EvaluationCycle
     * @throws InvalidOperationException if cycle is in progress or validation fails
     */

    @Override
    public EvaluationCycle updateCycle(String id, EvaluationCycle cycle) {
        log.info(Constants.INFO_UPDATING_EVALUATION_CYCLE, id);

        EvaluationCycle existing = getCycleById(id);

        if (existing.getStatus() == CycleStatus.COMPLETED) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_OPERATION,
                            Constants.ERROR_CANNOT_UPDATE_PUBLISHED_CYCLE));
        }

        validateCycleDates(cycle);

        existing.setName(cycle.getName());
        existing.setType(cycle.getType());
        existing.setStartDate(cycle.getStartDate());
        existing.setEndDate(cycle.getEndDate());
        existing.setFeedbackDeadline(cycle.getFeedbackDeadline());
        existing.setSelfEvalDeadline(cycle.getSelfEvalDeadline());
        existing.setFormDescription(cycle.getFormDescription());
        existing.setStatus(cycle.getStatus());
        existing.setQuestionnaireId(cycle.getQuestionnaireId());

        try {
            existing = cycleRepository.save(existing);
        } catch (Exception e) {
            log.error(Constants.ERROR_UPDATING_EVALUATION_CYCLE, e);
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.INTERNAL_SERVER_ERROR,
                            ErrorCode.DATABASE_ERROR,
                            Constants.ERROR_UPDATING_EVALUATION_CYCLE
                    ));
        }

        log.info(Constants.INFO_EVALUATION_CYCLE_UPDATED_SUCCESSFULLY, existing.getId());
        return existing;
    }

    /**
     * Updates an existing evaluation cycle with new details and optionally updates or creates its associated questions.
     * The method first fetches the evaluation cycle by ID and updates its fields with values from the provided DTO.
     * If the DTO contains questions, it either updates the existing questionnaire or creates a new one if none exists.
     * @param id  The identifier of the evaluation cycle to update.
     * @param dto The data transfer object containing updated cycle details and optional questions.
     * @return An {@link EvaluationCycleDetailsDto} containing the updated evaluation cycle and questionnaire (if updated or created).
     * @throws InvalidOperationException if the cycle cannot be found or an error occurs during update operations.
     */
    @Override
    public EvaluationCycleDetailsDto updateFullCycle(String id, EvaluationCycleDetailsDto dto) {
        log.info(Constants.INFO_FULL_UPDATE_START, id);

        EvaluationCycle cycle = getCycleById(id);
        log.info(Constants.INFO_EXISTING_CYCLE_FETCHED, cycle.getName(), cycle.getStatus());

        cycle.setName(dto.getName());
        cycle.setType(dto.getType());
        cycle.setStartDate(dto.getStartDate());
        cycle.setEndDate(dto.getEndDate());
        cycle.setFeedbackDeadline(dto.getFeedbackDeadline());
        cycle.setSelfEvalDeadline(dto.getSelfEvalDeadline());
        cycle.setStatus(dto.getStatus());
        cycle.setFormDescription(dto.getFormDescription());

        log.info(Constants.INFO_UPDATING_CYCLE_FIELDS, id);
        EvaluationCycle updatedCycle = updateCycle(id, cycle);

        Questionnaire updatedQuestionnaire = null;

        if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            log.info(Constants.INFO_UPDATING_QUESTIONS, id);

            String questionnaireId = cycle.getQuestionnaireId();

            if (questionnaireId == null || questionnaireId.trim().isEmpty()) {
                updatedQuestionnaire = new Questionnaire();
                updatedQuestionnaire.setQuestions(dto.getQuestions());
                updatedQuestionnaire = questionnaireService.createQuestionnaire(updatedQuestionnaire);

                cycle.setQuestionnaireId(updatedQuestionnaire.getId());
                updatedCycle = updateCycle(id, cycle);
            } else {
                updatedQuestionnaire = questionnaireService.updateQuestions(questionnaireId, dto.getQuestions());
            }
        }
        log.info(Constants.INFO_FULL_UPDATE_COMPLETED, id);
        return new EvaluationCycleDetailsDto(updatedCycle, updatedQuestionnaire);
    }

    /**
     * Updates the status of a given cycle.
     *
     * @param id Cycle ID
     * @param status New CycleStatus
     * @return Updated EvaluationCycle
     * @throws InvalidOperationException if status transition is invalid
     */
    @Override
    public EvaluationCycle updateCycleStatus(String id, CycleStatus status) {
        log.info(Constants.INFO_UPDATING_EVALUATION_CYCLE_STATUS, id);

        if (status == null) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_INPUT,
                            Constants.ERROR_STATUS_CANNOT_BE_NULL
                    ));
        }

        EvaluationCycle cycle = getCycleById(id);
        validateStatusTransition(cycle.getStatus(), status);
        cycle.setStatus(status);

        try {
            cycle = cycleRepository.save(cycle);
        } catch (Exception e) {
            log.error(Constants.ERROR_FAILED_UPDATE_CYCLE_STATUS, id, e);
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.INTERNAL_SERVER_ERROR,
                            ErrorCode.DATABASE_ERROR,
                            Constants.ERROR_UPDATING_EVALUATION_CYCLE_STATUS
                    ));
        }

        log.info(Constants.INFO_UPDATED_CYCLE_STATUS, status, id);
        return cycle;
    }

    /**
     * Fetches the current active cycle based on today's date and status.
     *
     * @return Active EvaluationCycle
     * @throws ResourceNotFoundException if no cycle matches
     */
    @Override
    public EvaluationCycle getCurrentActiveCycle(CycleStatus inProgress) {
        LocalDate today = LocalDate.now();
        return cycleRepository.findByStatusAndStartDateLessThanEqualAndFeedbackDeadlineGreaterThanEqual(
                        CycleStatus.IN_PROGRESS, today, today)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorUtils.formatError(
                                ErrorType.NOT_FOUND,
                                ErrorCode.CYCLE_NOT_FOUND,
                                Constants.ERROR_NO_ACTIVE_EVALUATION_CYCLE)
                ));
    }

    /**
     * Retrieves all evaluation cycles by status.
     *
     * @param status CycleStatus to filter by
     * @return List of EvaluationCycle objects
     */
    @Override
    public List<EvaluationCycle> getCyclesByStatus(CycleStatus status) {
        log.info(Constants.INFO_FETCH_CYCLES_BY_STATUS, status);
        List<EvaluationCycle> cycles = cycleRepository.findByStatus(status);
        if (cycles.isEmpty()) {
            log.warn(Constants.ERROR_NO_CYCLES_FOUND, status);
        }
        return cycles;
    }

    private void validateCycleDates(EvaluationCycle cycle) {
        LocalDate startDate = cycle.getStartDate();
        LocalDate endDate = cycle.getEndDate();
        LocalDate selfEvalDeadline = cycle.getSelfEvalDeadline();
        LocalDate feedbackDeadline = cycle.getFeedbackDeadline();

        if (startDate == null || endDate == null || selfEvalDeadline == null || feedbackDeadline == null) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_DATE,
                            Constants.MISSING_CYCLE_DATE_FIELDS
                    )
            );
        }

        //startDate <= selfEvalDeadline
        if (selfEvalDeadline.isBefore(startDate)) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_SELF_EVAL_DEADLINE,
                            Constants.SELF_EVAL_DEADLINE_BEFORE_START
                    )
            );
        }

        // endDate <= feedbackDeadline
        if (feedbackDeadline.isBefore(endDate)) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_DATE,
                            Constants.FEEDBACK_DEADLINE_BEFORE_END_DATE
                    )
            );
        }

        // startDate <= endDate
        if (startDate.isAfter(endDate)) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_DATE,
                            Constants.START_DATE_AFTER_END_DATE
                    )
            );
        }

        // selfEvalDeadline <= feedbackDeadline
        if (selfEvalDeadline.isAfter(feedbackDeadline)) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_SELF_EVAL_DEADLINE,
                            Constants.INFO_SELF_EVAL_DEADLINE_ERROR
                    ));
        }
    }

    private void validateStatusTransition(CycleStatus currentStatus, CycleStatus newStatus) {
        switch (currentStatus) {
            case IN_PROGRESS:
                if (newStatus != CycleStatus.COMPLETED) {
                    throw new InvalidOperationException("An in-progress cycle can only be marked as completed");
                }
                break;

            case COMPLETED:
                throw new InvalidOperationException("A completed cycle cannot be modified or reopened");

            default:
                throw new InvalidOperationException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    /**
     * Deletes a cycle by ID.
     *
     * @param id Cycle ID
     * @throws ResourceNotFoundException if not found
     */
    @Override
    public void deleteCycle(String id) {
        if (!cycleRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ErrorUtils.formatError(
                            ErrorType.RESOURCE_NOT_FOUND_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND + id
                    ));
        }

        cycleRepository.deleteById(id);
        log.info(Constants.INFO_DELETED_EVALUATION_CYCLE, id);
    }


}