package com.beeja.api.projectmanagement.responses;

import lombok.Data;

import java.util.List;

@Data
public class ResourceView {
    private String employeeId;
    private String name;
    private List<String> contractName;
    private Double allocationPercentage;
}