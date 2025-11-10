package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.client.AccountClient;
import com.beeja.api.performance_management.client.EmployeeFeignClient;
import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.exceptions.BadRequestException;
import com.beeja.api.performance_management.exceptions.FeignClientException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.FeedbackProvider;
import com.beeja.api.performance_management.model.dto.*;
import com.beeja.api.performance_management.repository.FeedbackProviderRepository;
import com.beeja.api.performance_management.request.FeedbackProviderRequest;
import com.beeja.api.performance_management.response.FeedbackFormSummaryResponse;
import com.beeja.api.performance_management.response.FeedbackProviderDetails;
import com.beeja.api.performance_management.response.ReviewerAssignedEmployeesResponse;
import com.beeja.api.performance_management.service.EvaluationCycleService;
import com.beeja.api.performance_management.service.FeedbackProvidersService;
import com.beeja.api.performance_management.utils.BuildErrorMessage;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FeedbackProviderServiceImpl implements FeedbackProvidersService {

    @Autowired
    AccountClient accountClient;

    @Autowired
    EmployeeFeignClient employeeFeignClient;

    @Autowired
    private FeedbackProviderRepository feedbackProviderRepository;

    @Autowired
    private EvaluationCycleService evaluationCycleService;


    @Override
    public List<FeedbackProvider> assignFeedbackProvider(String employeeId, FeedbackProviderRequest requestDto) {
        List<FeedbackProvider> savedForms = new ArrayList<>();
        String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();

        try {
            if (requestDto == null) {
                log.error(Constants.FEEDBACK_REQUEST_NULL);
                throw new BadRequestException(
                        ErrorType.VALIDATION_ERROR + "," +
                                ErrorCode.FIELD_VALIDATION_MISSING + "," +
                                Constants.FEEDBACK_REQUEST_NULL);
            }

            List<AssignedReviewer> reviewers = requestDto.getAssignedReviewers().stream()
                    .filter(r -> !r.getReviewerId().equals(employeeId))
                    .map(r -> {
                        AssignedReviewer rev = new AssignedReviewer();
                        rev.setReviewerId(r.getReviewerId());
                        rev.setRole(r.getRole());
                        rev.setStatus(ProviderStatus.IN_PROGRESS);
                        return rev;
                    })
                    .toList();

            feedbackProviderRepository.findByOrganizationIdAndEmployeeIdAndCycleId(orgId, employeeId, requestDto.getCycleId())
                    .ifPresentOrElse(
                            existing -> savedForms.add(existing),
                            () -> {
                                FeedbackProvider form = new FeedbackProvider();
                                form.setOrganizationId(orgId);
                                form.setEmployeeId(employeeId);
                                form.setCycleId(requestDto.getCycleId());
                                form.setQuestionnaireId(requestDto.getQuestionnaireId());
                                form.setAssignedReviewers(reviewers);
                                try {
                                    savedForms.add(feedbackProviderRepository.save(form));
                                } catch (Exception e) {
                                    log.error(Constants.ERROR_ASSIGNING_FEEDBACK_PROVIDER, employeeId, e);
                                    throw e;
                                }
                            }
                    );

        } catch (BadRequestException e) {
            log.warn(Constants.WARN_BAD_REQUEST_EXCEPTION, e.getMessage());
        }

        log.info(Constants.INFO_FEEDBACK_FORM_PROCESSED, employeeId);
        return savedForms;
    }

    @Override
    public List<FeedbackProvider> updateFeedbackProviders(FeedbackProviderRequest request, String employeeId) {
        String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        log.info(Constants.INFO_UPDATING_FEEDBACK_PROVIDERS, employeeId, organizationId);

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.NUll_VALUE,
                            Constants.EMPLOYEE_ID_NOT_EMPTY
                    )
            );
        }

        if (request.getAssignedReviewers() == null || request.getAssignedReviewers().isEmpty()) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.NUll_VALUE,
                            Constants.ASSIGNED_REVIEWERS_NOT_EMPTY
                    )
            );
        }

        FeedbackProvider existingForm = feedbackProviderRepository
                .findByOrganizationIdAndEmployeeIdAndCycleId(organizationId, employeeId, request.getCycleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.PROVIDERS_NOT_FOUND + employeeId
                        )
                ));

        if (!existingForm.getCycleId().equals(request.getCycleId())) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.CYCLE_ID_MISMATCH + employeeId
                    )
            );
        }

        if (!existingForm.getQuestionnaireId().equals(request.getQuestionnaireId())) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.QUESTIONNAIRE_ID_MISMATCH + employeeId
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

        FeedbackProvider savedForm = feedbackProviderRepository.save(existingForm);

        return List.of(savedForm);
    }


    @Override
    public FeedbackProviderDetails getFeedbackFormDetails(String employeeId, String cycleId, String providerStatus) {
        try {
            String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();

            List<FeedbackProvider> feedbackForm = feedbackProviderRepository
                    .findByOrganizationIdAndEmployeeIdAndCycleId(organizationId, employeeId, cycleId)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());


            List<EmployeeIdNameDTO> employeeDetails = accountClient.getEmployeeNamesById(List.of(employeeId));
            String employeeName = employeeDetails.isEmpty() ? "-" : employeeDetails.get(0).getFullName();

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
                        .forEach(dto -> log.warn(Constants.INVALID_REVIEWER_DETAIL_RETURNED, dto));

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
                            .reviewerName(reviewerNameMap.getOrDefault(r.getReviewerId(), "-"))
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
            log.error(Constants.WARN_BAD_REQUEST_EXCEPTION, e.getMessage());
            throw e;
        }
    }

    @Override
    public ReviewerAssignedEmployeesResponse getEmployeesAssignedToReviewer() {
        try {
            String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
            String reviewerId = UserContext.getLoggedInEmployeeId();

            List<FeedbackProvider> providers = feedbackProviderRepository.findByOrganizationId(organizationId);

            List<FeedbackProvider> matchedProviders = providers.stream()
                    .filter(p -> p.getAssignedReviewers() != null &&
                            p.getAssignedReviewers().stream()
                                    .anyMatch(r -> reviewerId.equals(r.getReviewerId())))
                    .toList();

            if (matchedProviders.isEmpty()) {
                return ReviewerAssignedEmployeesResponse.builder()
                        .reviewerId(reviewerId)
                        .reviewerName("Unknown")
                        .assignedEmployees(List.of())
                        .build();
            }

            List<String> employeeIds = matchedProviders.stream()
                    .map(FeedbackProvider::getEmployeeId)
                    .distinct()
                    .toList();

            List<EmployeeIdNameDTO> employeeDetails = accountClient.getEmployeeNamesById(employeeIds);
            List<EmployeeDepartmentDTO> departmentDetails = employeeFeignClient.getDepartmentsByEmployeeIds(employeeIds);

            Map<String, String> employeeNameMap = employeeDetails.stream()
                    .filter(e -> e.getEmployeeId() != null && e.getFullName() != null)
                    .collect(Collectors.toMap(EmployeeIdNameDTO::getEmployeeId, EmployeeIdNameDTO::getFullName));

            Map<String, String> employeeDepartmentMap = departmentDetails.stream()
                    .filter(e -> e.getEmployeeId() != null && e.getDepartment() != null)
                    .collect(Collectors.toMap(EmployeeDepartmentDTO::getEmployeeId, EmployeeDepartmentDTO::getDepartment));

            List<EmployeeIdNameDTO> reviewerDetails = accountClient.getEmployeeNamesById(List.of(reviewerId));
            String reviewerName = reviewerDetails.isEmpty() ? "Unknown" : reviewerDetails.get(0).getFullName();

            Map<String, ReviewerEmployeeDTO> employeeMap = new LinkedHashMap<>();

            for (FeedbackProvider provider : matchedProviders) {
                String empId = provider.getEmployeeId();

                AssignedReviewer reviewer = provider.getAssignedReviewers().stream()
                        .filter(r -> reviewerId.equals(r.getReviewerId()))
                        .findFirst()
                        .orElse(null);

                boolean isSubmitted = reviewer != null && ProviderStatus.COMPLETED.equals(reviewer.getStatus());

                ReviewerEmployeeDTO employeeDTO = employeeMap.computeIfAbsent(empId, id ->
                        ReviewerEmployeeDTO.builder()
                                .employeeId(id)
                                .employeeName(employeeNameMap.getOrDefault(id, "Unknown"))
                                .department(employeeDepartmentMap.getOrDefault(id, "Unknown"))
                                .role(reviewer != null ? reviewer.getRole() : "Unknown")
                                .feedbackCycles(new ArrayList<>())
                                .build()
                );

                employeeDTO.getFeedbackCycles().add(
                        FeedbackCycleDTO.builder()
                                .cycleId(provider.getCycleId())
                                .submitted(isSubmitted)
                                .build()
                );
            }

            return ReviewerAssignedEmployeesResponse.builder()
                    .reviewerId(reviewerId)
                    .reviewerName(reviewerName)
                    .assignedEmployees(new ArrayList<>(employeeMap.values()))
                    .build();

        } catch (Exception e) {
            log.error(Constants.ERROR_FETCHING_ASSIGNED_EMPLOYEES, e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public List<FeedbackFormSummaryResponse> getFormsByEmployeeAndReviewer(String employeeId, String reviewerId) {
        try {
            String organizationId = UserContext.getLoggedInUserOrganization()
                    .get(Constants.ID)
                    .toString();

            List<FeedbackProvider> providers = feedbackProviderRepository
                    .findByOrganizationIdAndEmployeeId(organizationId, employeeId)
                    .stream()
                    .filter(p -> p.getAssignedReviewers().stream()
                            .anyMatch(r -> reviewerId.equals(r.getReviewerId())))
                    .toList();

            if (providers.isEmpty()) {
                return Collections.emptyList();
            }

            return providers.stream()
                    .map(provider -> {
                        String cycleName = "Unknown Cycle";
                        try {
                            var cycle = evaluationCycleService.getCycleById(provider.getCycleId());
                            if (cycle != null && cycle.getName() != null) {
                                cycleName = cycle.getName();
                            }
                        } catch (Exception e) {
                            log.warn(Constants.ERROR_FETCHING_CYCLE_NAME, provider.getCycleId(), e.getMessage());
                        }

                        String status = provider.getAssignedReviewers().stream()
                                .filter(r -> reviewerId.equals(r.getReviewerId()))
                                .findFirst()
                                .map(r -> r.getStatus().toString())
                                .orElse("NOT_STARTED");

                        return FeedbackFormSummaryResponse.builder()
                                .cycleId(provider.getCycleId())
                                .cycleName(cycleName)
                                .status(status)
                                .build();
                    })
                    .toList();

        } catch (Exception e) {
            log.error(Constants.ERROR_FETCHING_FORMS, employeeId, reviewerId, e.getMessage(), e);
            throw e;
        }
    }
}
