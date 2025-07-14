package com.beeja.api.accounts.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeIdNameDTO {
    private String employeeId;
    private String firstName;
    private String lastName;
}