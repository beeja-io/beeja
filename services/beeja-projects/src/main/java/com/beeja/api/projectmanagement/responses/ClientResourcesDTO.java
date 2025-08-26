package com.beeja.api.projectmanagement.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientResourcesDTO {
    private String employeeId;
    private String employeeName;
    private long numberOfContracts;
    private double totalAllocation;
}

