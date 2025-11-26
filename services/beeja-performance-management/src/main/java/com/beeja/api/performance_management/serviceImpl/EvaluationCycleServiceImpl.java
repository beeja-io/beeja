package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.exceptions.BadRequestException;
import com.beeja.api.performance_management.exceptions.InvalidOperationException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.Questionnaire;
import com.beeja.api.performance_management.model.dto.EvaluationCycleCreateDto;
import com.beeja.api.performance_management.model.dto.EvaluationCycleDetailsDto;
import com.beeja.api.performance_management.model.dto.ReceiverDetails;
import com.beeja.api.performance_management.repository.EvaluationCycleRepository;
import com.beeja.api.performance_management.repository.FeedbackProviderRepository;
import com.beeja.api.performance_management.repository.FeedbackReceiverRepository;
import com.beeja.api.performance_management.repository.FeedbackResponseRepository;
import com.beeja.api.performance_management.response.ReceiverResponse;
import com.beeja.api.performance_management.service.EvaluationCycleService;
import com.beeja.api.performance_management.service.FeedbackReceiversService;
import com.beeja.api.performance_management.service.QuestionnaireService;
import com.beeja.api.performance_management.utils.BuildErrorMessage;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.ErrorUtils;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @Autowired
    private FeedbackProviderRepository feedbackProviderRepository;

    @Autowired
    private FeedbackReceiverRepository feedbackReceiverRepository;

    @Autowired
    private FeedbackResponseRepository feedbackResponseRepository;

    @Autowired
    private FeedbackReceiversService feedbackReceiversService;

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

        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        EvaluationCycle newCycle = new EvaluationCycle();
        newCycle.setOrganizationId(orgId);

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
     * Flow:
     *  1) Create and save EvaluationCycle first (so we have cycleId).
     *  2) If questions are provided, create Questionnaire with organizationId + cycleId.
     *  3) Attach questionnaireId back to the saved cycle.
     *
     * @param dto The data transfer object containing details of the evaluation cycle and optional questions.
     * @return An {@link EvaluationCycleDetailsDto} containing the persisted evaluation cycle and the linked questionnaire (if created).
     * @throws InvalidOperationException if there is an error during questionnaire creation or cycle persistence.
     */
    @Override
    public EvaluationCycleDetailsDto createCycleWithQuestions(EvaluationCycleCreateDto dto) {
        log.info(Constants.INFO_CREATING_CYCLE_WITH_QUESTIONS, dto.getName());

        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();

        // 1) Build and save the EvaluationCycle first
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

        Questionnaire savedQuestionnaire = null;

        // 2) If questions exist, create Questionnaire and tag it with this cycle's id
        if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setOrganizationId(orgId);
            questionnaire.setCycleId(savedCycle.getId());     // IMPORTANT: link questionnaire to this cycle
            questionnaire.setQuestions(dto.getQuestions());

            try {
                // Let BadRequestException (e.g., duplicate questions) bubble up
                savedQuestionnaire = questionnaireService.createQuestionnaire(questionnaire);
            } catch (BadRequestException bre) {
                // Questionnaire validation failed (e.g., duplicate questions)
                // Roll back the cycle we just created so we don't leave an empty/orphan cycle
                rollbackCycleSafe(savedCycle);
                throw bre;
            } catch (Exception e) {
                log.error(Constants.ERROR_CREATING_QUESTIONNAIRE, e);
                // Any other error during questionnaire creation -> also roll back the cycle
                rollbackCycleSafe(savedCycle);
                throw new InvalidOperationException(
                        ErrorUtils.formatError(
                                ErrorType.INTERNAL_SERVER_ERROR,
                                ErrorCode.DATABASE_ERROR,
                                Constants.ERROR_CREATING_QUESTIONNAIRE
                        )
                );
            }

            // 3) Attach questionnaireId to the saved cycle and update it
            if (savedQuestionnaire != null) {
                savedCycle.setQuestionnaireId(savedQuestionnaire.getId());
                try {
                    savedCycle = cycleRepository.save(savedCycle);
                } catch (Exception e) {
                    log.error(Constants.ERROR_UPDATING_EVALUATION_CYCLE, e);
                    throw new InvalidOperationException(
                            ErrorUtils.formatError(
                                    ErrorType.INTERNAL_SERVER_ERROR,
                                    ErrorCode.DATABASE_ERROR,
                                    Constants.ERROR_UPDATING_EVALUATION_CYCLE
                            )
                    );
                }
            }
        }

        log.info(Constants.INFO_EVALUATION_CYCLE_CREATED, savedCycle.getId());
        return new EvaluationCycleDetailsDto(savedCycle, savedQuestionnaire);
    }

    /**
     * Safely roll back (delete) a cycle if something fails while creating its questionnaire.
     */
    private void rollbackCycleSafe(EvaluationCycle savedCycle) {
        if (savedCycle == null || savedCycle.getId() == null) {
            return;
        }
        try {
            cycleRepository.deleteById(savedCycle.getId());
            log.info("Rolled back evaluation cycle with id: {}", savedCycle.getId());
        } catch (Exception ex) {
            // We log this but don't override the original exception
            log.error("Failed to rollback evaluation cycle with id: {}", savedCycle.getId(), ex);
        }
    }

    /**
     * Retrieves all evaluation cycles for the current organization and resolves their statuses
     * using feedback receivers/providers.
     *
     * @return list of evaluation cycles
     */
    @Override
    public List<EvaluationCycle> getAllCycles() {
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        List<EvaluationCycle> cycles = cycleRepository.findByOrganizationId(orgId);

        if (cycles == null || cycles.isEmpty()) {
            log.info(Constants.NO_EVALUATION_CYCLE);
            return List.of();
        }


        List<EvaluationCycle> updatedCycles = new ArrayList<>(cycles.size());

        for (EvaluationCycle cycle : cycles) {
            try {
                ReceiverResponse receiverResponse = feedbackReceiversService.getFeedbackReceiversList(cycle.getId(), cycle.getQuestionnaireId());
                List<ReceiverDetails> receivers = receiverResponse != null ? receiverResponse.getReceivers() : null;

                if (receivers == null || receivers.isEmpty()) {
                    cycle.setStatus(CycleStatus.IN_PROGRESS);
                } else {
                    boolean allCompleted = receivers.stream()
                            .allMatch(r -> r.getProviderStatus() == ProviderStatus.COMPLETED);

                    boolean anyInProgress = receivers.stream()
                            .anyMatch(r -> r.getProviderStatus() == ProviderStatus.IN_PROGRESS);

                    if (allCompleted) {
                        cycle.setStatus(CycleStatus.COMPLETED);
                    } else if (anyInProgress) {
                        cycle.setStatus(CycleStatus.IN_PROGRESS);
                    } else {
                        cycle.setStatus(CycleStatus.IN_PROGRESS);
                    }
                }
            } catch (Exception e) {
                log.error("Error while resolving receiver statuses for cycleId={} questionnaireId={}. Marking IN_PROGRESS. Error: {}",
                        cycle.getId(), cycle.getQuestionnaireId(), e.getMessage(), e);
                cycle.setStatus(CycleStatus.IN_PROGRESS);
            }

            updatedCycles.add(cycle);
        }

        try {
            cycleRepository.saveAll(updatedCycles);
        } catch (Exception e) {
            log.error("Failed to persist updated cycle statuses: {}", e.getMessage(), e);
        }

        return updatedCycles;
    }

    /**
     * Retrieves an evaluation cycle by ID for the current organization.
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
     * Updates an existing evaluation cycle if not completed.
     *
     * @param id    cycle ID
     * @param cycle updated cycle data
     * @return updated cycle
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
     * Performs a full update (cycle + questionnaire).
     *
     * @param id  cycle ID
     * @param dto updated data DTO
     * @return updated cycle details
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
                // No questionnaire yet -> create a new one tied to this cycle
                updatedQuestionnaire = new Questionnaire();
                updatedQuestionnaire.setOrganizationId(cycle.getOrganizationId());
                updatedQuestionnaire.setCycleId(cycle.getId());           // IMPORTANT: link to this cycle
                updatedQuestionnaire.setQuestions(dto.getQuestions());

                updatedQuestionnaire = questionnaireService.createQuestionnaire(updatedQuestionnaire);

                cycle.setQuestionnaireId(updatedQuestionnaire.getId());
                updatedCycle = updateCycle(id, cycle);
            } else {
                // Questionnaire already exists -> update its questions only
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
     * Deletes a cycle by ID.
     *
     * @param id Cycle ID
     * @throws ResourceNotFoundException if not found
     */
    @Override
    public void deleteCycle(String id) {
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();

        EvaluationCycle cycle = cycleRepository.findByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND + id)));


        String questionnaireId = cycle.getQuestionnaireId();

        try {
            log.info(Constants.INFO_DELETING_FEEDBACK_PROVIDERS, id, orgId);
            feedbackProviderRepository.deleteByCycleIdAndOrganizationId(id, orgId);

            log.info(Constants.INFO_DELETING_FEEDBACK_RECEIVERS, id, orgId);
            feedbackReceiverRepository.deleteByCycleIdAndOrganizationId(id, orgId);

            log.info(Constants.INFO_DELETING_FEEDBACK_RESPONSES, id, orgId);
            feedbackResponseRepository.deleteByCycleIdAndOrganizationId(id, orgId);

            if (questionnaireId != null && !questionnaireId.isBlank()) {
                log.info(Constants.INFO_VALIDATING_QUESTIONNAIRE_FOR_DELETION, questionnaireId);

                Questionnaire q = questionnaireService.getQuestionnaireById(questionnaireId);

                if (q.getOrganizationId().equals(orgId)) {
                    log.info(Constants.INFO_DELETING_QUESTIONNAIRE_FOR_ORG, questionnaireId, orgId);
                    questionnaireService.deleteQuestionnaire(questionnaireId);
                } else {
                    log.warn(Constants.WARN_SKIPPING_QUESTIONNAIRE_DELETION_ORG_MISMATCH, questionnaireId);
                }
            }

            log.info(Constants.INFO_DELETING_EVALUATION_CYCLE_FOR_ORG, id, orgId);
            cycleRepository.deleteById(id);

            log.info(Constants.INFO_SUCCESSFULLY_DELETED_CYCLE_AND_DATA, id);

        } catch (Exception e) {
            log.error(Constants.ERROR_DELETING_CYCLE, id, e.getMessage());
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.INTERNAL_SERVER_ERROR,
                            ErrorCode.DATABASE_ERROR,
                            Constants.ERROR_FAILED_TO_DELETE_CYCLE_AND_DATA
                    ));
        }
    }

    /**
     * Fetches the currently active evaluation cycle.
     *
     * @param status desired status (e.g., IN_PROGRESS)
     * @return active evaluation cycle
     *
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
     * Retrieves all cycles by status for the current organization.
     *
     * @param status cycle status
     * @return list of matching cycles
     */
    @Override
    public List<EvaluationCycle> getCyclesByStatus(CycleStatus status) {
        log.info(Constants.INFO_FETCH_CYCLES_BY_STATUS, status);
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        List<EvaluationCycle> cycles = cycleRepository.findByOrganizationIdAndStatus(orgId, status);
        if (cycles == null || cycles.isEmpty()) {
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

        // startDate <= selfEvalDeadline
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
}
