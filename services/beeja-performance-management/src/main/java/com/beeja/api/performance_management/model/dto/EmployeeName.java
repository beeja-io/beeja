package com.beeja.api.performance_management.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeName {
    private String employeeId;
    private String firstName;
    private String lastName;
}
