package com.beeja.api.performance_management.utils;

import com.beeja.api.performance_management.model.ReviewCycle;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReviewCycleValidator {

    public void validateReviewCycle(ReviewCycle reviewCycle) {
        if (reviewCycle.getName() == null || reviewCycle.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Review cycle name is required");
        }
        if (reviewCycle.getStartDate() == null || reviewCycle.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (reviewCycle.getStartDate().isAfter(reviewCycle.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        if (reviewCycle.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("End date cannot be in the past");
        }
    }
}
