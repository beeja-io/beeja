package com.beeja.api.employeemanagement.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSummaryDTO {
    private String employeeId;
    private String organizationId;
    private JobDetailsCompressed jobDetails;
    private String profilePictureId;
}
