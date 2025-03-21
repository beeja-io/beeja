package com.beeja.api.accounts.requests;

import lombok.Data;

@Data
public class AddEmployeeRequest {
  private String firstName;
  private String lastName;
  private String email;
  private String employmentType;
  private String department;
}
