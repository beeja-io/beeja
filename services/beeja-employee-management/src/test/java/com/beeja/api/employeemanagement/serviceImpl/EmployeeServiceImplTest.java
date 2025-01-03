package com.beeja.api.employeemanagement.serviceImpl;

import com.beeja.api.employeemanagement.client.AccountClient;
import com.beeja.api.employeemanagement.model.*;
import com.beeja.api.employeemanagement.model.clients.accounts.OrganizationDTO;
import com.beeja.api.employeemanagement.repository.EmployeeRepository;
import com.beeja.api.employeemanagement.requests.EmployeeUpdateRequest;
import com.beeja.api.employeemanagement.service.EmployeeService;
import com.beeja.api.employeemanagement.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.beeja.api.employeemanagement.constants.PermissionConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class EmployeeServiceImplTest {

  @InjectMocks private EmployeeServiceImpl employeeServiceImpl;
  @Mock private EmployeeRepository employeeRepository;

  @Mock AccountClient accountClient;
  @Mock MongoTemplate mongoTemplate;
  @Mock EmployeeService employeeService;
  private MockedStatic<UserContext> userContextMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  public Employee createEmployee() {
    Employee employee = new Employee();
    employee.setId("testId");
    employee.setEmployeeId("testEmployeeId");
    employee.setBeejaAccountId("testBeejaId");
    employee.setPosition("position");
    return employee;
  }

  public void createUserContext() {
    OrganizationDTO loggedInUserOrganization = new OrganizationDTO();
    loggedInUserOrganization.setId("testId");
    UserContext.setLoggedInUserOrganization(loggedInUserOrganization);
    Set<String> loggedInUserPermissions = new HashSet<>();
    loggedInUserPermissions.add(CREATE_EMPLOYEE);
    loggedInUserPermissions.add(UPDATE_ALL_EMPLOYEES);
    UserContext.setLoggedInUserPermissions(loggedInUserPermissions);
    UserContext.setLoggedInEmployeeId("existingId");
  }

  public EmployeeUpdateRequest createEmployeeUpdateRequest() {
    EmployeeUpdateRequest updateEmployee = new EmployeeUpdateRequest();
    updateEmployee.setEmail("test-accounts@test.com");

    Address address = new Address();
    address.setHouseNumber("house No.");
    address.setLandMark("landMark");
    address.setVillage("village");
    address.setCity("city");
    address.setCountry("country");
    address.setState("state");
    address.setPinCode("pincode");
    updateEmployee.setAddress(address);

    PersonalInformation personalInfo = new PersonalInformation();
    personalInfo.setNationality("Country");
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    try {
      Date dateOfBirth = formatter.parse("2000-01-01");
      personalInfo.setDateOfBirth(dateOfBirth);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    personalInfo.setGender("Gender");
    personalInfo.setMaritalStatus("Status");
    updateEmployee.setPersonalInformation(personalInfo);

    JobDetails jobDetails = new JobDetails();
    jobDetails.setDesignation("Developer");
    jobDetails.setEmployementType("Full-time");
    jobDetails.setDepartment("IT");
    jobDetails.setJoiningDate(new Date());
    jobDetails.setResignationDate(new Date());
    updateEmployee.setJobDetails(jobDetails);

    Contact contact = new Contact();
    contact.setAlternativeEmail("test@example.com");
    contact.setPhone("123-456-7890");
    contact.setAlternativePhone("987-654-3210");
    updateEmployee.setContact(contact);

    PFDetails pfDetails = new PFDetails();
    pfDetails.setPFNumber("PF12345");
    pfDetails.setUAN("UAN6789");
    pfDetails.setJoiningData("2023-01-01");
    pfDetails.setAccountNumber("1234567890");
    pfDetails.setState("YourState");
    pfDetails.setLocation("YourLocation");
    updateEmployee.setPfDetails(pfDetails);

    return updateEmployee;
  }

  private Map<String, Object> convertEmployeeToMap(Employee employee) {
    Map<String, Object> employeeMap = new HashMap<>();
    employeeMap.put("id", employee.getId());
    employeeMap.put("employeeId", employee.getEmployeeId());
    employeeMap.put("beejaAccountId", employee.getBeejaAccountId());
    employeeMap.put("position", employee.getPosition());
    return employeeMap;
  }

  @Test
  void testCreateEmployee() throws Exception {
    Employee employee = createEmployee();
    when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

    Employee result = employeeServiceImpl.createEmployee(convertEmployeeToMap(employee));

    assertNotNull(result);
    assertEquals(result.getId(), employee.getId());
  }

  @Test
  public void testGetEmployeeByEmployeeId() throws Exception {
    Employee mockEmployee = new Employee();
    mockEmployee.setId("1");

    OrganizationDTO organizationDTO = new OrganizationDTO();
    organizationDTO.setId("orgId");

    Set<String> mockPermissions = Set.of(READ_COMPLETE_EMPLOYEE_DETAILS);

    UserContext.setLoggedInUser(
        "test@example.com",
        "Test User",
        "1",
        organizationDTO,
        mockPermissions,
        null,
        "mockAccessToken");

    when(employeeRepository.findByEmployeeIdAndOrganizationId("1", "orgId"))
        .thenReturn(mockEmployee);

    Map<String, Object> mockAccountData = new HashMap<>();
    mockAccountData.put("accountId", "accountId123");

    ResponseEntity mockAccountResponse = ResponseEntity.ok(mockAccountData);

    when(accountClient.getUserByEmployeeId("1")).thenReturn(mockAccountResponse);

    Map<String, Object> result = employeeServiceImpl.getEmployeeByEmployeeId("1");

    assertNotNull(result);
    assertEquals("1", ((Employee) result.get("employee")).getId());
    assertNotNull(result.get("account"));
    assertEquals("accountId123", ((Map<String, Object>) result.get("account")).get("accountId"));
  }

  @Test
  void testEmployeeNotFoundExceptionWhenUpdateEmployee() {

    EmployeeUpdateRequest updateEmployee = createEmployeeUpdateRequest();
    when(accountClient.isUserPresentWithMail(updateEmployee.getEmail())).thenReturn(true);

    Exception exception =
        assertThrows(
            Exception.class, () -> employeeServiceImpl.updateEmployee("id", updateEmployee));

    assertEquals(
        "RESOURCE_EXISTS_ERROR,RESOURCE_CREATING_ERROR,Email is already registered",
        exception.getMessage());
  }

  @Test
  void testUpdateEmployeeWhenUserHasPermissionOfCreateEmployee() throws Exception {
    createUserContext();
    EmployeeUpdateRequest updateEmployee = createEmployeeUpdateRequest();

    Employee existingEmployee = createEmployee();
    when(employeeRepository.findByEmployeeIdAndOrganizationId(
            "id", UserContext.getLoggedInUserOrganization().getId()))
        .thenReturn(existingEmployee);

    ResponseEntity<Map<String, Object>> accountsResponse = mock(ResponseEntity.class);
    Map<String, Object> accountDetails = new HashMap<>();
    accountDetails.put("organizations", Collections.singletonMap("id", "testId"));

    when(accountsResponse.getStatusCode()).thenReturn(HttpStatus.OK);
    when(accountsResponse.getBody()).thenReturn(accountDetails);
    doReturn(accountsResponse).when(accountClient).getUserByEmployeeId(any(String.class));

    when(employeeRepository.save(any())).thenReturn(existingEmployee);

    Employee result = employeeServiceImpl.updateEmployee("id", updateEmployee);

    assertEquals(existingEmployee, result);
  }


}
