package com.beeja.api.employeemanagement.service;

import com.beeja.api.employeemanagement.client.AccountClient;
import com.beeja.api.employeemanagement.model.*;
import com.beeja.api.employeemanagement.model.clients.accounts.OrganizationDTO;
import com.beeja.api.employeemanagement.model.clients.accounts.UserDTO;
import com.beeja.api.employeemanagement.repository.EmployeeRepository;
import com.beeja.api.employeemanagement.requests.EmployeeOrgRequest;
import com.beeja.api.employeemanagement.requests.EmployeeUpdateRequest;
import com.beeja.api.employeemanagement.requests.UpdateKYCRequest;
import com.beeja.api.employeemanagement.response.GetLimitedEmployee;
import com.beeja.api.employeemanagement.serviceImpl.EmployeeServiceImpl;
import com.beeja.api.employeemanagement.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** */
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

  @InjectMocks private EmployeeServiceImpl employeeService;

  @Mock private EmployeeRepository employeeRepository;

  @Mock AccountClient accountClient;

  private UpdateKYCRequest validKYCRequest;
  private UpdateKYCRequest invalidKYCRequest;
  private Employee mockEmployee;
  private MockedStatic<UserContext> userContextMock;
  @Mock private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userContextMock = mockStatic(UserContext.class);
    KYCDetails validKYCDetails = new KYCDetails();
    validKYCDetails.setPanNumber("ABCDE1234F");
    validKYCDetails.setAadhaarNumber("123456789012");
    validKYCDetails.setPassportNumber("A1234567");

    BankDetails validBankDetails = new BankDetails();
    validBankDetails.setIfscCode("HDFC0001234");

    validKYCRequest = new UpdateKYCRequest();
    validKYCRequest.setKycDetails(validKYCDetails);
    validKYCRequest.setBankDetails(validBankDetails);

    KYCDetails invalidKYCDetails = new KYCDetails();
    invalidKYCDetails.setPanNumber("INVALIDPAN");
    invalidKYCDetails.setAadhaarNumber("INVALIDAADHAAR");
    invalidKYCDetails.setPassportNumber("INVALIDPASSPORT");

    invalidKYCRequest = new UpdateKYCRequest();
    invalidKYCRequest.setKycDetails(invalidKYCDetails);
    invalidKYCRequest.setBankDetails(validBankDetails);
    mockEmployee = new Employee();
    mockEmployee.setId("12345");
    mockEmployee.setEmployeeId("EMP001");
    mockEmployee.setKycDetails(validKYCDetails);
    mockEmployee.setBankDetails(validBankDetails);
  }

  @Test
  public void testCreateEmployee_Success() throws Exception {

    Map<String, Object> employeeData = new HashMap<>();
    employeeData.put("name", "John Doe");
    employeeData.put("position", "Software Engineer");
    employeeData.put("employmentType", "Full-Time");
    employeeData.put("organizationId", "org123");

    Employee savedEmployee = new Employee();
    savedEmployee.setId("12345");
    savedEmployee.setBeejaAccountId("acc789");
    savedEmployee.setPosition("Software Engineer");
    savedEmployee.setEmploymentType("Full-Time");
    savedEmployee.setOrganizationId("org123");

    when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

    Employee result = employeeService.createEmployee(employeeData);

    assertNotNull(result);
    assertEquals("12345", result.getId());
    assertEquals("Software Engineer", result.getPosition());
    assertEquals("Full-Time", result.getEmploymentType());
    assertEquals("org123", result.getOrganizationId());

    verify(employeeRepository, times(1)).save(any(Employee.class));
  }

  @Test
  public void testGetEmployeeByEmployeeId_Success() throws Exception {
    // Arrange
    String employeeId = "emp123";
    String organizationId = "org789";
    Employee employee = new Employee();
    employee.setId(employeeId);
    employee.setBeejaAccountId("acc456");
    employee.setEmploymentType("Full-Time");
    employee.setOrganizationId(organizationId);
    employee.setPosition("Software Engineer");

    PersonalInformation personalInformation = new PersonalInformation();
    personalInformation.setNationality("India");
    personalInformation.setGender("Female");
    personalInformation.setMaritalStatus("single");
    employee.setPersonalInformation(personalInformation);

    Address address = new Address();
    address.setCity("City");
    address.setState("State");
    employee.setAddress(address);
    when(employeeRepository.findByEmployeeIdAndOrganizationId(employeeId, organizationId))
        .thenReturn(employee);

    UserDTO mockUserDTO = new UserDTO();
    mockUserDTO.setId(employeeId);
    mockUserDTO.setEmail("user123@gmail.com");
    ResponseEntity<Object> accountResponse = ResponseEntity.ok(mockUserDTO);
    when(accountClient.getUserByEmployeeId(employeeId.toUpperCase())).thenReturn(accountResponse);

    OrganizationDTO mockOrganization = new OrganizationDTO();
    mockOrganization.setId(organizationId);
    Set<String> mockPermissions = Set.of("READ_COMPLETE_EMPLOYEE_DETAILS");
    userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);
    userContextMock.when(UserContext::getLoggedInEmployeeId).thenReturn(employeeId);
    UserContext.setLoggedInUser(
        "user@example.com",
        "John Doe",
        employeeId,
        mockOrganization,
        mockPermissions,
        new UserDTO(),
        "sampleToken");

    Map<String, Object> result = employeeService.getEmployeeByEmployeeId(employeeId);

    assertNotNull(result);

    Employee resultEmployee = (Employee) result.get("employee");
    assertEquals(employeeId, resultEmployee.getId());
    assertEquals("acc456", resultEmployee.getBeejaAccountId());
    assertEquals("Full-Time", resultEmployee.getEmploymentType());
    assertEquals(organizationId, resultEmployee.getOrganizationId());
    assertEquals("Software Engineer", resultEmployee.getPosition());
    UserDTO resultUserDTO = (UserDTO) result.get("account");
    assertNotNull(resultUserDTO);
    assertEquals(employeeId, resultUserDTO.getId());
    assertEquals("user123@gmail.com", resultUserDTO.getEmail());

    verify(employeeRepository, times(1))
        .findByEmployeeIdAndOrganizationId(employeeId, organizationId);
    verify(accountClient, times(1)).getUserByEmployeeId(employeeId.toUpperCase());
  }

  @Test
  public void testUpdateEmployee_Success() throws Exception {
    String employeeId = "emp123";
    String organizationId = "org789";

    EmployeeUpdateRequest updatedEmployee = new EmployeeUpdateRequest();
    updatedEmployee.setId(employeeId);
    updatedEmployee.setBeejaAccountId("acc456");
    updatedEmployee.setFirstName("John");
    updatedEmployee.setLastName("Doe");
    updatedEmployee.setEmail("john.doe@example.com");
    updatedEmployee.setEmployeeId(employeeId);
    updatedEmployee.setPosition("Senior Software Engineer");
    updatedEmployee.setDepartment("Engineering");

    JobDetails updatedJobDetails = new JobDetails();
    updatedEmployee.setJobDetails(updatedJobDetails);

    Contact updatedContact = new Contact();
    updatedContact.setPhone("123-456-7890");
    updatedEmployee.setContact(updatedContact);

    PFDetails updatedPFDetails = new PFDetails();
    updatedEmployee.setPfDetails(updatedPFDetails);

    OrganizationDTO mockOrganization = new OrganizationDTO();
    mockOrganization.setId(organizationId);
    Set<String> mockPermissions = Set.of("READ_COMPLETE_EMPLOYEE_DETAILS", "UPDATE_ALL_EMPLOYEES");
    userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);
    userContextMock.when(UserContext::getLoggedInEmployeeId).thenReturn(employeeId);
    UserContext.setLoggedInUser(
        "user@example.com",
        "John Doe",
        employeeId,
        mockOrganization,
        mockPermissions,
        new UserDTO(),
        "sampleToken");

    Employee existingEmployee = new Employee();
    existingEmployee.setId(employeeId);
    existingEmployee.setOrganizationId(organizationId);
    existingEmployee.setBeejaAccountId("acc456");
    existingEmployee.setEmployeeId(employeeId);
    existingEmployee.setPosition("Senior Software Engineer");

    when(employeeRepository.findByEmployeeIdAndOrganizationId(employeeId, organizationId))
        .thenReturn((existingEmployee));
    Map<String, String> organizations = new HashMap<>();
    organizations.put("id", organizationId);

    Map<String, Object> accountDetails = new HashMap<>();
    accountDetails.put("organizations", organizations);
    ResponseEntity<Object> accountResponse = ResponseEntity.ok(accountDetails);

    when(accountClient.getUserByEmployeeId(employeeId)).thenReturn(accountResponse);

    when(employeeRepository.save(any(Employee.class))).thenReturn(existingEmployee);
    Employee updated = employeeService.updateEmployee(employeeId, updatedEmployee);

    // Assert
    assertNotNull(updated);
    assertEquals(employeeId, updated.getId());
    assertEquals("acc456", updated.getBeejaAccountId());
    assertEquals("Senior Software Engineer", updated.getPosition());

    verify(employeeRepository, times(1))
        .findByEmployeeIdAndOrganizationId(employeeId, organizationId);
    verify(employeeRepository, times(1)).save(any(Employee.class)); // Ensure save is called
  }

  @Test
  public void testDeleteByOrganizationId_Success() {

    String organizationId = "org123";

    doNothing().when(employeeRepository).deleteByOrganizationId(organizationId);
    employeeRepository.deleteByOrganizationId(organizationId);
    verify(employeeRepository, times(1)).deleteByOrganizationId(organizationId);
  }

  @Test
  void updateKYCRequest_withValidData_returnsUpdatedEmployee() throws Exception {
    String employeeId = "emp123";
    String organizationId = "org789";
    OrganizationDTO mockOrganization = new OrganizationDTO();
    mockOrganization.setId(organizationId);

    userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);
    userContextMock.when(UserContext::getLoggedInEmployeeId).thenReturn(employeeId);

    Employee mockEmployee = new Employee();
    mockEmployee.setId(employeeId);
    mockEmployee.setOrganizationId(organizationId);

    KYCDetails kycDetails = new KYCDetails();
    kycDetails.setPanNumber("ABCDE1234F");
    kycDetails.setAadhaarNumber("123456789012");
    kycDetails.setPassportNumber("A1234567");
    mockEmployee.setKycDetails(kycDetails);

    when(employeeRepository.findByEmployeeIdAndOrganizationId(employeeId, organizationId))
        .thenReturn(mockEmployee);
    when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

    UpdateKYCRequest validKYCRequest = new UpdateKYCRequest();
    validKYCRequest.setKycDetails(kycDetails);

    Employee updatedEmployee = employeeService.updateKYCRequest(employeeId, validKYCRequest);

    assertNotNull(updatedEmployee);
    assertEquals(employeeId, updatedEmployee.getId());
    assertEquals("ABCDE1234F", updatedEmployee.getKycDetails().getPanNumber());
    assertEquals("123456789012", updatedEmployee.getKycDetails().getAadhaarNumber());
    assertEquals("A1234567", updatedEmployee.getKycDetails().getPassportNumber());

    verify(employeeRepository, times(1))
        .findByEmployeeIdAndOrganizationId(employeeId, organizationId);
    verify(employeeRepository, times(1)).save(mockEmployee);
  }

  @Test
  void testGetLimitedDataOfEmployees_Success() {

    String organizationId = "org789";
    OrganizationDTO mockOrganization = new OrganizationDTO();
    mockOrganization.setId(organizationId);
    Mockito.when(UserContext.getLoggedInUserOrganization()).thenReturn(mockOrganization);

    String department = "Engineering";
    String designation = "Developer";
    String employmentType = "Full-Time";
    int pageNumber = 1;
    int pageSize = 10;
    String status = "Active";

    Employee mockEmployee = new Employee();
    mockEmployee.setEmployeeId("EMP001");
    mockEmployee.setJobDetails(new JobDetails());
    List<Employee> employees = List.of(mockEmployee);

    Mockito.when(mongoTemplate.find(Mockito.any(Query.class), Mockito.eq(Employee.class)))
        .thenReturn(employees);

    Map<String, Object> accountResponseData = Map.of("employeeId", "EMP001", "active", true);
    List<Map<String, Object>> accountResponseList = List.of(accountResponseData);
    Mockito.when(accountClient.getUsersByEmployeeIds(Mockito.any(EmployeeOrgRequest.class)))
        .thenReturn(new ResponseEntity<>(accountResponseList, HttpStatus.OK));
    GetLimitedEmployee limitedEmployee = new GetLimitedEmployee();
    limitedEmployee.setId("1");
    limitedEmployee.setEmployeeId("EMP001");
    limitedEmployee.setJobDetails(new JobDetails());
    limitedEmployee.setProfilePictureId("pic1");
    AggregationResults<GetLimitedEmployee> aggregationResults =
        Mockito.mock(AggregationResults.class);
    Mockito.when(aggregationResults.getMappedResults()).thenReturn(List.of(limitedEmployee));
    Mockito.when(
            mongoTemplate.aggregate(
                Mockito.any(Aggregation.class),
                Mockito.eq("employees"),
                Mockito.eq(GetLimitedEmployee.class)))
        .thenReturn(aggregationResults);

    List<GetLimitedEmployee> result =
        employeeService.getLimitedDataOfEmployees(
            department, designation, employmentType, pageNumber, pageSize, status);

    assertNotNull(result, "Result should not be null");
    assertEquals(1, result.size(), "Result size should be 1");
    assertEquals("EMP001", result.get(0).getEmployeeId(), "First employee ID should match");
    Mockito.verify(mongoTemplate, Mockito.times(1))
        .aggregate(
            Mockito.any(Aggregation.class),
            Mockito.eq("employees"),
            Mockito.eq(GetLimitedEmployee.class));
  }

  @AfterEach
  void tearDown() {
    if (userContextMock != null) {
      userContextMock.close();
    }
  }
}
