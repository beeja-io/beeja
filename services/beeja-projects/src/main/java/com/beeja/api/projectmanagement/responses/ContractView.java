package com.beeja.api.projectmanagement.responses;

import com.beeja.api.projectmanagement.enums.ProjectStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ContractView {
    private String contractId;
    private String name;
    private ProjectStatus status;
    private Date startDate;
    private List<ProjectManagerView> projectManagers;
}
