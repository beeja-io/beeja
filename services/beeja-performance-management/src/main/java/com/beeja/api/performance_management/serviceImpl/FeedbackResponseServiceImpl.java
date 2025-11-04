package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.enums.FormStatus;
import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.exceptions.BadRequestException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.*;
import com.beeja.api.performance_management.model.dto.*;
import com.beeja.api.performance_management.repository.EvaluationCycleRepository;
import com.beeja.api.performance_management.repository.FeedbackProviderRepository;
import com.beeja.api.performance_management.repository.FeedbackResponseRepository;
import com.beeja.api.performance_management.service.FeedbackResponseService;
import com.beeja.api.performance_management.utils.BuildErrorMessage;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * Implementation of {@link FeedbackResponseService}.
 * Handles feedback submission and retrieval for employees and cycles.
 */
@Service
@Slf4j
public class FeedbackResponseServiceImpl implements FeedbackResponseService {

    private final FeedbackResponseRepository repository;
    private final EvaluationCycleRepository cycleRepository;

    @Autowired
    private FeedbackProviderRepository feedbackProviderRepository;

    public FeedbackResponseServiceImpl(FeedbackResponseRepository repository,
                                       EvaluationCycleRepository cycleRepository) {
        this.repository = repository;
        this.cycleRepository = cycleRepository;
    }

    /**
     * Retrieves organization ID from user context.
     */
    private String getOrgId() {
        Map<String, Object> org = UserContext.getLoggedInUserOrganization();
        if (org == null || org.get("id") == null) {
            throw new BadRequestException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.UNAUTHORIZED_ACCESS,
                            Constants.ORG_ID_NOT_FOUND_IN_CONTEXT
                    ).toString()
            );
        }
        return org.get("id").toString();
    }

    /**
     * Submits feedback for a given form and employee.
     * Automatically marks the reviewer as COMPLETED in FeedbackProvider.
     */
    @Override
    public FeedbackResponse submitFeedback(SubmitFeedbackRequest req) {
        if (req == null) {
            throw new BadRequestException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.FIELD_VALIDATION_MISSING,
                            Constants.REQUEST_BODY_CANNOT_BE_NULL
                    ).toString()
            );
        }

        FeedbackResponse response = new FeedbackResponse();
        response.setFormId(req.getFormId());
        response.setEmployeeId(req.getEmployeeId());
        response.setCycleId(req.getCycleId());
        response.setReviewerId(req.getReviewerId());
        response.setReviewerRole(req.getReviewerRole());
        response.setResponses(req.getResponses());
        response.setSubmittedAt(Instant.now());
        response.setStatus(FormStatus.COMPLETED);
        response.setOrganizationId(getOrgId());

        try {
            FeedbackResponse saved = repository.save(response);
            log.info(Constants.FEEDBACK_SUBMITTED_SUCCESSFULLY, req.getEmployeeId(), req.getFormId());

            String orgId = getOrgId();

            feedbackProviderRepository
                    .findByOrganizationIdAndEmployeeIdAndCycleId(orgId, req.getEmployeeId(), req.getCycleId())
                    .ifPresent(provider -> {
                        boolean updated = false;
                        for (AssignedReviewer reviewer : provider.getAssignedReviewers()) {
                            if (reviewer.getReviewerId().equals(req.getReviewerId())) {
                                reviewer.setStatus(ProviderStatus.COMPLETED);
                                updated = true;
                            }
                        }

                        if (updated) {
                            feedbackProviderRepository.save(provider);
                            log.info(Constants.REVIEWER_MARKED_COMPLETED, req.getReviewerId(), req.getEmployeeId());
                        } else {
                            log.warn(Constants.REVIEWER_NOT_FOUND_IN_FEEDBACK_PROVIDER, req.getReviewerId(), req.getEmployeeId());
                        }
                    });

            return saved;

        } catch (Exception e) {
            log.error(Constants.ERROR_SAVING_FEEDBACK_RESPONSE, e.getMessage(), e);
            throw new RuntimeException(Constants.ERROR_SAVING_FEEDBACK_RESPONSE_SIMPLE, e);
        }
    }

    /**
     * Fetches feedback responses for a given form.
     */
    @Override
    public List<FeedbackResponse> getByFormId(String formId) {
        if (formId == null || formId.isBlank()) {
            throw new BadRequestException("Form ID cannot be null or empty");
        }
        return repository.findByFormIdAndOrganizationId(formId, getOrgId());
    }

    /**
     * Fetches feedback responses for a given employee and cycle.
     */
    @Override
    public List<FeedbackResponse> getByEmployeeAndCycle(String employeeId, String cycleId) {
        if (employeeId == null || cycleId == null) {
            throw new BadRequestException("EmployeeId and CycleId must not be null");
        }
        return repository.findByEmployeeIdAndCycleIdAndOrganizationId(employeeId, cycleId, getOrgId());
    }

    /**
     * Fetches all feedback responses for a given employee.
     */
    @Override
    public List<FeedbackResponse> getByEmployee(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new BadRequestException("EmployeeId cannot be null or empty");
        }
        return repository.findByEmployeeIdAndOrganizationId(employeeId, getOrgId());
    }

    /**
     * Returns grouped responses for an employee along with cycle details.
     */
    @Override
    public EmployeeGroupedResponsesDTO getGroupedResponsesWithCycleByEmployee(String employeeId) {
        List<FeedbackResponse> responses =
                repository.findByEmployeeIdAndOrganizationId(employeeId, getOrgId());

        if (responses.isEmpty()) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.RESOURCE_NOT_FOUND_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.NO_RESPONSES_FOUND_FOR_EMPLOYEE + employeeId
                    )
            );
        }

        String cycleId = responses.get(0).getCycleId();
        EvaluationCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.CYCLE_NOT_FOUND_FOR_ID + cycleId
                        )
                ));

        Map<String, QuestionResponseDTO> groupedResponses = new HashMap<>();
        for (FeedbackResponse response : responses) {
            for (QuestionAnswer qa : response.getResponses()) {
                groupedResponses.computeIfAbsent(qa.getQuestionId(), id -> {
                    QuestionResponseDTO dto = new QuestionResponseDTO();
                    dto.setQuestionId(id);
                    return dto;
                }).getResponses().add(qa.getAnswer());
            }
        }

        return new EmployeeGroupedResponsesDTO(cycle, new ArrayList<>(groupedResponses.values()));
    }

    /**
     * Fetches all feedback responses for a given evaluation cycle.
     */
    @Override
    public CycleWithResponsesDTO getResponsesForCycle(String cycleId) {
        EvaluationCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.EVALUATION_CYCLE_NOT_FOUND_WITH_ID + cycleId
                        )
                ));

        List<FeedbackResponse> responses = repository.findByCycleIdAndOrganizationId(cycleId, getOrgId());
        return new CycleWithResponsesDTO(cycle, responses);
    }
}
