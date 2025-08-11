package com.beeja.api.employeemanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDefaultValues {
  private String employmentType;
  private String designation;
  private String department;
}
