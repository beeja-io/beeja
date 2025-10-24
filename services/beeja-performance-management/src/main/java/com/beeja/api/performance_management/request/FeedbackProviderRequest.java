package com.beeja.api.performance_management.request;


import com.beeja.api.performance_management.model.dto.AssignedReviewer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackProviderRequest {
    private List<String> employeeIds;
    private String cycleId;
    private String questionnaireId;
    private List<AssignedReviewer> assignedReviewers;
}
