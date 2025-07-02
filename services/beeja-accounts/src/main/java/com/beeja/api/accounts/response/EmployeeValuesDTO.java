package com.beeja.api.accounts.response;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeValuesDTO {
  private Set<String> employmentTypes;
  private Set<String> designations;
  private Set<String> departments;
}
