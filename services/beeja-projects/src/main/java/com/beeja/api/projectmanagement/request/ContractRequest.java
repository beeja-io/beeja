package com.beeja.api.projectmanagement.request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.beeja.api.projectmanagement.model.Resource;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ContractRequest {
    @NotBlank(message = "Contract name cannot be empty")
    private String contractName;

    @NotBlank(message = "Contract type is required")
    private String contractType;

    @NotNull(message = "Start date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private LocalDate endDate;
    private String billingType;
    private String billingCurrency;
    private Double budget;
    private String description;

    @NotBlank(message = "Project ID is required")
    private String project;

    @NotBlank(message = "Client ID is required")
    private String client;

    @JsonProperty("projectManagers")
    private List<Resource> projectManagers = new ArrayList<>();

    @JsonProperty("resources")
    private List<Resource> resources = new ArrayList<>();
    private MultipartFile attachment ;


}
