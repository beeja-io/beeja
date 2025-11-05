package com.beeja.api.performance_management.model.dto;

import com.beeja.api.performance_management.enums.ProviderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewerDetailsDTO {
    private String reviewerId;
    private String reviewerName;
    private String role;
    private ProviderStatus providerStatus;
}
