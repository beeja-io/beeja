package com.beeja.api.employeemanagement.model;

import java.util.Date;
import lombok.Data;

@Data
public class JobDetails {
  private String designation;
  private String employementType;
  private String department;
  private Date joiningDate;
  private Date resignationDate;
}
