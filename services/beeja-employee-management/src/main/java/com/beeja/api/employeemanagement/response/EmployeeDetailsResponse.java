package com.beeja.api.employeemanagement.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDetailsResponse {
    private String objectId;
    private String employeeId;
    private String employeename;
    private String email;
    private String profilePic;
}
