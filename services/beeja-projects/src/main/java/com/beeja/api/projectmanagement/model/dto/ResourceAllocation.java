package com.beeja.api.projectmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceAllocation {
    private String employeeId;
    private Double allocationPercentage;
}
