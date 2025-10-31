package com.beeja.api.accounts.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSearchResponse {
    private String employeeId;
    private String fullName;
    private String department;
    private String email;
}
