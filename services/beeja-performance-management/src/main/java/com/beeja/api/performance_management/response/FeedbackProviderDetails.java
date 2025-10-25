package com.beeja.api.performance_management.response;


import com.beeja.api.performance_management.model.dto.ReviewerDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackProviderDetails {
    private String employeeId;
    private String employeeName;
    private List<ReviewerDetailsDTO> assignedReviewers;
}
