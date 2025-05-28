package com.beeja.api.projectmanagement.model.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetRequestDto {
    private String employeeId;
    private String organizationId;

    private String clientId;
    private String projectId;
    private String contractId;

    private Date startDate;
    private int timeInMinutes;
    private String description;
}
