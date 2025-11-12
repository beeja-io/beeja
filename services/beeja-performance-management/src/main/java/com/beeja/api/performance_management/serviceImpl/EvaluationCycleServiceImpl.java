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
import com.beeja.api.performance_management.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of {@link EvaluationCycleService}.
 * <p>
 * Handles CRUD operations, validation, and questionnaire linking for Evaluation Cycles.
 * Enforces tenant-level isolation using {@link UserContext}.
 */
@Slf4j
@Service
public class EvaluationCycleServiceImpl implements EvaluationCycleService {

    @Autowired private EvaluationCycleRepository cycleRepository;
    @Autowired private QuestionnaireService questionnaireService;

    /**
     * Creates a new evaluation cycle.
     *
     * @param cycle the evaluation cycle entity
     * @return the saved evaluation cycle
     */
    @Override
    public EvaluationCycle createCycle(EvaluationCycle cycle) {
        validateCycleDates(cycle);
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        cycle.setOrganizationId(orgId);
        cycle.setStatus(CycleStatus.IN_PROGRESS);
        try {
            return cycleRepository.save(cycle);
        } catch (Exception e) {
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.INTERNAL_SERVER_ERROR,
                    ErrorCode.DATABASE_ERROR,
                    Constants.ERROR_SAVING_EVALUATION_CYCLE));
        }
    }

    /**
     * Creates a new evaluation cycle along with its questionnaire.
     *
     * @param dto the DTO containing cycle and questions
     * @return the saved cycle details DTO
     */
    @Override
    public EvaluationCycleDetailsDto createCycleWithQuestions(EvaluationCycleCreateDto dto) {
        Questionnaire savedQuestionnaire = null;
        if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            Questionnaire q = new Questionnaire();
            q.setQuestions(dto.getQuestions());
            savedQuestionnaire = questionnaireService.createQuestionnaire(q);
        }

        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        EvaluationCycle cycle = new EvaluationCycle();
        cycle.setOrganizationId(orgId);
        cycle.setName(dto.getName());
        cycle.setType(dto.getType());
        cycle.setFormDescription(dto.getFormDescription());
        cycle.setStartDate(dto.getStartDate());
        cycle.setEndDate(dto.getEndDate());
        cycle.setFeedbackDeadline(dto.getFeedbackDeadline());
        cycle.setSelfEvalDeadline(dto.getSelfEvalDeadline());
        cycle.setStatus(dto.getStatus() != null ? dto.getStatus() : CycleStatus.IN_PROGRESS);
        if (savedQuestionnaire != null) cycle.setQuestionnaireId(savedQuestionnaire.getId());
        validateCycleDates(cycle);
        cycle = cycleRepository.save(cycle);
        return new EvaluationCycleDetailsDto(cycle, savedQuestionnaire);
    }

    /**
     * Retrieves all evaluation cycles for the current organization.
     *
     * @return list of evaluation cycles
     */
    @Override
    public List<EvaluationCycle> getAllCycles() {
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        List<EvaluationCycle> cycles = cycleRepository.findByOrganizationId(orgId);
        if (cycles.isEmpty())
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.SUCCESS,
                    ErrorCode.NO_EVALUATION_CYCLES_FOUND,
                    Constants.NO_EVALUATION_CYCLE));
        return cycles;
    }

    /**
     * Retrieves an evaluation cycle by ID.
     *
     * @param id the cycle ID
     * @return the evaluation cycle
     */
    @Override
    public EvaluationCycle getCycleById(String id) {
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        EvaluationCycle cycle = cycleRepository.findByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new ResourceNotFoundException(BuildErrorMessage.buildErrorMessage(
                        ErrorType.RESOURCE_NOT_FOUND_ERROR, ErrorCode.RESOURCE_NOT_FOUND, Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND + id)));
        validateCycleAccess(cycle);
        return cycle;
    }

    /**
     * Retrieves a cycle along with its questionnaire.
     *
     * @param id the cycle ID
     * @return DTO with cycle and questions
     */
    @Override
    public EvaluationCycleDetailsDto getCycleWithQuestionnaire(String id) {
        EvaluationCycle cycle = getCycleById(id);
        EvaluationCycleDetailsDto dto = new EvaluationCycleDetailsDto(cycle, null);
        if (cycle.getQuestionnaireId() != null) {
            try {
                Questionnaire q = questionnaireService.getQuestionnaireById(cycle.getQuestionnaireId());
                dto.setQuestions(q.getQuestions());
            } catch (ResourceNotFoundException e) {
                log.warn(Constants.ERROR_QUESTIONNAIRE_NOT_FOUND_FOR_CYCLE, id);
            }
        }
        return dto;
    }

    /**
     * Updates an existing evaluation cycle.
     *
     * @param id    cycle ID
     * @param cycle updated cycle data
     * @return updated cycle
     */
    @Override
    public EvaluationCycle updateCycle(String id, EvaluationCycle cycle) {
        EvaluationCycle existing = getCycleById(id);
        if (existing.getStatus() == CycleStatus.COMPLETED)
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_OPERATION, Constants.ERROR_CANNOT_UPDATE_PUBLISHED_CYCLE));
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
        return cycleRepository.save(existing);
    }

    /**
     * Performs a full update (cycle + questionnaire).
     *
     * @param id  cycle ID
     * @param dto updated data DTO
     * @return updated cycle details
     */
    @Override
    public EvaluationCycleDetailsDto updateFullCycle(String id, EvaluationCycleDetailsDto dto) {
        EvaluationCycle cycle = getCycleById(id);
        cycle.setName(dto.getName());
        cycle.setType(dto.getType());
        cycle.setStartDate(dto.getStartDate());
        cycle.setEndDate(dto.getEndDate());
        cycle.setFeedbackDeadline(dto.getFeedbackDeadline());
        cycle.setSelfEvalDeadline(dto.getSelfEvalDeadline());
        cycle.setFormDescription(dto.getFormDescription());
        cycle.setStatus(dto.getStatus());
        EvaluationCycle updatedCycle = updateCycle(id, cycle);
        Questionnaire updatedQuestionnaire = null;

        if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            if (updatedCycle.getQuestionnaireId() == null || updatedCycle.getQuestionnaireId().isBlank()) {
                updatedQuestionnaire = new Questionnaire();
                updatedQuestionnaire.setQuestions(dto.getQuestions());
                updatedQuestionnaire = questionnaireService.createQuestionnaire(updatedQuestionnaire);
                updatedCycle.setQuestionnaireId(updatedQuestionnaire.getId());
                updateCycle(id, updatedCycle);
            } else {
                updatedQuestionnaire = questionnaireService.updateQuestions(updatedCycle.getQuestionnaireId(), dto.getQuestions());
            }
        }
        return new EvaluationCycleDetailsDto(updatedCycle, updatedQuestionnaire);
    }

    /**
     * Updates cycle status.
     *
     * @param id     cycle ID
     * @param status new status
     * @return updated cycle
     */
    @Override
    public EvaluationCycle updateCycleStatus(String id, CycleStatus status) {
        if (status == null)
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_INPUT,
                    Constants.ERROR_STATUS_CANNOT_BE_NULL
            ));
        EvaluationCycle cycle = getCycleById(id);
        validateStatusTransition(cycle.getStatus(), status);
        cycle.setStatus(status);
        return cycleRepository.save(cycle);
    }

    /**
     * Deletes a cycle by ID.
     *
     * @param id cycle ID
     */
    @Override
    public void deleteCycle(String id) {
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        if (!cycleRepository.existsByIdAndOrganizationId(id, orgId))
            throw new ResourceNotFoundException(BuildErrorMessage.buildErrorMessage(
                    ErrorType.RESOURCE_NOT_FOUND_ERROR,
                    ErrorCode.RESOURCE_NOT_FOUND,
                    Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND + id));
        cycleRepository.deleteById(id);
    }

    /**
     * Fetches the currently active evaluation cycle.
     *
     * @param status desired status (e.g., IN_PROGRESS)
     * @return active evaluation cycle
     */
    @Override
    public EvaluationCycle getCurrentActiveCycle(CycleStatus status) {
        LocalDate today = LocalDate.now();
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        return cycleRepository.findByOrganizationIdAndStatusAndStartDateLessThanEqualAndFeedbackDeadlineGreaterThanEqual(orgId, status, today, today)
                .orElseThrow(() -> new ResourceNotFoundException(BuildErrorMessage.buildErrorMessage(
                        ErrorType.NOT_FOUND,
                        ErrorCode.CYCLE_NOT_FOUND,
                        Constants.ERROR_NO_ACTIVE_EVALUATION_CYCLE
                )));
    }

    /**
     * Retrieves all cycles by status.
     *
     * @param status cycle status
     * @return list of matching cycles
     */
    @Override
    public List<EvaluationCycle> getCyclesByStatus(CycleStatus status) {
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        List<EvaluationCycle> cycles = cycleRepository.findByOrganizationIdAndStatus(orgId, status);
        if (cycles.isEmpty())
            throw new ResourceNotFoundException(BuildErrorMessage.buildErrorMessage(
                    ErrorType.RESOURCE_NOT_FOUND_ERROR,
                    ErrorCode.RESOURCE_NOT_FOUND,
                    Constants.INFO_NO_EVALUATION_CYCLES_FOUND_WITH_STATUS + status
            ));
        return cycles;
    }

    /** Validates date relationships between cycle phases. */
    private void validateCycleDates(EvaluationCycle cycle) {
        LocalDate start = cycle.getStartDate(), end = cycle.getEndDate(),
                selfEval = cycle.getSelfEvalDeadline(), feedback = cycle.getFeedbackDeadline();
        if (start == null || end == null || selfEval == null || feedback == null)
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_DATE,
                    Constants.MISSING_CYCLE_DATE_FIELDS
            ));

        if (selfEval.isBefore(start))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_SELF_EVAL_DEADLINE,
                    Constants.SELF_EVAL_DEADLINE_BEFORE_START
            ));

        if (feedback.isBefore(end))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_DATE,
                    Constants.FEEDBACK_DEADLINE_BEFORE_END_DATE
            ));

        if (start.isAfter(end))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_DATE,
                    Constants.START_DATE_AFTER_END_DATE
            ));

        if (selfEval.isAfter(feedback))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_SELF_EVAL_DEADLINE,
                    Constants.INFO_SELF_EVAL_DEADLINE_ERROR
            ));
    }

    /** Ensures deadlines are not past for the current cycle. */
    private void validateCycleAccess(EvaluationCycle cycle) {
        LocalDate today = LocalDate.now();
        if (cycle.getEndDate() != null && today.isAfter(cycle.getEndDate()))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_OPERATION, Constants.ERROR_EVALUATION_FORM_EXPIRED_END_DATE_PASSED));
        if (cycle.getSelfEvalDeadline() != null && today.isAfter(cycle.getSelfEvalDeadline()))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_OPERATION,
                    Constants.ERROR_SELF_EVALUATION_DEADLINE_PASSED_FOR_CYCLE
            ));

        if (cycle.getFeedbackDeadline() != null && today.isAfter(cycle.getFeedbackDeadline()))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_OPERATION,
                    Constants.ERROR_FEEDBACK_DEADLINE_PASSED_FOR_CYCLE
            ));
    }

    /** Validates status transitions between cycle states. */
    private void validateStatusTransition(CycleStatus current, CycleStatus target) {
        if (current == CycleStatus.IN_PROGRESS && target != CycleStatus.COMPLETED)
            throw new InvalidOperationException("An in-progress cycle can only be marked as completed");
        if (current == CycleStatus.COMPLETED)
            throw new InvalidOperationException("A completed cycle cannot be modified or reopened");
    }
}
