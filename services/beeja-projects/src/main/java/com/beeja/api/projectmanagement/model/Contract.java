package com.beeja.api.projectmanagement.model;

import com.beeja.api.projectmanagement.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    @Id
    private String id;

    private String contractId;
    private String projectId;
    private String clientId;
    private String organizationId;

    private String contractTitle;
    private String description;

    private Double contractValue;
    private Date startDate;
    private Date endDate;
    private ProjectStatus status;

    private String signedBy;
    private Boolean isActive = true;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
