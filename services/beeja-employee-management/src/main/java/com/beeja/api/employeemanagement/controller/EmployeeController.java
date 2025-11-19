package com.beeja.api.employeemanagement.controller;

import com.beeja.api.employeemanagement.annotations.HasPermission;
import com.beeja.api.employeemanagement.constants.PermissionConstants;
import com.beeja.api.employeemanagement.model.DTO.EmployeeSummaryDTO;
import com.beeja.api.employeemanagement.model.Employee;
import com.beeja.api.employeemanagement.model.JobDetails;
import com.beeja.api.employeemanagement.model.clients.accounts.EmployeeBasicInfo;
import com.beeja.api.employeemanagement.model.clients.accounts.EmployeeDepartmentDTO;
import com.beeja.api.employeemanagement.repository.EmployeeRepository;
import com.beeja.api.employeemanagement.requests.EmployeeUpdateRequest;
import com.beeja.api.employeemanagement.requests.UpdateKYCRequest;
import com.beeja.api.employeemanagement.response.EmployeeResponse;
import com.beeja.api.employeemanagement.response.EmployeeValues;
import com.beeja.api.employeemanagement.service.EmployeeService;
import com.beeja.api.employeemanagement.utils.UserContext;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class EmployeeController {

  @Autowired private EmployeeService employeeService;

  @Autowired private EmployeeRepository employeeRepository;

  @GetMapping("/organization")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<List<EmployeeSummaryDTO>> getEmployeesByLoggedInUserOrganization() {

    List<EmployeeSummaryDTO> employees = employeeService.getEmployeesByOrganizationId(UserContext.getLoggedInUserOrganization().getId());
    return ResponseEntity.ok(employees);
  }

  @GetMapping
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<EmployeeResponse> getAllEmployees(
      @RequestParam(name = "department", required = false) String department,
      @RequestParam(name = "designation", required = false) String designation,
      @RequestParam(name = "employmentType", required = false) String employmentType,
      @RequestParam(name = "status", required = false) String status,
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

  @GetMapping("/employee-values")
  ResponseEntity<EmployeeValues> getEmployeeValues() throws Exception {
    return ResponseEntity.ok(employeeService.getEmployeeValues());
  }

  @GetMapping("/basic-info")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  ResponseEntity<List<EmployeeBasicInfo>> getAllEmployeeBasicInfo(
          @RequestParam(name = "designations", required = false) List<String> designations){
    return ResponseEntity.ok(employeeService.getAllEmpInfo(designations));
  }

  @PostMapping("/{employeeId}/history")
  public ResponseEntity<Employee> addJobStage(
          @PathVariable String employeeId,
          @RequestBody JobDetails newJob) throws Exception {
    return ResponseEntity.ok(employeeService.addJobHistory(employeeId, newJob));
  }

  @PutMapping("/{employeeId}/history/{jobId}")
  public ResponseEntity<Employee> updateJobStage(
          @PathVariable String employeeId,
          @PathVariable String jobId,
          @RequestBody JobDetails updatedJob) throws Exception {
    return ResponseEntity.ok(employeeService.updateJobHistory(employeeId, jobId, updatedJob));
  }

  @DeleteMapping("/{employeeId}/history/{jobId}")
  public ResponseEntity<Employee> deleteJobStage(
          @PathVariable String employeeId,
          @PathVariable String jobId) throws Exception {
    return ResponseEntity.ok(employeeService.deleteJobHistory(employeeId, jobId));
  }

  @GetMapping("/{employeeId}/history")
  public ResponseEntity<List<JobDetails>> getJobHistory(@PathVariable String employeeId) throws Exception {
    return ResponseEntity.ok(employeeService.getJobHistory(employeeId));
  }

    @GetMapping("/departments")
    public List<EmployeeDepartmentDTO> getDepartmentsByEmployeeIds(@RequestParam List<String> employeeIds) {
        String orgId = UserContext.getLoggedInUserOrganization().getId();
        return employeeRepository.findDesignationsByEmployeeIds(employeeIds, orgId);
    }
}
