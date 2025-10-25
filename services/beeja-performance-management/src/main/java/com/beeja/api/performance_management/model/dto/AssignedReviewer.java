package com.beeja.api.performance_management.model.dto;

import com.beeja.api.performance_management.enums.ProviderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignedReviewer {
    private String reviewerId;
    private String role;
    private ProviderStatus status;
}
