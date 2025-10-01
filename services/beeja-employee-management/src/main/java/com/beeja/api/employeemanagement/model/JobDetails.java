package com.beeja.api.employeemanagement.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class JobDetails {
  private String designation;
  private String employementType;
  private String department;
  private Date joiningDate;
  private Date resignationDate;
  private String updatedBy; // who updated
  private Date updatedAt;   // when update
  private List<JobDetails> previousJobDetailsList; // added new
}
