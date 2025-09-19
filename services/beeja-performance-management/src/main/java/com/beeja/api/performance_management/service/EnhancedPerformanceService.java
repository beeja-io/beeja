package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.ReviewCycle;
import com.beeja.api.performance_management.responses.ReviewCycleDto;
import com.beeja.api.performance_management.utils.ReviewCycleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class EnhancedPerformanceService extends PerformanceService {

    @Autowired
    private ReviewCycleValidator validator;

    @Override
    public ReviewCycle createReviewCycle(ReviewCycle reviewCycle) {
        validator.validateReviewCycle(reviewCycle);
        reviewCycle.setCycleId("RC-" + System.currentTimeMillis());
        reviewCycle.setStatus("DRAFT");
        return super.createReviewCycle(reviewCycle);
    }

    private String getCurrentUserId() {
        return "current-user-id";
    }
}
