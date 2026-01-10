package com.beeja.api.performance_management.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackStatusResponse {
    private int totalAssignedReviewers;
    private int feedbackGivenTillNow;
}
