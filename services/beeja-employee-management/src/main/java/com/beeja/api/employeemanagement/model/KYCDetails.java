package com.beeja.api.employeemanagement.model;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class KYCDetails {

  @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}")
  private String panNumber;

  @Pattern(regexp = "[0-9]{12}")
  private String aadhaarNumber;

  @Pattern(regexp = "[A-Z][0-9]{7}")
  private String passportNumber;
}
