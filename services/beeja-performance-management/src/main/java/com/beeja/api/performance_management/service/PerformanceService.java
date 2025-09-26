package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.ReviewCycle;
import com.beeja.api.performance_management.repository.ReviewCycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerformanceService {

    @Autowired
    protected ReviewCycleRepository reviewCycleRepository;

    // Review Cycle
    public List<ReviewCycle> getAllReviewCycles() {
        return reviewCycleRepository.findAll();
    }

    public Optional<ReviewCycle> getReviewCycleById(String reviewCycleId) {
        return reviewCycleRepository.findByCycleId(reviewCycleId);
    }

    public ReviewCycle createReviewCycle(ReviewCycle reviewCycle) {
        return reviewCycleRepository.save(reviewCycle);
    }

    public ReviewCycle updateReviewCycle(String reviewCycleId, ReviewCycle reviewCycle) {
        reviewCycle.setCycleId(reviewCycleId);
        return reviewCycleRepository.save(reviewCycle);
    }

    public List<ReviewCycle> getReviewCyclesByManager(String managerId) {
        return reviewCycleRepository.findByManagerIdsContaining(managerId);
    }

    protected String getCurrentManagerId() {
        return "current-manager-id";
    }
}
