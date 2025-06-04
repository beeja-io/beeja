package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.ReviewCycleStatus;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.ReviewCycle;
import com.beeja.api.performance_management.repository.ReviewCycleRepository;
import com.beeja.api.performance_management.repository.ReviewFormRepository;
import com.beeja.api.performance_management.requests.AssignManagersRequest;
import com.beeja.api.performance_management.requests.ReviewCycleRequest;
import com.beeja.api.performance_management.response.ReviewCycleResponse;
import com.beeja.api.performance_management.service.ReviewCycleService;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ReviewCycleServiceImpl implements ReviewCycleService {

    @Autowired
    ReviewCycleRepository reviewCycleRepository;

    @Autowired
    ReviewFormRepository reviewFormRepository;

    @Override
    public String createReviewCycle(ReviewCycleRequest request) {

        ReviewCycle reviewCycle = new ReviewCycle();

        reviewCycle.setName(request.getName());
        reviewCycle.setReviewType(request.getReviewType());
        reviewCycle.setStartDate(request.getStartDate());
        reviewCycle.setEndDate(request.getEndDate());
        reviewCycle.setStatus(ReviewCycleStatus.IN_PROGRESS);
        reviewCycle.setOrganizationId(UserContext.getLoggedInUserOrganization().get("id").toString());
        reviewCycle.setCreatedBy(UserContext.getLoggedInEmployeeId());
        reviewCycle.setCreatedAt(Date.from(Instant.now()));
        reviewCycleRepository.save(reviewCycle);
        return reviewCycle.getId();
    }

    @Override
    public void assignManagers(String reviewCycleId, AssignManagersRequest managers) {

        ReviewCycle reviewCycle = reviewCycleRepository.findById(reviewCycleId)
                .orElseThrow(() -> new ResourceNotFoundException("ReviewCycle not found with id: " + reviewCycleId));

        reviewCycle.setManagerIds(managers.getManagers());
        reviewCycleRepository.save(reviewCycle);

    }

    @Override
    public ReviewCycleResponse getReviewCycleById(String reviewCycleId) {
        ReviewCycle reviewCycle = reviewCycleRepository.findById(reviewCycleId)
                .orElseThrow(() -> new ResourceNotFoundException("ReviewCycle not found with id: " + reviewCycleId));

        ReviewCycleResponse response = new ReviewCycleResponse();

        response.setName(reviewCycle.getName());
        response.setReviewType(reviewCycle.getReviewType());
        response.setStartDate(reviewCycle.getStartDate());
        response.setEndDate(reviewCycle.getEndDate());
        response.setStatus(reviewCycle.getStatus());

        return response;
    }

    @Override
    public List<ReviewCycle> getAllRCs() {
        return reviewCycleRepository.findByOrganizationId(
                UserContext.getLoggedInUserOrganization().get("id").toString());
    }

    @Override
    public ReviewCycle updateReviewCycle(String reviewCycleId, ReviewCycleRequest request) {

        ReviewCycle reviewCycle = reviewCycleRepository.findById(reviewCycleId)
                .orElseThrow(() -> new ResourceNotFoundException("ReviewCycle not found with id: " + reviewCycleId));

        if (request.getName() != null) {
            reviewCycle.setName(request.getName());
        }

        if (request.getReviewType() != null) {
            reviewCycle.setReviewType(request.getReviewType());
        }

        if (request.getStartDate() != null) {
            reviewCycle.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            reviewCycle.setEndDate(request.getEndDate());
        }

        reviewCycle.setUpdatedAt(Date.from(Instant.now()));
        reviewCycle.setUpdatedBy(UserContext.getLoggedInEmployeeId());

       return reviewCycleRepository.save(reviewCycle);
    }


}
