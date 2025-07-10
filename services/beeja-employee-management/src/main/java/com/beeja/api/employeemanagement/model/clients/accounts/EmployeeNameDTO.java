package com.beeja.api.employeemanagement.model.clients.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeNameDTO {
    private String employeeId;
    private String fullName;
}
