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
import com.beeja.api.performance_management.response.MyFeedbackFormResponse;
import com.beeja.api.performance_management.service.FeedbackResponseService;
import com.beeja.api.performance_management.utils.BuildErrorMessage;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Secure and multi-tenant safe implementation of FeedbackResponseService.
 * All operations are scoped by organizationId to prevent data leaks across tenants.
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

    /** Helper: Safely fetch organization ID from UserContext. */
    private String getOrgId() {
        Map<String, Object> org = UserContext.getLoggedInUserOrganization();
        Object id = (org == null) ? null : org.get(Constants.ID);
        if (id == null) {
            throw new BadRequestException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.UNAUTHORIZED_ACCESS,
                            Constants.ORG_ID_NOT_FOUND_IN_CONTEXT
                    ).toString()
            );
        }
        return id.toString();
    }

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

        String orgId = getOrgId();

        FeedbackResponse response = new FeedbackResponse();
        response.setFormId(req.getFormId());
        response.setEmployeeId(req.getEmployeeId());
        response.setCycleId(req.getCycleId());
        response.setReviewerId(req.getReviewerId());
        response.setReviewerRole(req.getReviewerRole());
        response.setResponses(req.getResponses());
        response.setSubmittedAt(Instant.now());
        response.setStatus(FormStatus.COMPLETED);
        response.setOrganizationId(orgId);

        try {
            FeedbackResponse saved = repository.save(response);
            log.info(Constants.FEEDBACK_SUBMITTED_SUCCESSFULLY, req.getEmployeeId(), req.getFormId());

            // Update feedback provider reviewer status to COMPLETED
            try {
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

            } catch (IncorrectResultSizeDataAccessException e) {
                log.warn(Constants.MULTIPLE_FEEDBACK_PROVIDERS_FOUND,
                        req.getEmployeeId(), req.getCycleId(), orgId);

                List<FeedbackProvider> duplicates = feedbackProviderRepository.findByOrganizationId(orgId)
                        .stream()
                        .filter(p -> req.getEmployeeId().equals(p.getEmployeeId())
                                && req.getCycleId().equals(p.getCycleId()))
                        .toList();

                for (FeedbackProvider provider : duplicates) {
                    for (AssignedReviewer reviewer : provider.getAssignedReviewers()) {
                        if (reviewer.getReviewerId().equals(req.getReviewerId())) {
                            reviewer.setStatus(ProviderStatus.COMPLETED);
                            feedbackProviderRepository.save(provider);
                            log.info(Constants.REVIEWER_MARKED_COMPLETED_DUPLICATE, req.getReviewerId(), provider.getId());
                        }
                    }
                }
            }

            return saved;

        } catch (Exception e) {
            log.error(Constants.ERROR_SAVING_FEEDBACK_RESPONSE, e.getMessage(), e);
            throw new RuntimeException(Constants.ERROR_SAVING_FEEDBACK_RESPONSE_SIMPLE, e);
        }
    }

    @Override
    public List<FeedbackResponse> getByFormId(String formId) {
        if (formId == null || formId.isBlank()) {
            throw new BadRequestException("Form ID cannot be null or empty");
        }
        return repository.findByFormIdAndOrganizationId(formId, getOrgId());
    }

    @Override
    public List<FeedbackResponse> getByEmployeeAndCycle(String employeeId, String cycleId) {
        if (employeeId == null || cycleId == null) {
            throw new BadRequestException("EmployeeId and CycleId must not be null");
        }
        return repository.findByEmployeeIdAndCycleIdAndOrganizationId(employeeId, cycleId, getOrgId());
    }

    @Override
    public List<FeedbackResponse> getByEmployee(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new BadRequestException("EmployeeId cannot be null or empty");
        }
        return repository.findByEmployeeIdAndOrganizationId(employeeId, getOrgId());
    }

    @Override
    public EmployeeGroupedResponsesDTO getGroupedResponsesWithCycleByEmployee(String employeeId) {
        String orgId = getOrgId();
        List<FeedbackResponse> responses =
                repository.findByEmployeeIdAndOrganizationId(employeeId, orgId);

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
        EvaluationCycle cycle = cycleRepository.findByIdAndOrganizationId(cycleId, orgId)
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

    @Override
    public CycleWithResponsesDTO getResponsesForCycle(String cycleId) {
        String orgId = getOrgId();
        EvaluationCycle cycle = cycleRepository.findByIdAndOrganizationId(cycleId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.EVALUATION_CYCLE_NOT_FOUND_WITH_ID + cycleId
                        )
                ));

        List<FeedbackResponse> responses = repository.findByCycleIdAndOrganizationId(cycleId, orgId);
        return new CycleWithResponsesDTO(cycle, responses);
    }

    @Override
    public List<MyFeedbackFormResponse> getMyFeedbackForms() {
        String orgId = getOrgId();
        String employeeId = UserContext.getLoggedInEmployeeId();

        List<FeedbackResponse> myResponses =
                repository.findByEmployeeIdAndOrganizationId(employeeId, orgId);

        if (myResponses.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> cycleIds = myResponses.stream()
                .map(FeedbackResponse::getCycleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<EvaluationCycle> cycles = cycleRepository.findByIdInAndOrganizationId(new ArrayList<>(cycleIds), orgId);

        List<MyFeedbackFormResponse> result = new ArrayList<>();
        for (EvaluationCycle cycle : cycles) {
            result.add(new MyFeedbackFormResponse(
                    cycle.getId(),
                    cycle.getName(),
                    cycle.getType() != null ? cycle.getType().name() : null,
                    cycle.getStatus() != null ? cycle.getStatus().name() : null,
                    cycle.getStartDate() != null ? cycle.getStartDate().toString() : null,
                    cycle.getEndDate() != null ? cycle.getEndDate().toString() : null
            ));
        }

        return result;
    }

    @Override
    public EmployeeGroupedResponsesDTO getMyResponsesByCycle(String cycleId) {
        String orgId = getOrgId();
        String employeeId = UserContext.getLoggedInEmployeeId();

        List<FeedbackResponse> responses =
                repository.findByEmployeeIdAndCycleIdAndOrganizationId(employeeId, cycleId, orgId);

        if (responses.isEmpty()) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.RESOURCE_NOT_FOUND_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.NO_FEEDBACK_RESPONSES_FOUND + cycleId
                    )
            );
        }

        EvaluationCycle cycle = cycleRepository.findByIdAndOrganizationId(cycleId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.CYCLE_NOT_FOUND + cycleId
                        )
                ));

        Map<String, QuestionResponseDTO> grouped = new LinkedHashMap<>();

        for (FeedbackResponse response : responses) {
            for (QuestionAnswer qa : response.getResponses()) {
                if (qa.getQuestionId() != null) {
                    grouped.computeIfAbsent(qa.getQuestionId(), id -> {
                        QuestionResponseDTO dto = new QuestionResponseDTO();
                        dto.setQuestionId(id);
                        return dto;
                    }).getResponses().add(qa.getAnswer());
                }
            }
        }

        return new EmployeeGroupedResponsesDTO(cycle, new ArrayList<>(grouped.values()));
    }
}
