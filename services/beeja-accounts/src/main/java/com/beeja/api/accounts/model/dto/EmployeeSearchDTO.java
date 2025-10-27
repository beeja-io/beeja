package com.beeja.api.accounts.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSearchDTO {
    private String employeeId;
    private String fullName;
    private String email;
}
