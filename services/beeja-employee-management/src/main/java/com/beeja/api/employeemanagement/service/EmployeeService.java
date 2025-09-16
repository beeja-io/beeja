package com.beeja.api.employeemanagement.service;

import com.beeja.api.employeemanagement.model.Employee;
import com.beeja.api.employeemanagement.model.JobDetails;
import com.beeja.api.employeemanagement.model.clients.accounts.EmployeeBasicInfo;
import com.beeja.api.employeemanagement.requests.EmployeeUpdateRequest;
import com.beeja.api.employeemanagement.requests.UpdateKYCRequest;
import com.beeja.api.employeemanagement.response.EmployeeResponse;
import com.beeja.api.employeemanagement.response.EmployeeValues;
import com.beeja.api.employeemanagement.response.GetLimitedEmployee;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
  Employee createEmployee(Map<String, Object> employee) throws Exception;

  Map<String, Object> getEmployeeByEmployeeId(String employeeId) throws Exception;

  Employee updateEmployee(String id, EmployeeUpdateRequest updatedEmployee) throws Exception;

  EmployeeResponse getCombinedLimitedDataOfEmployees(
      String department,
      String designation,
      String employmentType,
      int pageNumber,
      int pageSize,
      String status)
      throws Exception;

  List<GetLimitedEmployee> getLimitedDataOfEmployees(
      String department,
      String designation,
      String employmentType,
      int pageNumber,
      int pageSize,
      String status);

  Employee updateKYCRequest(String id, UpdateKYCRequest updateKYCRequest) throws Exception;

  Employee uploadOrUpdateProfilePic(MultipartFile file, String employeeId) throws Exception;

  EmployeeValues getEmployeeValues() throws Exception;

  List<EmployeeBasicInfo> getAllEmpInfo(List<String> designations);

  Employee getEmployeeById(String employeeId);

  List<JobDetails> getEmploymentHistory(Employee employee);



}
