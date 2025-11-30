package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.client.AccountClient;
import com.beeja.api.performance_management.client.EmployeeFeignClient;
import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.FeedbackProvider;
import com.beeja.api.performance_management.model.FeedbackReceivers;
import com.beeja.api.performance_management.model.OverallRating;
import com.beeja.api.performance_management.model.dto.*;
import com.beeja.api.performance_management.repository.EvaluationCycleRepository;
import com.beeja.api.performance_management.repository.FeedbackProviderRepository;
import com.beeja.api.performance_management.repository.FeedbackReceiverRepository;
import com.beeja.api.performance_management.repository.OverallRatingRepository;
import com.beeja.api.performance_management.service.MyTeamOverviewService;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class MyTeamOverviewServiceImpl implements MyTeamOverviewService {

    @Autowired
    private OverallRatingRepository overallRatingRepository;

    @Autowired
    private EvaluationCycleRepository evaluationCycleRepository;

    @Autowired
    private FeedbackReceiverRepository feedbackReceiverRepository;

    @Autowired
    private EmployeeFeignClient employeeFeignClient;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private FeedbackProviderRepository repository;

    @Override
    public PaginatedEmployeePerformanceResponse getEmployeePerformanceData(
            String department,
            String designation,
            String employmentType,
            String status,
            int pageNumber,
            int pageSize) {

        ResponseEntity<List<EmployeeSummaryDTO>> employeeResponse =
                employeeFeignClient.getEmployeesByLoggedInUserOrganization();

        List<EmployeeSummaryDTO> employees =
                Optional.ofNullable(employeeResponse.getBody()).orElse(Collections.emptyList());

        List<BasicUserInfoDTO> users =
                Optional.ofNullable(accountClient.getUsersByLoggedInUserOrganization())
                        .orElse(Collections.emptyList());

        Map<String, BasicUserInfoDTO> userMap = users.stream()
                .filter(u -> u.getEmployeeId() != null)
                .collect(Collectors.toMap(
                        BasicUserInfoDTO::getEmployeeId,
                        u -> u,
                        (a, b) -> a
                ));

        Stream<EmployeeSummaryDTO> filteredStream = employees.stream()
                .filter(emp -> department == null || department.isEmpty() ||
                        (emp.getJobDetails() != null &&
                                department.equalsIgnoreCase(emp.getJobDetails().getDepartment())))
                .filter(emp -> designation == null || designation.isEmpty() ||
                        (emp.getJobDetails() != null &&
                                designation.equalsIgnoreCase(emp.getJobDetails().getDesignation())))
                .filter(emp -> employmentType == null || employmentType.isEmpty() ||
                        (emp.getJobDetails() != null &&
                                employmentType.equalsIgnoreCase(emp.getJobDetails().getEmployementType())))
                .filter(emp -> {

                    BasicUserInfoDTO user = userMap.get(emp.getEmployeeId());
                    if (user == null) return false;

                    return user.isActive();
                });

        List<EmployeeSummaryDTO> filteredEmployees = filteredStream.collect(Collectors.toList());

        List<EmployeePerformanceDTO> merged = filteredEmployees.stream()
                .map(emp -> {
                    EmployeePerformanceDTO dto = new EmployeePerformanceDTO();
                    dto.setEmployeeId(emp.getEmployeeId());
                    dto.setOrganizationId(emp.getOrganizationId());
                    dto.setJobDetails(emp.getJobDetails());
                    dto.setProfilePictureId(emp.getProfilePictureId());
                    Double rating = Optional.ofNullable(getOverallRatingByEmployeeId(emp.getEmployeeId()))
                            .map(OverallRating::getRating)
                            .orElse(null);
                    dto.setOverallRating(rating);
                    FeedbackStatusResponse fsr = getFeedbackStatus(emp.getEmployeeId());
                    dto.setNumberOfReviewersAssigned(fsr.getTotalAssignedReviewers());
                    dto.setNumberOfReviewerResponses(fsr.getFeedbackGivenTillNow());

                    BasicUserInfoDTO user = userMap.get(emp.getEmployeeId());
                    if (user != null) {
                        dto.setFirstName(user.getFirstName());
                        dto.setLastName(user.getLastName());
                        dto.setEmail(user.getEmail());
                        dto.setActive(user.isActive());
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        if (status != null && !status.isEmpty() && !"-".equals(status)) {
            merged = merged.stream()
                    .filter(dto -> {
                        int assigned = dto.getNumberOfReviewersAssigned();
                        int given = dto.getNumberOfReviewerResponses();

                        if ("completed".equalsIgnoreCase(status)) {
                            return assigned == given && assigned != 0;
                        } else if ("incomplete".equalsIgnoreCase(status)) {
                            return (assigned != given);
                        }
                        else if ("notAssigned".equalsIgnoreCase(status)) {
                            return assigned == given && assigned == 0;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        int totalRecords = merged.size();
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        int fromIndex = Math.max(0, (pageNumber - 1) * pageSize);
        int toIndex = Math.min(totalRecords, fromIndex + pageSize);

        List<EmployeePerformanceDTO> paginatedEmployees = new ArrayList<>();
        if (fromIndex < totalRecords) {
            paginatedEmployees = merged.subList(fromIndex, toIndex);
        }

        PaginatedEmployeePerformanceResponse response = new PaginatedEmployeePerformanceResponse();
        response.setData(paginatedEmployees);
        response.setTotalRecords(totalRecords);
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalPages(totalPages);

        return response;
    }

    public FeedbackStatusResponse getFeedbackStatus(String employeeId) {

        List<FeedbackProvider> providerList = repository.findProvidersByOrganizationIdAndEmployeeId(UserContext.getLoggedInUserOrganization().get(Constants.ID).toString(),employeeId);

        int totalAssignedReviewers = providerList.stream()
                .mapToInt(fp -> fp.getAssignedReviewers() != null ? fp.getAssignedReviewers().size() : 0)
                .sum();

        int feedbackGivenTillNow = Math.toIntExact(providerList.stream()
                .flatMap(fp -> fp.getAssignedReviewers().stream())
                .filter(r -> r.getStatus() == ProviderStatus.COMPLETED)
                .count());

        return new FeedbackStatusResponse(totalAssignedReviewers, feedbackGivenTillNow);
    }

    @Override
    public OverallRating createOrUpdateOverallRating(String employeeId, Double rating, String comments) {
        try {
            String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
            OverallRating existing = (OverallRating) overallRatingRepository
                    .findByEmployeeIdAndOrganizationId(employeeId, orgId)
                    .orElse(new OverallRating());

            existing.setEmployeeId(employeeId);
            existing.setRating(rating);
            existing.setComments(comments);
            existing.setGivenBy(UserContext.getLoggedInUserName());
            existing.setOrganizationId(orgId);
            existing.setPublishedAt(Instant.now());

            log.info(Constants.FINAL_RATING_COMPUTED_AND_SAVED, employeeId, orgId);
            return overallRatingRepository.save(existing);

        } catch (Exception e) {
            log.error(Constants.ERROR_SAVING_FEEDBACK_RESPONSE, e.getMessage());
            throw new RuntimeException(Constants.ERROR_SAVING_FEEDBACK_RESPONSE_SIMPLE, e);
        }
    }

    @Override
    public OverallRating getOverallRatingByEmployeeId(String employeeId) {
        try {
            String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
            return (OverallRating) overallRatingRepository
                    .findByEmployeeIdAndOrganizationId(employeeId, orgId)
                    .orElse(null);
        } catch (Exception e) {
            log.error(Constants.NO_RATINGS_FOUND_FOR_EMPLOYEE, employeeId);
            throw new RuntimeException(Constants.NO_RATINGS_FOUND_FOR_EMPLOYEE + employeeId, e);
        }
    }

    @Override
    public void deleteOverallRatingByEmployeeId(String employeeId) {
        try {
            String orgId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
            overallRatingRepository.deleteByEmployeeIdAndOrganizationId(employeeId, orgId);
        } catch (Exception e) {
            log.error(Constants.FINAL_RATING_NOT_FOUND, employeeId);
            throw new RuntimeException(Constants.FINAL_RATING_NOT_FOUND + employeeId, e);
        }
    }

    @Override
    public List<EmployeeCycleInfo> getCycleIdsByEmployeeId(String employeeId) {
        try {
            List<FeedbackReceivers> receivers = feedbackReceiverRepository.findByEmployeeIdAndOrganizationId(employeeId,UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
            if (receivers.isEmpty()) {
                log.warn(Constants.NO_RECEIVER_FOUND, employeeId);
                return new ArrayList<>();
            }

            return receivers.stream()
                    .map(receiver -> {
                        EvaluationCycle cycle = evaluationCycleRepository.getCycleByOrganizationIdAndId(UserContext.getLoggedInUserOrganization().get(Constants.ID).toString(),receiver.getCycleId());
                        String cycleName = (cycle != null) ? cycle.getName() : null;
                        return new EmployeeCycleInfo(employeeId, receiver.getCycleId(), cycleName);
                    })
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND, employeeId);
            throw new RuntimeException(Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND + employeeId, e);
        }
    }
}
