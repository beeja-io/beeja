package com.beeja.api.performance_management.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewerEmployeeDTO {
    private String employeeId;
    private String employeeName;
    private String department;
    private String role;
    private List<FeedbackCycleDTO> feedbackCycles;
}
