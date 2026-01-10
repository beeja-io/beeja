package com.beeja.api.employeemanagement.model.clients.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDepartmentDTO {
    private String employeeId;
    private String designation;
    private String department;
}
