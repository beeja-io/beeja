package com.beeja.api.projectmanagement.model;


import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "contracts")
public class Contract {

    @Id
    private String id;

    @NotBlank(message = "Contract name cannot be empty")
    private String contractName;

    @NotBlank(message = "Contract type is required")
    private String contractType;

    private String contractId;
    private String organizationId;

    @NotNull(message = "Start date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate endDate;

    private String billingType;
    private String billingCurrency;
    private Double budget;
    private String description;

    @DBRef(lazy = true)
    @NotNull(message = "Project is required")
    private Project project;

    @DBRef(lazy = true)
    @NotNull(message = "Client is required")
    private Client client;

    @DBRef(lazy = true)
    private List<Resource> projectManagers;

    @DBRef(lazy = true)
    private List<Resource> resources;

    private String attachmentId;



}


