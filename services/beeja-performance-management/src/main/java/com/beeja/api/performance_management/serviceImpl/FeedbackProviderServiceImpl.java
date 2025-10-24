package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.client.AccountClient;
import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.exceptions.BadRequestException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.FeedbackProvider;
import com.beeja.api.performance_management.model.dto.AssignedReviewer;
import com.beeja.api.performance_management.model.dto.EmployeeIdNameDTO;
import com.beeja.api.performance_management.repository.FeedbackProviderRepository;
import com.beeja.api.performance_management.request.FeedbackProviderRequest;
import com.beeja.api.performance_management.response.FeedbackProviderDetails;
import com.beeja.api.performance_management.service.FeedbackProvidersService;
import com.beeja.api.performance_management.utils.BuildErrorMessage;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.beeja.api.performance_management.model.dto.ReviewerDetailsDTO;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FeedbackProviderServiceImpl implements FeedbackProvidersService {

    @Autowired
    AccountClient accountClient;

    @Autowired
    private FeedbackProviderRepository feedbackProviderRepository;

    @Override
    public List<FeedbackProvider> assignFeedbackProvider(FeedbackProviderRequest requestDto) {
        List<FeedbackProvider> savedForms = new ArrayList<>();

        String orgId = UserContext.getLoggedInUserOrganization().get("id").toString();

        try{
            if (requestDto == null) {
                log.error("FeedbackFormRequest is null");
                throw new BadRequestException(
                        ErrorType.VALIDATION_ERROR + "," +
                                ErrorCode.FIELD_VALIDATION_MISSING + "," +
                                "Request body cannot be null");
            }
            for (String receiverId : requestDto.getEmployeeIds()) {

                List<AssignedReviewer> reviewers = requestDto.getAssignedReviewers().stream()
                        .filter(r -> !r.getReviewerId().equals(receiverId))
                        .map(r -> {
                            AssignedReviewer rev = new AssignedReviewer();
                            rev.setReviewerId(r.getReviewerId());
                            rev.setRole(r.getRole());
                            rev.setStatus(ProviderStatus.IN_PROGRESS);
                            return rev;
                        })
                        .toList();

                feedbackProviderRepository.findByOrganizationIdAndEmployeeIdAndCycleId(orgId, receiverId, requestDto.getCycleId())
                        .ifPresentOrElse(
                                existing -> savedForms.add(existing),
                                () -> {
                                    FeedbackProvider form = new FeedbackProvider();
                                    form.setOrganizationId(orgId);
                                    form.setEmployeeId(receiverId);
                                    form.setCycleId(requestDto.getCycleId());
                                    form.setQuestionnaireId(requestDto.getQuestionnaireId());
                                    form.setAssignedReviewers(reviewers);
                                    form.setProviderStatus(ProviderStatus.IN_PROGRESS);
                                    try {
                                        savedForms.add(feedbackProviderRepository.save(form));
                                        log.info("Saved feedback form for employeeId={}", receiverId);
                                    } catch (Exception e) {
                                        log.error("Error saving feedback form for employeeId={}: {}", receiverId, e.getMessage(), e);
                                        throw e;
                                    }
                                }
                        );
            }
        }catch (BadRequestException e) {
            log.warn("BadRequestException: {}", e.getMessage());
        }
        log.info("Total feedback forms processed: {}", savedForms.size());
        return savedForms;
    }

    @Override
    public List<FeedbackProvider> updateFeedbackProviders(FeedbackProviderRequest request, String employeeId) {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        log.info("Updating feedback providers for employeeId={}, orgId={}", employeeId, organizationId);

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.NUll_VALUE,
                            "Employee ID must not be null or empty"
                    )
            );
        }

        if (request.getAssignedReviewers() == null || request.getAssignedReviewers().isEmpty()) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.NUll_VALUE,
                            "Assigned reviewers list must not be empty"
                    )
            );
        }

        FeedbackProvider existingForm = feedbackProviderRepository
                .findByOrganizationIdAndEmployeeIdAndCycleId(organizationId, employeeId, request.getCycleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                "Feedback form not found for employeeId=" + employeeId
                        )
                ));

        if (!existingForm.getCycleId().equals(request.getCycleId())) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Cycle ID mismatch for employeeId=" + employeeId
                    )
            );
        }

        // Validate questionnaireId
        if (!existingForm.getQuestionnaireId().equals(request.getQuestionnaireId())) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Questionnaire ID mismatch for employeeId=" + employeeId
                    )
            );
        }

        List<AssignedReviewer> reviewers = request.getAssignedReviewers().stream()
                .filter(r -> !r.getReviewerId().equals(employeeId))
                .map(r -> {
                    AssignedReviewer rev = new AssignedReviewer();
                    rev.setReviewerId(r.getReviewerId());
                    rev.setRole(r.getRole());
                    rev.setStatus(ProviderStatus.IN_PROGRESS);
                    return rev;
                })
                .toList();

        existingForm.setEmployeeId(employeeId);
        existingForm.setOrganizationId(organizationId);
        existingForm.setAssignedReviewers(reviewers);
        existingForm.setProviderStatus(ProviderStatus.IN_PROGRESS);

        FeedbackProvider savedForm = feedbackProviderRepository.save(existingForm);

        log.info("✅ Updated feedback provider for employeeId={} with {} reviewers and status={}",
                employeeId, reviewers.size(), existingForm.getProviderStatus());

        return List.of(savedForm);
    }


    @Override
    public FeedbackProviderDetails getFeedbackFormDetails(String employeeId, String cycleId, String providerStatus) {
        try {
            String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

            List<FeedbackProvider> feedbackForm = feedbackProviderRepository
                    .findByOrganizationIdAndEmployeeIdAndCycleId(organizationId, employeeId, cycleId)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());


            List<EmployeeIdNameDTO> employeeDetails = accountClient.getEmployeeNamesById(List.of(employeeId));
            String employeeName = employeeDetails.isEmpty() ? "Unknown" : employeeDetails.get(0).getFullName();

            List<AssignedReviewer> assignedReviewers = feedbackForm.isEmpty()
                    ? Collections.emptyList()
                    : Optional.ofNullable(feedbackForm.get(0).getAssignedReviewers())
                    .orElse(Collections.emptyList());

            List<String> reviewerIds = assignedReviewers.stream()
                    .map(AssignedReviewer::getReviewerId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            Map<String, String> reviewerNameMap;
            if (!reviewerIds.isEmpty()) {
                List<EmployeeIdNameDTO> reviewerDetails = accountClient.getEmployeeNamesById(reviewerIds);

                reviewerDetails.stream()
                        .filter(dto -> dto.getEmployeeId() == null || dto.getFullName() == null)
                        .forEach(dto -> log.warn("Invalid reviewer detail returned: {}", dto));

                reviewerNameMap = reviewerDetails.stream()
                        .filter(dto -> dto.getEmployeeId() != null && dto.getFullName() != null)
                        .collect(Collectors.toMap(
                                EmployeeIdNameDTO::getEmployeeId,
                                EmployeeIdNameDTO::getFullName,
                                (existing, replacement) -> existing
                        ));
            } else {
                reviewerNameMap = Collections.emptyMap();
            }

            List<ReviewerDetailsDTO> reviewers = assignedReviewers.stream()
                    .filter(r -> providerStatus == null ||
                            (r.getStatus() != null && r.getStatus().toString().equalsIgnoreCase(providerStatus)))
                    .map(r -> ReviewerDetailsDTO.builder()
                            .reviewerId(r.getReviewerId())
                            .reviewerName(reviewerNameMap.getOrDefault(r.getReviewerId(), "Unknown"))
                            .role(r.getRole())
                            .providerStatus(r.getStatus())
                            .build())
                    .collect(Collectors.toList());

            return FeedbackProviderDetails.builder()
                    .employeeId(employeeId)
                    .employeeName(employeeName)
                    .assignedReviewers(reviewers)
                    .build();

        } catch (Exception e) {
            log.error("Exception in getFeedbackFormDetails: ", e);
            throw e;
        }
    }
}
