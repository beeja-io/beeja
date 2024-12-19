package com.beeja.api.employeemanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
  List<Map<String, Object>> EmployeeList;
  Long totalSize;
}
