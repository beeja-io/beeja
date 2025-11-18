package com.beeja.api.performance_management.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePerformanceDTO {
    private String employeeId;
    private String organizationId;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isActive;
    private JobDetailsCompressed jobDetails;
    private String profilePictureId;
    private Double overallRating;
    private Integer numberOfReviewersAssigned;
    private Integer numberOfReviewerResponses;
}
