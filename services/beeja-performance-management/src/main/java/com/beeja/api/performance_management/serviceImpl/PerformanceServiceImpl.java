package com.beeja.api.performance_management.serviceImpl;


import com.beeja.api.performance_management.model.ReviewCycle;
import com.beeja.api.performance_management.repository.ReviewCycleRepository;
import com.beeja.api.performance_management.service.PerformanceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerformanceServiceImpl extends PerformanceService {

    public PerformanceServiceImpl(ReviewCycleRepository reviewCycleRepository) {
        this.reviewCycleRepository = reviewCycleRepository;
    }


    // Review Cycle Operations
    @Override
        public List<ReviewCycle> getAllReviewCycles() {
        return reviewCycleRepository.findAll();
    }

    @Override
    public Optional<ReviewCycle> getReviewCycleById(String reviewCycleId) {
        return reviewCycleRepository.findByCycleId(reviewCycleId);
    }


    @Override
    public ReviewCycle createReviewCycle(ReviewCycle reviewCycle) {
      //  reviewCycle.setCreatedAt(LocalDateTime.now());
     //   reviewCycle.setUpdatedAt(LocalDateTime.now());
        return reviewCycleRepository.save(reviewCycle);
    }

    @Override
    public ReviewCycle updateReviewCycle(String reviewCycleId, ReviewCycle reviewCycle) {
        return reviewCycleRepository.findByCycleId(reviewCycleId)
                .map(existing -> {
                    existing.setName(reviewCycle.getName());
                    existing.setDescription(reviewCycle.getDescription());
                    existing.setStartDate(reviewCycle.getStartDate());
                    existing.setEndDate(reviewCycle.getEndDate());
                    existing.setStatus(reviewCycle.getStatus());
                    existing.setManagerIds(reviewCycle.getManagerIds());
                    existing.setEmployeeIds(reviewCycle.getEmployeeIds());
                   // existing.setUpdatedAt(LocalDateTime.now());
                    return reviewCycleRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("ReviewCycle not found: " + reviewCycleId));
    }

    @Override
    public List<ReviewCycle> getReviewCyclesByManager(String managerId) {
        return reviewCycleRepository.findByManagerIdsContaining(managerId);
    }
}