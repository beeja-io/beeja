package com.beeja.api.projectmanagement.request;

import com.beeja.api.projectmanagement.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

  @NotBlank(message = "Project name is required")
  private String name;

  private String description;

  @NotNull(message = "Project status is required")
  private ProjectStatus status;

  @NotNull(message = "Start date is required")
  private Date startDate;

  private Date endDate;

  @NotBlank(message = "Client ID is required")
  private String clientId;

  List<String> projectManagers;
  List<String> projectResources;
}
