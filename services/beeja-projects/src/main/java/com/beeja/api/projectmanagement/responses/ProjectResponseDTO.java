package com.beeja.api.projectmanagement.responses;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.model.Project;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class ProjectResponseDTO {
    private String projectId;
    private String name;
    private List<String> projectManagerIds;
    private List<String> projectManagerNames;
    private ProjectStatus projectStatus;
    private String clientName;
    private String clientId;
    private Date startDate;
}