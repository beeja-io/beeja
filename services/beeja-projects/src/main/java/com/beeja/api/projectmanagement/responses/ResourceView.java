package com.beeja.api.projectmanagement.responses;

import lombok.Data;

@Data
public class ResourceView {
    private String employeeId;
    private String name;
    private String contractName;
    private Double allocationPercentage;
}