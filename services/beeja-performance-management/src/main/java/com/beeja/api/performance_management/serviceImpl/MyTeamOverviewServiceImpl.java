package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.FeedbackReceivers;
import com.beeja.api.performance_management.model.OverallRating;
import com.beeja.api.performance_management.model.dto.EmployeeCycleInfo;
import com.beeja.api.performance_management.repository.FeedbackReceiverRepository;
import com.beeja.api.performance_management.repository.OverallRatingRepository;
import com.beeja.api.performance_management.service.EvaluationCycleService;
import com.beeja.api.performance_management.service.MyTeamOverviewService;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MyTeamOverviewServiceImpl implements MyTeamOverviewService {

    @Autowired
    private OverallRatingRepository overallRatingRepository;

    @Autowired
    private EvaluationCycleService cycleService;

    @Autowired
    private FeedbackReceiverRepository feedbackReceiverRepository;

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
                        EvaluationCycle cycle = cycleService.getCycleById(receiver.getCycleId());
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
