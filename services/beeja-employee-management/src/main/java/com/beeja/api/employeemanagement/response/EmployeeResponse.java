package com.beeja.api.employeemanagement.response;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
  List<Map<String, Object>> employeeList;
  Long totalSize;
}
