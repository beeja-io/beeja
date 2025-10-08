package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.exceptions.InvalidOperationException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.Questionnaire;
import com.beeja.api.performance_management.model.dto.EvaluationCycleDetailsDto;
import com.beeja.api.performance_management.repository.EvaluationCycleRepository;
import com.beeja.api.performance_management.service.EvaluationCycleService;
import com.beeja.api.performance_management.service.QuestionnaireService;
import com.beeja.api.performance_management.utils.ErrorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of {@link EvaluationCycleService} that manages the business logic
 * for creating, retrieving, updating, and publishing evaluation cycles.
 * <p>
 * This service interacts with {@link EvaluationCycleRepository} to persist data
 * and with {@link QuestionnaireService} to link appropriate questionnaires
 * to evaluation cycles.
 * </p>
 */
@Service
public class EvaluationCycleServiceImpl implements EvaluationCycleService {

    @Autowired
    private EvaluationCycleRepository cycleRepository;

    @Autowired
    private QuestionnaireService questionnaireService;


    /**
     * Creates a new evaluation cycle after validating dates and attempting
     * to auto-link a questionnaire based on department.
     *
     * @param cycle the evaluation cycle to create
     * @return the saved EvaluationCycle
     */
    @Override
    public EvaluationCycle createCycle(EvaluationCycle cycle) {
        validateCycleDates(cycle);

        if (cycle.getQuestionnaireId() == null && cycle.getDepartment() != null) {
            try {
                List<Questionnaire> questionnaires = questionnaireService
                        .getQuestionnairesByDepartment(cycle.getDepartment().name());
                if (!questionnaires.isEmpty()) {
                    cycle.setQuestionnaireId(questionnaires.get(0).getId());
                }
            } catch (Exception e) {
            }
        }

        return cycleRepository.save(cycle);
    }

    /**
     * Retrieves all evaluation cycles from the repository.
     *
     * @return a list of all EvaluationCycle instances
     */
    @Override
    public List<EvaluationCycle> getAllCycles() {
        return cycleRepository.findAll();
    }

