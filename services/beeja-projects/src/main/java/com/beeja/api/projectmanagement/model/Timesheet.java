package com.beeja.api.projectmanagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Timesheet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timesheet {
    @Id
    private String id;

    private String employeeId;
    private String organizationId;

    private String clientId;
    private String projectId;
    private String contractId;

    private Date startDate;
    private int timeInMinutes;
    private String description;

    private Date createdAt;
    private String createdBy;

    private Date modifiedAt;
    private String modifiedBy;
}
