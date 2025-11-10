package com.beeja.api.performance_management.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedbackFormSummaryResponse {
    private String cycleId;
    private String cycleName;
    private String status;
}