    /**
     * Retrieves an evaluation cycle by its ID.
     *
     * @param id the ID of the evaluation cycle
     * @return the found EvaluationCycle
     * @throws ResourceNotFoundException if no cycle with the given ID exists
     */
    @Override
    public EvaluationCycle getCycleById(String id) {
        return cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorUtils.formatError(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                "Evaluation cycle not found with id: " + id
                        )
                ));
    }

    /**
     * Retrieves an evaluation cycle along with its linked questionnaire details.
     *
     * @param id the ID of the evaluation cycle
     * @return EvaluationCycleDetailsDto containing both cycle and questionnaire information
     */
    @Override
    public EvaluationCycleDetailsDto getCycleWithQuestionnaire(String id) {
        EvaluationCycle cycle = getCycleById(id);
        Questionnaire questionnaire = null;

        if (cycle.getQuestionnaireId() != null) {
            try {
                questionnaire = questionnaireService.getQuestionnaireById(cycle.getQuestionnaireId());
            } catch (ResourceNotFoundException e) {
            }
        }

        EvaluationCycleDetailsDto dto = new EvaluationCycleDetailsDto();
        dto.setId(cycle.getId());
        dto.setName(cycle.getName());
        dto.setType(cycle.getType());
        dto.setStartDate(cycle.getStartDate());
        dto.setEndDate(cycle.getEndDate());
        dto.setSelfEvalDeadline(cycle.getSelfEvalDeadline());
        dto.setFeedbackDeadline(cycle.getFeedbackDeadline());
        dto.setStatus(cycle.getStatus());
        dto.setDepartment(cycle.getDepartment());

        if (questionnaire != null) {
            dto.setQuestionnaireId(questionnaire.getId());
            dto.setQuestions(questionnaire.getQuestions());
        }

        return dto;
    }

    /**
     * Updates an existing evaluation cycle, except if it's already published.
     *
     * @param id the ID of the cycle to update
     * @param cycle the updated cycle data
     * @return the updated EvaluationCycle
     * @throws InvalidOperationException if the cycle is already published
     */
    @Override
    public EvaluationCycle updateCycle(String id, EvaluationCycle cycle) {
        EvaluationCycle existing = getCycleById(id);

        if (existing.getStatus() == CycleStatus.PUBLISHED) {
            throw new InvalidOperationException("Cannot update published cycle");
        }

        validateCycleDates(cycle);

        existing.setName(cycle.getName());
        existing.setType(cycle.getType());
        existing.setStartDate(cycle.getStartDate());
        existing.setEndDate(cycle.getEndDate());
        existing.setSelfEvalDeadline(cycle.getSelfEvalDeadline());
        existing.setFeedbackDeadline(cycle.getFeedbackDeadline());
        existing.setQuestionnaireId(cycle.getQuestionnaireId());

        return cycleRepository.save(existing);
    }

    /**
     * Updates the status of an evaluation cycle, validating transitions.
     *
     * @param id the ID of the cycle
     * @param status the new status to apply
     * @return the updated EvaluationCycle
     * @throws InvalidOperationException for invalid status transitions
     */
    @Override
    public EvaluationCycle updateCycleStatus(String id, CycleStatus status) {
        EvaluationCycle cycle = getCycleById(id);
        validateStatusTransition(cycle.getStatus(), status);
        cycle.setStatus(status);
        return cycleRepository.save(cycle);
    }

    /**
     * Retrieves the currently active evaluation cycle, based on date and status.
     *
     * @return the currently open EvaluationCycle
     * @throws ResourceNotFoundException if no active cycle is found
     */
    @Override
    public EvaluationCycle getCurrentActiveCycle() {
        LocalDate today = LocalDate.now();
        return cycleRepository.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        CycleStatus.OPEN, today, today)
                .orElseThrow(() -> new ResourceNotFoundException("No active evaluation cycle found"));
    }

    /**
     * Validates date constraints for an evaluation cycle.
     *
     * @param cycle the evaluation cycle to validate
     * @throws InvalidOperationException if any date validation rule is violated
     */
    private void validateCycleDates(EvaluationCycle cycle) {
        if (cycle.getStartDate().isAfter(cycle.getEndDate())) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_DATE,
                            "Start date must be before end date"
                    )
            );
        }

        if (cycle.getSelfEvalDeadline().isBefore(cycle.getStartDate()) ||
                cycle.getSelfEvalDeadline().isAfter(cycle.getEndDate())) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_SELF_EVAL_DEADLINE,
                            "Self evaluation deadline must be within cycle period"
                    )
            );
        }

        if (cycle.getFeedbackDeadline().isBefore(cycle.getStartDate()) ||
                cycle.getFeedbackDeadline().isAfter(cycle.getEndDate())) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_FEEDBACK_DEADLINE,
                            "Feedback deadline must be within cycle period"
                    )
            );
        }
    }

    /**
     * Validates allowed transitions between cycle statuses.
     *
     * @param currentStatus the current status of the cycle
     * @param newStatus the new status being requested
     * @throws InvalidOperationException if the transition is invalid
     */
    private void validateStatusTransition(CycleStatus currentStatus, CycleStatus newStatus) {
        switch (currentStatus) {
            case DRAFT:
                if (newStatus != CycleStatus.OPEN) {
                    throw new InvalidOperationException("Draft cycle can only be opened");
                }
                break;
            case OPEN:
                if (newStatus != CycleStatus.CLOSED) {
                    throw new InvalidOperationException("Open cycle can only be closed");
                }
                break;
            case CLOSED:
                if (newStatus != CycleStatus.PUBLISHED) {
                    throw new InvalidOperationException("Closed cycle can only be published");
                }
                break;
            case PUBLISHED:
                throw new InvalidOperationException("Published cycle cannot be modified");
            default:
                throw new InvalidOperationException("Invalid status transition");
        }
    }
}