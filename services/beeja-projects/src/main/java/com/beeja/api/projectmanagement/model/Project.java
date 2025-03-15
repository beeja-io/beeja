package com.beeja.api.projectmanagement.model;


import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.responses.EmployeeDetailsResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projectDB")
@Data
public class Project {
    @Id
    private String id;

    @DBRef
    @NotNull(message = "Client information is required.")
    private Client client;

    @NotBlank(message = "Project name cannot be empty")
    private String projectName;

    private String projectId;
    private String organizationId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Date startDate;


    @JsonProperty("status")
    private ProjectStatus status=ProjectStatus.IN_PROGRESS;

    private String description;

    @DBRef
    private List<Resource> resources;

    @DBRef
    private List<Resource> projectManagers;


}
