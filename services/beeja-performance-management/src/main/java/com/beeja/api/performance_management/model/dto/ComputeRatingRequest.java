package com.beeja.api.performance_management.model.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComputeRatingRequest {
    @NotBlank
    private String employeeId;

    private String organizationId;

    @NotBlank
    private String cycleId;

    private String computedBy;

    private String comments;
}