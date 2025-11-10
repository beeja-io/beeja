package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.model.*;
import com.beeja.api.performance_management.repository.*;
import com.beeja.api.performance_management.service.RatingService;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Secure multi-tenant implementation for managing employee ratings.
 * All repository calls are scoped by organizationId to prevent data leaks.
 */
@Service
public class RatingServiceImpl implements RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingServiceImpl.class);

    private final FeedbackResponseRepository responseRepo;
    private final SelfEvaluationRepository selfRepo;
    private final FinalRatingRepository ratingRepo;

    public RatingServiceImpl(FeedbackResponseRepository responseRepo,
                             SelfEvaluationRepository selfRepo,
                             FinalRatingRepository ratingRepo) {
        this.responseRepo = responseRepo;
        this.selfRepo = selfRepo;
        this.ratingRepo = ratingRepo;
    }

    /**
     * Helper method to safely retrieve organizationId from user context.
     */
    private String getOrgId() {
        Object org = UserContext.getLoggedInUserOrganization().get("id");
        if (org == null) {
            throw new IllegalStateException("Organization ID missing in user context");
        }
        return org.toString();
    }

    /**
     * Computes a final rating for an employee in a given evaluation cycle.
     */
    @Override
    @Transactional
    public FinalRating computeRating(String employeeId, String cycleId, String computedBy) {
        String orgId = getOrgId();
        log.info(Constants.COMPUTING_RATING, employeeId, cycleId, orgId);

        List<FeedbackResponse> responses = responseRepo.findByEmployeeIdAndOrganizationId(employeeId, orgId);
        if (responses.isEmpty()) {
            log.warn(Constants.NO_RESPONSES_FOUND_FOR_EMPLOYEE, employeeId);
        }

        List<SelfEvaluation> selfEvals = selfRepo.findByEmployeeIdAndOrganizationId(employeeId, orgId);
        if (selfEvals.isEmpty()) {
            log.warn(Constants.NO_SELF_EVALUATIONS_FOUND_FOR_EMPLOYEE, employeeId);
        }

        FinalRating finalRating = new FinalRating();
        finalRating.setEmployeeId(employeeId);
        finalRating.setCycleId(cycleId);
        finalRating.setOrganizationId(orgId);
        finalRating.setGivenBy(computedBy == null ? "SYSTEM" : computedBy);
        finalRating.setPublished(false);
        finalRating.setPublishedAt(null);

        FinalRating savedRating = ratingRepo.save(finalRating);
        log.info(Constants.FINAL_RATING_COMPUTED_AND_SAVED, employeeId, cycleId);

        return savedRating;
    }

    /**
     * Retrieves all ratings for an employee in a specific cycle.
     */
    @Override
    public List<FinalRating> getRatings(String employeeId, String cycleId) {
        String orgId = getOrgId();
        log.info(Constants.FETCHING_RATINGS, employeeId, cycleId, orgId);

        List<FinalRating> ratings = ratingRepo.findByEmployeeIdAndCycleIdAndOrganizationId(employeeId, cycleId, orgId);
        if (ratings.isEmpty()) {
            log.warn(Constants.NO_RATINGS_FOUND_FOR_EMPLOYEE, employeeId, cycleId);
        }

        return ratings;
    }

    /**
     * Publishes a final rating by marking it as published.
     * Ensures that the rating belongs to the same organization as the logged-in user.
     */
    @Override
    public FinalRating publishRating(String id) {
        String orgId = getOrgId();
        log.info(Constants.PUBLISHING_FINAL_RATING, id);

        FinalRating finalRating = ratingRepo.findByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> {
                    log.error(Constants.FINAL_RATING_NOT_FOUND, id);
                    return new IllegalArgumentException("Final rating not found for this organization: " + id);
                });

        finalRating.setPublished(true);
        finalRating.setPublishedAt(Instant.now());
        FinalRating savedRating = ratingRepo.save(finalRating);

        log.info(Constants.FINAL_RATING_PUBLISHED_SUCCESSFULLY, id);
        return savedRating;
    }
}
