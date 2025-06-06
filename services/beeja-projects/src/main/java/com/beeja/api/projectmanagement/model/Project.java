package com.beeja.api.projectmanagement.model;

import com.beeja.api.projectmanagement.enums.ProjectStatus;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

  @Id private String id;
  private String projectId;
  private String name;
  private String description;
  private ProjectStatus status;
  private Date startDate;
  private Date endDate;
  private String clientId;
  private String organizationId;

  @CreatedDate private Date createdAt;

  @LastModifiedDate private Date updatedAt;
}
