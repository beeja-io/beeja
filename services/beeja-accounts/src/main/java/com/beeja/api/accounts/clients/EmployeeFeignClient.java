package com.beeja.api.accounts.clients;

import com.beeja.api.accounts.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "employee-service", url = "${client-urls.employeeService}")
public interface EmployeeFeignClient {

  @PostMapping("/v1/users")
  void createEmployee(@RequestBody User user);

  @DeleteMapping("/v1/users/organizations/{organizationId}")
  ResponseEntity<String> deleteAllEmployeesByOrganizationId(@PathVariable String organizationId);
}
