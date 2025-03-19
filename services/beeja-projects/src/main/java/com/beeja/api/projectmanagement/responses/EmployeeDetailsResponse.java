package com.beeja.api.projectmanagement.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailsResponse {
    private String id;
    private String firstName;
    private String employeeId;
}
