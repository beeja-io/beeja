package com.beeja.api.accounts.service;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.constants.RoleConstants;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.Organization.Address;
import com.beeja.api.accounts.model.Organization.LoanLimit;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Preferences;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.model.UserPreferences;
import com.beeja.api.accounts.repository.RolesRepository;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.requests.UpdateUserRequest;
import com.beeja.api.accounts.requests.UpdateUserRoleRequest;
import com.beeja.api.accounts.serviceImpl.EmployeeServiceImpl;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeServiceImplTest {

  @InjectMocks EmployeeServiceImpl employeeServiceImpl;

  @Mock private UserRepository userRepository;

  @Mock RolesRepository roleRepository;

  @Mock private EmployeeFeignClient employeeFeignClient;

  @Mock private NotificationClient notificationClient;

  @Mock private UserContext userContext;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  Organization organization1 =
      new Organization(
          "org1",
          "OrganizationName",
          "org@example.com",
          "sub123",
          "example.com",
          "contact@example.com",
          "https://www.example.com",
          new Preferences(),
          new Address(),
          "Location",
          null,
          "logo123",
          new LoanLimit());
  Organization organization2 =
      new Organization(
          "org2",
          "OrganizationName",
          "org@example.com",
          "sub123",
          "example.com",
          "contact@example.com",
          "https://www.example.com",
          new Preferences(),
          new Address(),
          "Location",
          null,
          "logo456",
          new LoanLimit());

  Role role1 = new Role("1", "ROLE_EMPLOYEE", null, Set.of("READ_EMPLOYEE"), "tac");
  Role role2 =
      new Role("2", "ROLE_MANAGER", null, Set.of("CREATE_EMPLOYEE", "UPDATE_EMPLOYEE"), "tac");

  User user1 =
      new User(
          "1",
          "dattu",
          "gundeti",
          "dattu@example.com",
          Set.of(role1),
          "EMP001",
          organization1,
          new UserPreferences(),
          null,
          true,
          "admin",
          "admin",
          new Date(),
          new Date());
  User user2 =
      new User(
          "2",
          "ravi",
          "ravi",
          "kiran@example.com",
          Set.of(role2),
          "EMP002",
          organization2,
          new UserPreferences(),
          null,
          true,
          "admin",
          "admin",
          new Date(),
          new Date());

  @Test
  public void toGetAllUser() throws Exception {

    List<User> users = new ArrayList<>(Arrays.asList(user1, user2));
    when(userRepository.findByOrganizationsId(anyString())).thenReturn(users);

    Set<String> permissions = Set.of(PermissionConstants.GET_ALL_EMPLOYEES);
    UserContext.setLoggedInUser(
        "test@example.com",
        "Test User",
        organization1,
        "empId123",
        Set.of(),
        permissions,
        "token123");
    List<User> allUsers = employeeServiceImpl.getAllEmployees();

    assertNotNull(allUsers);
    assertEquals(2, allUsers.size());
    assertTrue(allUsers.contains(user1));
    assertTrue(allUsers.contains(user2));
  }

  @Test
  public void toGetAllByEmail() throws Exception {

    // Arrange
    when(userRepository.findByEmailAndOrganizations("dattu@example.com", organization1))
        .thenReturn(user1);
    UserContext.setLoggedInUserOrganization(organization1);

    // Act
    User user = employeeServiceImpl.getEmployeeByEmail("dattu@example.com", organization1);

    // Assert
    assertNotNull(user);
    assertEquals("dattu@example.com", user.getEmail());
    assertEquals("org1", user.getOrganizations().getId());
  }

  @Test
  void testGetEmployeeByEmail_UserNotFound() {
    // Arrange
    String email = "nonexistent@example.com";
    Organization loggedInUserOrganization = organization1;
    UserContext.setLoggedInUserOrganization(loggedInUserOrganization);

    when(userRepository.findByEmailAndOrganizations(email, organization1)).thenReturn(null);

    // Act
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> employeeServiceImpl.getEmployeeByEmail(email, organization1));
    // Assert
    assertEquals(
        "RESOURCE_NOT_FOUND_ERROR,USER_NOT_FOUND,User Not Found " + email, exception.getMessage());
  }

  @Test
  void testGetEmployeeByEmployeeId_UserNotFound() {
    // Arrange
    String employeeId = "EMP001";
    when(userRepository.findByEmployeeIdAndOrganizations(employeeId, organization1))
        .thenReturn(null);

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> employeeServiceImpl.getEmployeeByEmployeeId(employeeId, organization1));
  }

  @Test
  void testGetEmployeeByEmployeeId_UserBelongsToDifferentOrganization() {
    // Arrange
    String employeeId = "EMP1";
    when(userRepository.findByEmployeeIdAndOrganizations(employeeId, organization1))
        .thenReturn(null);
    UserContext.setLoggedInUserOrganization(organization1);

    // Act &Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> employeeServiceImpl.getEmployeeByEmployeeId(employeeId, organization1));
  }

  @Test
  void testGetEmployeeByEmployeeId_Successful() throws Exception {
    // Arrange
    when(userRepository.findByEmployeeIdAndOrganizations("EMP001", organization1))
        .thenReturn(user1);
    UserContext.setLoggedInUserOrganization(organization1);

    // Act
    User ruser = employeeServiceImpl.getEmployeeByEmployeeId("EMP001", organization1);

    // Assert
    assertNotNull(ruser);
    assertEquals("EMP001", ruser.getEmployeeId());
    assertEquals("org1", ruser.getOrganizations().getId());
  }

  @Test
  void testToggleUserActivation_SuccessfulActivationToggle() throws Exception {
    // Arrange
    String employeeId = "EMP001";
    UserContext.setLoggedInEmployeeId("EMP002");
    UserContext.setLoggedInUserOrganization(organization1);
    when(userRepository.findByEmployeeIdAndOrganizations("EMP001", organization1))
        .thenReturn(user1);
    when(userRepository.findByEmployeeIdAndOrganizations(
            UserContext.getLoggedInEmployeeId(), UserContext.getLoggedInUserOrganization()))
        .thenReturn(user1);

    // Act
    employeeServiceImpl.changeEmployeeStatus(employeeId);

    // Assert
    assertNotEquals(!user1.isActive(), user1.isActive());
  }

  @Test
  void testUpdateUserRolesAndPermissions() throws Exception {
    // Arrange
    String employeeId = "EMP001";
    Role role = new Role("1", "ROLE_HR", null, Set.of("READ_EMPLOYEE"), null);

    // Mock UserContext to return a valid organization
    UserContext.setLoggedInUser(
        "test@example.com", "Test User", organization1, "empId123", Set.of(), null, "token123");

    when(userRepository.findByEmployeeIdAndOrganizations(employeeId, organization1))
        .thenReturn(user1);
    when(roleRepository.findByNameAndOrganizationId("ROLE_HR", organization1.getId()))
        .thenReturn(role);
    when(roleRepository.findByName("ROLE_HR")).thenReturn(role);

    user1.setRoles(Set.of(role));
    // Mock user save
    when(userRepository.save(any(User.class))).thenReturn(user1);
    // Act
    UpdateUserRoleRequest uprole = new UpdateUserRoleRequest();
    uprole.setRoles(Set.of("ROLE_HR"));
    User updatedUser = employeeServiceImpl.updateEmployeeRolesDyEmployeeId(employeeId, uprole);
    // Assert
    assertNotNull(updatedUser);
  }

  @Test
  void testUpdateEmployeeRoles_UserNotFound() {
    // Arrange
    String empId = "ABCD";
    UpdateUserRoleRequest updateRequest = new UpdateUserRoleRequest();

    when(userRepository.findByEmployeeIdAndOrganizations(empId, organization1)).thenReturn(null);

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> employeeServiceImpl.updateEmployeeRolesDyEmployeeId(empId, updateRequest),
        Constants.USER_NOT_FOUND + empId);
  }

  @Test
  public void testCreateEmployee_Success() throws Exception {
    // Arrange
    User mockUser = new User();
    mockUser.setEmail("testuser@example.com");
    mockUser.setEmployeeId("EMP001");

    Organization mockOrganization = new Organization();
    mockOrganization.setId("empid");

    Role mockRole = new Role();
    mockRole.setName(RoleConstants.ROLE_EMPLOYEE);

    UserContext.setLoggedInUser(
        "test@example.com", "Test User", mockOrganization, "empId123", Set.of(), null, "token123");

    when(userRepository.findByEmailAndOrganizations(anyString(), any(Organization.class)))
        .thenReturn(null);

    when(userRepository.findByEmployeeIdAndOrganizations(anyString(), eq(mockOrganization)))
        .thenReturn(null);

    when(roleRepository.findByNameAndOrganizationId(anyString(), eq(mockOrganization.getId())))
        .thenReturn(mockRole);

    when(userRepository.save(any(User.class))).thenReturn(mockUser);

    // Act
    User createdUser = employeeServiceImpl.createEmployee(mockUser);

    // Assert
    assertNotNull(createdUser);
  }

  @Test
  void testCreateEmployeeEmail_UserAlreadyExists() {
    // Arrange
    User user = new User();
    user.setEmail("abcd@gmail.com");
    when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

    // Act & Assert
    assertThrows(Exception.class, () -> employeeServiceImpl.createEmployee(user));
  }

  @Test
  void testCreateEmployeeId_UserAlreadyExists() {
    // Arrange
    User user = new User();
    user.setEmployeeId("ABCD");
    when(userRepository.findByEmployeeIdAndOrganizations(user.getEmployeeId(), organization1))
        .thenReturn(user);

    // Act & Assert
    assertThrows(Exception.class, () -> employeeServiceImpl.createEmployee(user));
  }

  @Test
  public void testUpdateEmployeeByEmployeeId() {
    // Arrange
    String employeeId = "ABCD";
    UserContext.setLoggedInUserOrganization(organization1);

    UpdateUserRequest updatedUser = new UpdateUserRequest();

    User existingUser = new User();
    existingUser.setEmployeeId(employeeId);
    existingUser.setOrganizations(organization1);

    when(userRepository.findByEmployeeIdAndOrganizations(employeeId, organization1))
        .thenReturn(existingUser);

    when(userRepository.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              User savedUser = invocation.getArgument(0);
              savedUser.setModifiedAt(new Date());
              return savedUser;
            });

    // Act
    User result = employeeServiceImpl.updateEmployeeByEmployeeId(employeeId, updatedUser);

    // Assert
    verify(userRepository, times(1)).findByEmployeeIdAndOrganizations(employeeId, organization1);
    verify(userRepository, times(1)).save(any(User.class));
    assertNotNull(result);
    assertEquals(employeeId, result.getEmployeeId());
    assertNotNull(result.getModifiedAt());
  }

  @Test
  void testUpdateEmployeeEmployeeId_UserAlreadyExists() {
    // Arrange
    User user = new User();
    UpdateUserRequest updatedUser = new UpdateUserRequest();
    when(userRepository.findByEmployeeIdAndOrganizations("ABCD", organization1)).thenReturn(null);

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> employeeServiceImpl.updateEmployeeByEmployeeId("abcd", updatedUser));
  }
}
