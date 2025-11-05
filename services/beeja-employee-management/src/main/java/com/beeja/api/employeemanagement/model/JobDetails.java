package com.beeja.api.employeemanagement.model;

import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class JobDetails {
  @Id
  private String id; // unique & never changes
  private String designation;
  private String employementType;
  private String department;
  private Date joiningDate;
  private Date resignationDate;
  private String description;
  private String updatedBy;
  private Date updatedAt;
  private Date startDate;
  private Date endDate;
}
