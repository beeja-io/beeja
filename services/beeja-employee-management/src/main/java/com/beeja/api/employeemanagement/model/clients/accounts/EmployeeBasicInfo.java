package com.beeja.api.employeemanagement.model.clients.accounts;


import com.beeja.api.employeemanagement.model.JobDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBasicInfo {
    private String employeeId;
    private String fullName;
    private JobDetails jobDetails;
}
