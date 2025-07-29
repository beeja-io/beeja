package com.beeja.api.projectmanagement.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectManagerView {
    private String employeeId;
    private String name;
    private String contractName;
}