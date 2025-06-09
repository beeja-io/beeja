package com.beeja.api.employeemanagement.requests;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOrgRequest {
  private List<String> employeeIds;
}
