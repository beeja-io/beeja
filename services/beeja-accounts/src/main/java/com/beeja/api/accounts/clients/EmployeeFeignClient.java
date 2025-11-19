package com.beeja.api.accounts.clients;

import com.beeja.api.accounts.model.dto.EmployeeDepartmentDTO;
import com.beeja.api.accounts.response.EmployeeValuesDTO;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "employee-service", url = "${client-urls.employeeService}")
public interface EmployeeFeignClient {

  @PostMapping("/v1/users")
  void createEmployee(@RequestBody Map<String, Object> employee);

  @DeleteMapping("/v1/users/organizations/{organizationId}")
  ResponseEntity<String> deleteAllEmployeesByOrganizationId(@PathVariable String organizationId);

  @GetMapping("/v1/users/employee-values")
  EmployeeValuesDTO getEmployeeValues(@RequestHeader String authorization);

    @GetMapping("/v1/users/departments")
    List<EmployeeDepartmentDTO> getDesignationsByEmployeeIds(@RequestParam("employeeIds") List<String> employeeIds);
}
