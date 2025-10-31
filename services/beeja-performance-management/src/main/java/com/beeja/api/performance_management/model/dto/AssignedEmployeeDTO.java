package com.beeja.api.performance_management.model.dto;

import com.beeja.api.performance_management.enums.ProviderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedEmployeeDTO {
    private String employeeId;
    private String employeeName;
    private String department;
    private String cycleId;
    private String role;
}
