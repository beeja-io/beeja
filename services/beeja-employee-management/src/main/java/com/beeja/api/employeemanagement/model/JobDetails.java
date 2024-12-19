package com.beeja.api.employeemanagement.model;

import lombok.Data;

import java.util.Date;

@Data
public class JobDetails {
  private String designation;
  private String employementType;
  private String department;
  private Date joiningDate;
  private Date resignationDate;
}
