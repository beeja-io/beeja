package com.beeja.api.performance_management.client;

import com.beeja.api.performance_management.model.dto.EmployeeDepartmentDTO;
import com.beeja.api.performance_management.model.dto.EmployeeSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "employee-service", url = "${client-urls.employeeService}")
public interface EmployeeFeignClient {
    
    @GetMapping("/v1/users/{employeeId}")
    ResponseEntity<Map<String, Object>> getEmployeeByEmployeeId(@PathVariable("employeeId") String employeeId);

    @GetMapping("/v1/users/organization")
    public ResponseEntity<List<EmployeeSummaryDTO>> getEmployeesByLoggedInUserOrganization();

    @GetMapping("/v1/users/departments")
    List<EmployeeDepartmentDTO> getDepartmentsByEmployeeIds(@RequestParam("employeeIds") List<String> employeeIds);
}
