package com.beeja.api.projectmanagement.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailsResponse {
    private String objectId;
    private String employeeId;
    private String employeename;
    private String email;
    private String profilePic;

}
