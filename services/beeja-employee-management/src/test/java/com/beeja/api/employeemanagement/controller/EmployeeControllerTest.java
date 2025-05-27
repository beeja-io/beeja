package com.beeja.api.employeemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.beeja.api.employeemanagement.client.AccountClient;
import com.beeja.api.employeemanagement.config.filters.JwtProperties;
import com.beeja.api.employeemanagement.enums.ErrorCode;
import com.beeja.api.employeemanagement.enums.ErrorType;
import com.beeja.api.employeemanagement.model.*;
import com.beeja.api.employeemanagement.requests.EmployeeUpdateRequest;
import com.beeja.api.employeemanagement.requests.UpdateKYCRequest;
import com.beeja.api.employeemanagement.response.EmployeeResponse;
import com.beeja.api.employeemanagement.service.EmployeeService;
import com.beeja.api.employeemanagement.service.FileService;
import com.beeja.api.employeemanagement.utils.Constants;
import com.beeja.api.employeemanagement.utils.UserContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(
        controllers = EmployeeController.class, // Specify the controller you are testing
        properties = "server.port=0" // Assign a random available port for the test
)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

  private static final String BASE_URL = "/v1/users";


  @MockBean JwtProperties jwtProperties;

  @InjectMocks EmployeeController employeeController;

  @MockBean EmployeeService employeeService;

  @MockBean FileService fileService;

  @MockBean AccountClient accountClient;

  @MockBean UserContext userContext;

  @Autowired MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  Employee employee;

  @Test
  void testGetAllEmployees_withAllParams() throws Exception {
    List<Map<String, Object>> employeeList =
            List.of(Map.of("id", "1", "name", "John Doe"), Map.of("id", "2", "name", "Jane Doe"));
    EmployeeResponse mockResponse = new EmployeeResponse(employeeList, 2L);
    Mockito.when(
                    employeeService.getCombinedLimitedDataOfEmployees(
                            "IT", "Developer", "Full-Time", 1, 10, "active"))
            .thenReturn(mockResponse);
    mockMvc
            .perform(
                    get(BASE_URL)
                            .param("department", "IT")
                            .param("designation", "Developer")
                            .param("employmentType", "Full-Time")
                            .param("status", "active")
                            .param("pageNumber", "1")
                            .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.employeeList.size()").value(2))
            .andExpect(jsonPath("$.employeeList[0].name").value("John Doe"))
            .andExpect(jsonPath("$.employeeList[1].name").value("Jane Doe"))
            .andExpect(jsonPath("$.totalSize").value(2));
    Mockito.verify(employeeService)
            .getCombinedLimitedDataOfEmployees("IT", "Developer", "Full-Time", 1, 10, "active");
  }

  @Test
  void testGetAllEmployees_withDefaultParams() throws Exception {
    List<Map<String, Object>> mockEmployeeList = List.of(Map.of("id", "3", "name", "Sam Smith"));

    EmployeeResponse mockResponse = new EmployeeResponse(mockEmployeeList, 1L);

    Mockito.when(
                    employeeService.getCombinedLimitedDataOfEmployees(null, null, null, 1, 10, "active"))
            .thenReturn(mockResponse);

    mockMvc
            .perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.employeeList.size()").value(1))
            .andExpect(jsonPath("$.employeeList[0].name").value("Sam Smith"))
            .andExpect(jsonPath("$.totalSize").value(1));

    Mockito.verify(employeeService)
            .getCombinedLimitedDataOfEmployees(null, null, null, 1, 10, "active");
  }

  @Test
  void testGetAllEmployees_withEmptyResponse() throws Exception {
    EmployeeResponse mockResponse = new EmployeeResponse(Collections.emptyList(), 0L);
    Mockito.when(
                    employeeService.getCombinedLimitedDataOfEmployees(null, null, null, 1, 10, "inactive"))
            .thenReturn((mockResponse));

    mockMvc
            .perform(get(BASE_URL).param("status", "inactive"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.employeeList.size()").value(0))
            .andExpect(jsonPath("$.totalSize").value(0));

    Mockito.verify(employeeService)
            .getCombinedLimitedDataOfEmployees(null, null, null, 1, 10, "inactive");
  }

  @Test
  void testCreateEmployee() throws Exception {
    Map<String, Object> user = new HashMap<>();
    user.put("id", "1");
    user.put("employeeId", "12345");
    Employee employee1 = new Employee();
    employee1.setBeejaAccountId(((String) user.get("id")));
    employee1.setEmployeeId(((String) user.get("employeeId")));

    when(employeeService.createEmployee(user)).thenReturn(employee1);

    String userJson = objectMapper.writeValueAsString(user);

    mockMvc
            .perform(
                    MockMvcRequestBuilders.post("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
            .andExpect(status().isCreated());
  }

  @Test
  void testUpdateEmployee() throws Exception {
    String employeeId = "12345";
    EmployeeUpdateRequest employeeUpdateRequest = new EmployeeUpdateRequest();
    employeeUpdateRequest.setEmployeeId("12345");
    employeeUpdateRequest.setPosition("developer");
    Employee employee1 = new Employee();
    employee1.setEmployeeId("12345");
    employee1.setPosition("developer");

    when(employeeService.updateEmployee(employeeId, employeeUpdateRequest)).thenReturn(employee1);

    String employeeUpdateJson = objectMapper.writeValueAsString(employeeUpdateRequest);

    mockMvc
            .perform(
                    MockMvcRequestBuilders.put("/v1/users/{employeeId}", employeeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(employeeUpdateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.employeeId").value("12345"))
            .andExpect(jsonPath("$.position").value("developer"));
  }

  @Test
  void testUpdateEmployee_employeeNotFound() throws Exception {
    String employeeId = "99999";
    EmployeeUpdateRequest employeeUpdateRequest = new EmployeeUpdateRequest();
    employeeUpdateRequest.setEmployeeId("99999");
    employeeUpdateRequest.setPosition("developer");

    when(employeeService.updateEmployee(employeeId, employeeUpdateRequest))
            .thenThrow(new NoSuchElementException("Employee not found"));

    String employeeUpdateJson = objectMapper.writeValueAsString(employeeUpdateRequest);

    mockMvc
            .perform(
                    MockMvcRequestBuilders.put("/v1/users/{employeeId}", employeeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(employeeUpdateJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.type").value(ErrorType.RESOURCE_NOT_FOUND_ERROR.toString()))
            .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.toString()))
            .andExpect(jsonPath("$.message").value(Constants.EMPLOYEE_NOT_FOUND));
  }

  @Test
  public void testGetEmployeeByEmployeeId() throws Exception {
    String employeeId = "123";
    Employee emp = new Employee();
    emp.setPosition("manager");
    emp.setJobDetails(new JobDetails());

    Map<String, Object> employeeData = new HashMap<>();
    employeeData.put("position", emp.getPosition());
    employeeData.put("jobDetails", emp.getJobDetails());

    when(employeeService.getEmployeeByEmployeeId(anyString())).thenReturn(employeeData);

    mockMvc
            .perform(
                    MockMvcRequestBuilders.get("/v1/users/{employeeId}", employeeId)
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.position").value("manager"))
            .andExpect(jsonPath("$.jobDetails").exists());
  }



  @Test
  void testUpdateKycDetails_success() throws Exception {
    String employeeId = "123";

    KYCDetails kycDetails = new KYCDetails();
    kycDetails.setPanNumber("ABCDE1234F");
    kycDetails.setAadhaarNumber("123456789012");
    kycDetails.setPassportNumber("A1234567");

    BankDetails bankDetails = new BankDetails();
    bankDetails.setAccountNo(1234567890L);
    bankDetails.setIfscCode("IFSC0001");
    bankDetails.setBankName("Test Bank");
    bankDetails.setBranchName("Test Branch");
    UpdateKYCRequest updateKYCRequest = new UpdateKYCRequest();
    updateKYCRequest.setKycDetails(kycDetails);
    updateKYCRequest.setBankDetails(bankDetails);

    Employee updatedEmployee = new Employee();
    updatedEmployee.setId(employeeId);

    Mockito.when(employeeService.updateKYCRequest(eq(employeeId), any(UpdateKYCRequest.class)))
            .thenReturn(updatedEmployee);

    ObjectMapper objectMapper = new ObjectMapper();

    mockMvc
            .perform(
                    patch("/v1/users/{employeeId}/kyc", employeeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateKYCRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(employeeId));
  }
}