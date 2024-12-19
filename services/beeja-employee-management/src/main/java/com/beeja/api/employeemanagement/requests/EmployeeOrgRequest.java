package com.beeja.api.employeemanagement.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOrgRequest {
  private List<String> employeeIds;
}
