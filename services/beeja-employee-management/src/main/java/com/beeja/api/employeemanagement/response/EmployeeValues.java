package com.beeja.api.employeemanagement.response;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeValues {
  private Set<String> employmentTypes;
  private Set<String> designations;
  private Set<String> departments;
}
