package com.beeja.api.performance_management.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeCycleInfo {
    private String employeeId;
    private String cycleId;
    private String cycleName;
}
