package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.ReviewCycle;
import com.beeja.api.performance_management.requests.AssignManagersRequest;
import com.beeja.api.performance_management.requests.ReviewCycleRequest;
import com.beeja.api.performance_management.response.ReviewCycleResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReviewCycleService {

    String createReviewCycle(ReviewCycleRequest request);

    void assignManagers(String reviewCycleId, AssignManagersRequest managers);

    ReviewCycleResponse getReviewCycleById(String reviewCycleId);

    List<ReviewCycle> getAllRCs();

    ReviewCycle updateReviewCycle(String reviewCycleId, ReviewCycleRequest request);
}
