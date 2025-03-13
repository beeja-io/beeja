package com.beeja.api.accounts.clients;

import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.response.EmployeeValuesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "employee-service", url = "${client-urls.employeeService}")
public interface EmployeeFeignClient {

  @PostMapping("/v1/users")
  void createEmployee(@RequestBody User user);

  @DeleteMapping("/v1/users/organizations/{organizationId}")
  ResponseEntity<String> deleteAllEmployeesByOrganizationId(@PathVariable String organizationId);

  @GetMapping("/v1/users/employee-values")
  EmployeeValuesDTO getEmployeeValues(@RequestHeader String Authorization);
}
