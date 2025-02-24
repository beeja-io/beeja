package com.beeja.api.employeemanagement.controller;

import com.beeja.api.employeemanagement.annotations.HasPermission;
import com.beeja.api.employeemanagement.constants.PermissionConstants;
import com.beeja.api.employeemanagement.model.Employee;
import com.beeja.api.employeemanagement.requests.EmployeeUpdateRequest;
import com.beeja.api.employeemanagement.requests.UpdateKYCRequest;
import com.beeja.api.employeemanagement.response.EmployeeDetailsResponse;
import com.beeja.api.employeemanagement.response.EmployeeResponse;
import com.beeja.api.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users")
public class EmployeeController {

  @Autowired private EmployeeService employeeService;

  @GetMapping
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<EmployeeResponse> getAllEmployees(
      @RequestParam(name = "department", required = false) String department,
      @RequestParam(name = "designation", required = false) String designation,
      @RequestParam(name = "employmentType", required = false) String employmentType,
      @RequestParam(name = "status", defaultValue = "active") String status,
      @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize)
      throws Exception {
    EmployeeResponse combinedDataList =
        employeeService.getCombinedLimitedDataOfEmployees(
            department, designation, employmentType, pageNumber, pageSize, status);
    return ResponseEntity.ok(combinedDataList);
  }

  @GetMapping("/{employeeID}")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<Map<String, Object>> getEmployeeByEmployeeId(
      @PathVariable String employeeID) throws Exception {
    return new ResponseEntity<>(employeeService.getEmployeeByEmployeeId(employeeID), HttpStatus.OK);
  }

  @PostMapping
  @HasPermission(PermissionConstants.CREATE_EMPLOYEE)
  public ResponseEntity<?> createEmployee(@RequestBody Map<String, Object> user) throws Exception {
    return new ResponseEntity<>(employeeService.createEmployee(user), HttpStatus.CREATED);
  }

  @PutMapping("/{employeeId}")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<Object> updateEmployee(
      @PathVariable String employeeId, @RequestBody EmployeeUpdateRequest updatedEmployee)
      throws Exception {
    Employee employee = employeeService.updateEmployee(employeeId, updatedEmployee);
    return new ResponseEntity<>(employee, HttpStatus.OK);
  }

  @PatchMapping("/{employeeId}/kyc")
  public ResponseEntity<Employee> updateKycDetails(
      @PathVariable String employeeId,
      @RequestBody UpdateKYCRequest updateKYCRequest,
      @Valid BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      List<String> errorMessages =
          bindingResult.getAllErrors().stream()
              .map(ObjectError::getDefaultMessage)
              .collect(Collectors.toList());
      throw new Exception(errorMessages.toString());
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(employeeService.updateKYCRequest(employeeId, updateKYCRequest));
  }

  @GetMapping("/info")
  public ResponseEntity<List<EmployeeDetailsResponse>> getEmployeeDetails() {
    List<EmployeeDetailsResponse> employees = employeeService.getEmployeeDetails();
    return ResponseEntity.ok(employees);
  }
}
