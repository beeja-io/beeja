package com.beeja.api.employeemanagement.model;

import lombok.Data;

@Data
public class NomineeDetails {
  private String name;
  private String email;
  private String phone;
  private String relationType;
  private String aadharNumber;
}
