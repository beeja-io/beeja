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
import com.beeja.api.performance_management.utils.BuildErrorMessage;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.ErrorUtils;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of {@link EvaluationCycleService}.
 * Provides full CRUD operations, deadline validation, and strict org-level isolation.
 * All log and error messages follow {@link Constants}.
 */
@Slf4j
@Service
public class EvaluationCycleServiceImpl implements EvaluationCycleService {

    @Autowired
    private EvaluationCycleRepository cycleRepository;

    @Autowired
    private QuestionnaireService questionnaireService;

    /**
     * Creates a new evaluation cycle.
     */
    @Override
    public EvaluationCycle createCycle(EvaluationCycle cycle) {
        log.info(Constants.INFO_CREATING_EVALUATION_CYCLE, cycle.getName());
        validateCycleDates(cycle);

        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        cycle.setOrganizationId(orgId);
        cycle.setStatus(CycleStatus.IN_PROGRESS);

        try {
            EvaluationCycle saved = cycleRepository.save(cycle);
            log.info(Constants.INFO_EVALUATION_CYCLE_CREATED + saved.getId());
            return saved;
        } catch (Exception e) {
            log.error(Constants.ERROR_SAVING_EVALUATION_CYCLE, e);
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.INTERNAL_SERVER_ERROR,
                    ErrorCode.DATABASE_ERROR,
                    Constants.ERROR_SAVING_EVALUATION_CYCLE
            ));
        }
    }

    /**
     * Creates a new evaluation cycle with optional questionnaire.
     */
    @Override
    public EvaluationCycleDetailsDto createCycleWithQuestions(EvaluationCycleCreateDto dto) {
        log.info(Constants.INFO_CREATING_CYCLE_WITH_QUESTIONS, dto.getName());
        Questionnaire savedQuestionnaire = null;

        if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            try {
                Questionnaire questionnaire = new Questionnaire();
                questionnaire.setQuestions(dto.getQuestions());
                savedQuestionnaire = questionnaireService.createQuestionnaire(questionnaire);
                log.info(Constants.INFO_QUESTIONNAIRE_CREATED, savedQuestionnaire.getId());
            } catch (Exception e) {
                log.error(Constants.ERROR_CREATING_QUESTIONNAIRE, e);
                throw new InvalidOperationException(ErrorUtils.formatError(
                        ErrorType.INTERNAL_SERVER_ERROR,
                        ErrorCode.DATABASE_ERROR,
                        Constants.ERROR_CREATING_QUESTIONNAIRE
                ));
            }
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
        if (savedQuestionnaire != null) {
            cycle.setQuestionnaireId(savedQuestionnaire.getId());
        }

        validateCycleDates(cycle);

        try {
            cycle = cycleRepository.save(cycle);
            log.info(Constants.INFO_EVALUATION_CYCLE_CREATED + cycle.getId());
        } catch (Exception e) {
            log.error(Constants.ERROR_SAVING_EVALUATION_CYCLE, e);
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.INTERNAL_SERVER_ERROR,
                    ErrorCode.DATABASE_ERROR,
                    Constants.ERROR_SAVING_EVALUATION_CYCLE
            ));
        }

        return new EvaluationCycleDetailsDto(cycle, savedQuestionnaire);
    }

    /**
     * Fetch all cycles for the logged-in organization.
     */
    @Override
    public List<EvaluationCycle> getAllCycles() {
        log.info(Constants.INFO_FETCHING_ALL_CYCLES);
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();

        try {
            List<EvaluationCycle> cycles = cycleRepository.findByOrganizationId(orgId);
            if (cycles.isEmpty()) {
                log.warn(Constants.ERROR_NO_CYCLES_FOUND, orgId);
                throw new ResourceNotFoundException(BuildErrorMessage.buildErrorMessage(
                        ErrorType.RESOURCE_NOT_FOUND_ERROR,
                        ErrorCode.RESOURCE_NOT_FOUND,
                        Constants.INFO_NO_EVALUATION_CYCLES_FOUND_FOR_ORGANIZATION
                ));
            }
            log.info(Constants.INFO_FETCHED_EVALUATION_CYCLES_FOR_ORG_ID, cycles.size(), orgId);
            return cycles;
        } catch (Exception e) {
            log.error(Constants.ERROR_NO_CYCLES_FOUND, e);
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.INTERNAL_SERVER_ERROR,
                    ErrorCode.DATABASE_ERROR,
                    Constants.ERROR_FAILED_TO_FETCH_EVALUATION_CYCLES
            ));
        }
    }

    /**
     * Fetch a single evaluation cycle by ID and validate deadlines.
     */
    @Override
    public EvaluationCycle getCycleById(String id) {
        log.info(Constants.INFO_FETCHING_EVALUATIONCYCLE_BY_ID, id);
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();

        EvaluationCycle cycle = cycleRepository.findByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> {
                    log.error(Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND + id);
                    return new ResourceNotFoundException(
                            BuildErrorMessage.buildErrorMessage(
                                    ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                    ErrorCode.RESOURCE_NOT_FOUND,
                                    Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND + id
                            )
                    );
                });

        validateCycleAccess(cycle);
        log.info(Constants.ERROR_FAILED_TO_FETCH_EVALUATION_CYCLES, id);
        return cycle;
    }

    /**
     * Fetch cycle with questionnaire.
     */
    @Override
    public EvaluationCycleDetailsDto getCycleWithQuestionnaire(String id) {
        log.info(Constants.INFO_FETCHING_EVALUATIONCYCLE_WITH_QUESTIONNAIRE_BY_ID, id);
        EvaluationCycle cycle = getCycleById(id);
        EvaluationCycleDetailsDto dto = new EvaluationCycleDetailsDto(cycle, null);

        if (cycle.getQuestionnaireId() != null) {
            try {
                Questionnaire questionnaire = questionnaireService.getQuestionnaireById(cycle.getQuestionnaireId());
                dto.setQuestions(questionnaire.getQuestions());
                log.info(Constants.INFO_QUESTIONNAIRE_FETCHING_QUESTIONNAIRE_BY_ID, cycle.getQuestionnaireId());
            } catch (ResourceNotFoundException e) {
                log.warn(Constants.ERROR_QUESTIONNAIRE_NOT_FOUND_FOR_CYCLE, id);
            }
        }
        return dto;
    }

    /**
     * Update an existing evaluation cycle.
     */
    @Override
    public EvaluationCycle updateCycle(String id, EvaluationCycle cycle) {
        log.info(Constants.INFO_UPDATING_EVALUATION_CYCLE, id);
        EvaluationCycle existing = getCycleById(id);

        if (existing.getStatus() == CycleStatus.COMPLETED) {
            log.warn(Constants.ERROR_CANNOT_UPDATE_PUBLISHED_CYCLE);
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_OPERATION,
                    Constants.ERROR_CANNOT_UPDATE_PUBLISHED_CYCLE
            ));
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
            EvaluationCycle updated = cycleRepository.save(existing);
            log.info(Constants.INFO_EVALUATION_CYCLE_UPDATED_SUCCESSFULLY, id);
            return updated;
        } catch (Exception e) {
            log.error(Constants.ERROR_UPDATING_EVALUATION_CYCLE, e);
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.INTERNAL_SERVER_ERROR,
                    ErrorCode.DATABASE_ERROR,
                    Constants.ERROR_UPDATING_EVALUATION_CYCLE
            ));
        }
    }

    /**
     * Update a full evaluation cycle and questionnaire.
     */
    @Override
    public EvaluationCycleDetailsDto updateFullCycle(String id, EvaluationCycleDetailsDto dto) {
        log.info(Constants.INFO_FULL_UPDATE_START, id);
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
            try {
                log.info(Constants.INFO_UPDATING_QUESTIONS, id);
                if (updatedCycle.getQuestionnaireId() == null || updatedCycle.getQuestionnaireId().isBlank()) {
                    updatedQuestionnaire = new Questionnaire();
                    updatedQuestionnaire.setQuestions(dto.getQuestions());
                    updatedQuestionnaire = questionnaireService.createQuestionnaire(updatedQuestionnaire);
                    updatedCycle.setQuestionnaireId(updatedQuestionnaire.getId());
                    updatedCycle = updateCycle(id, updatedCycle);
                } else {
                    updatedQuestionnaire = questionnaireService.updateQuestions(
                            updatedCycle.getQuestionnaireId(), dto.getQuestions());
                }
            } catch (Exception e) {
                log.error(Constants.ERROR_UPDATING_QUESTIONNAIRE, e);
                throw new InvalidOperationException(ErrorUtils.formatError(
                        ErrorType.INTERNAL_SERVER_ERROR,
                        ErrorCode.DATABASE_ERROR,
                        Constants.ERROR_UPDATING_QUESTIONNAIRE
                ));
            }
        }

        log.info(Constants.INFO_FULL_UPDATE_COMPLETED, id);
        return new EvaluationCycleDetailsDto(updatedCycle, updatedQuestionnaire);
    }

    /**
     * Update cycle status.
     */
    @Override
    public EvaluationCycle updateCycleStatus(String id, CycleStatus status) {
        log.info(Constants.INFO_UPDATING_EVALUATION_CYCLE_STATUS, id);
        if (status == null) {
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_INPUT,
                    Constants.ERROR_STATUS_CANNOT_BE_NULL
            ));
        }

        EvaluationCycle cycle = getCycleById(id);
        validateStatusTransition(cycle.getStatus(), status);
        cycle.setStatus(status);

        try {
            EvaluationCycle updated = cycleRepository.save(cycle);
            log.info(Constants.INFO_UPDATED_CYCLE_STATUS, status, id);
            return updated;
        } catch (Exception e) {
            log.error(Constants.ERROR_FAILED_UPDATE_CYCLE_STATUS, id, e);
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.INTERNAL_SERVER_ERROR,
                    ErrorCode.DATABASE_ERROR,
                    Constants.ERROR_UPDATING_EVALUATION_CYCLE_STATUS
            ));
        }
    }

    /**
     * Validate dates and prevent expired form access.
     */
    private void validateCycleAccess(EvaluationCycle cycle) {
        LocalDate today = LocalDate.now();
        log.info(Constants.INFO_VALIDATING_ACCESS_FOR_CYCLE_ID, cycle.getId(), today);

        if (cycle.getEndDate() != null && today.isAfter(cycle.getEndDate())) {
            log.warn(Constants.ERROR_FORM_EXPIRED_END_DATE_PASSED, cycle.getId());
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_OPERATION,
                    Constants.ERROR_EVALUATION_FORM_EXPIRED_END_DATE_PASSED
            ));
        }

        if (cycle.getSelfEvalDeadline() != null && today.isAfter(cycle.getSelfEvalDeadline())) {
            log.warn(Constants.ERROR_SELF_EVALUATION_DEADLINE_PASSED_FOR_CYCLE, cycle.getId());
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_OPERATION,
                    Constants.ERROR_SELF_EVALUATION_DEADLINE_PASSED_FOR_CYCLE
            ));
        }

        if (cycle.getFeedbackDeadline() != null && today.isAfter(cycle.getFeedbackDeadline())) {
            log.warn(Constants.ERROR_FEEDBACK_DEADLINE_PASSED_FOR_CYCLE, cycle.getId());
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_OPERATION,
                    Constants.ERROR_FEEDBACK_DEADLINE_PASSED_FOR_CYCLE
            ));
        }
    }

    private void validateCycleDates(EvaluationCycle cycle) {
        LocalDate start = cycle.getStartDate();
        LocalDate end = cycle.getEndDate();
        LocalDate selfEval = cycle.getSelfEvalDeadline();
        LocalDate feedback = cycle.getFeedbackDeadline();

        if (start == null || end == null || selfEval == null || feedback == null)
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_DATE, Constants.MISSING_CYCLE_DATE_FIELDS));

        if (selfEval.isBefore(start))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_SELF_EVAL_DEADLINE, Constants.SELF_EVAL_DEADLINE_BEFORE_START));

        if (feedback.isBefore(end))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_DATE, Constants.FEEDBACK_DEADLINE_BEFORE_END_DATE));

        if (start.isAfter(end))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_DATE, Constants.START_DATE_AFTER_END_DATE));

        if (selfEval.isAfter(feedback))
            throw new InvalidOperationException(ErrorUtils.formatError(
                    ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_SELF_EVAL_DEADLINE, Constants.INFO_SELF_EVAL_DEADLINE_ERROR));
    }

    private void validateStatusTransition(CycleStatus current, CycleStatus target) {
        if (current == CycleStatus.IN_PROGRESS && target != CycleStatus.COMPLETED)
            throw new InvalidOperationException("An in-progress cycle can only be marked as completed");
        if (current == CycleStatus.COMPLETED)
            throw new InvalidOperationException("A completed cycle cannot be modified or reopened");
    }

    /**
     * Delete cycle.
     */
    @Override
    public void deleteCycle(String id) {
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        log.info(Constants.INFO_DELETING_CYCLE_ID_FOR_ORG_ID, id, orgId);

        if (!cycleRepository.existsByIdAndOrganizationId(id, orgId)) {
            log.error(Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND + id);
            throw new ResourceNotFoundException(BuildErrorMessage.buildErrorMessage(
                    ErrorType.RESOURCE_NOT_FOUND_ERROR,
                    ErrorCode.RESOURCE_NOT_FOUND,
                    Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND + id
            ));
        }

        cycleRepository.deleteById(id);
        log.info(Constants.INFO_DELETED_EVALUATION_CYCLE, id);
    }

    @Override
    public EvaluationCycle getCurrentActiveCycle(CycleStatus status) {
        LocalDate today = LocalDate.now();
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        log.info(Constants.INFO_FETCHING_ACTIVE_EVALUATION_CYCLE_FOR_ORG_ID_AND_STATUS, orgId, status);

        return cycleRepository.findByOrganizationIdAndStatusAndStartDateLessThanEqualAndFeedbackDeadlineGreaterThanEqual(
                        orgId, status, today, today)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.NOT_FOUND,
                                ErrorCode.CYCLE_NOT_FOUND,
                                Constants.ERROR_NO_ACTIVE_EVALUATION_CYCLE
                        )
                ));
    }

    @Override
    public List<EvaluationCycle> getCyclesByStatus(CycleStatus status) {
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        log.info(Constants.INFO_FETCH_CYCLES_BY_STATUS, status);

        List<EvaluationCycle> cycles = cycleRepository.findByOrganizationIdAndStatus(orgId, status);
        if (cycles.isEmpty()) {
            log.warn(Constants.ERROR_NO_CYCLES_FOUND, status);
            throw new ResourceNotFoundException(BuildErrorMessage.buildErrorMessage(
                    ErrorType.RESOURCE_NOT_FOUND_ERROR,
                    ErrorCode.RESOURCE_NOT_FOUND,
                    Constants.INFO_NO_EVALUATION_CYCLES_FOUND_WITH_STATUS + status
            ));
        }
        log.info(Constants.INFO_FETCHED_CYCLES_WITH_STATUS, cycles.size(), status);
        return cycles;
    }
}