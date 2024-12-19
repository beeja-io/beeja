package com.beeja.api.accounts.controllers;

import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.exceptions.UserNotFoundException;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.model.UserPreferences;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.requests.UpdateUserRequest;
import com.beeja.api.accounts.requests.UpdateUserRoleRequest;
import com.beeja.api.accounts.service.EmployeeService;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmployeecontrollerTest {

  @InjectMocks EmployeeController employeeController;

  @Autowired MockMvc mockMvc;

  @Mock private EmployeeService employeeService;
  @Mock private BindingResult bindingResult;
  @Mock UserRepository userRepository;

  private String basePath = "/v1/users";

  User user1 =
      new User(
          "1",
          "dattu",
          "gundeti",
          "dattu@example.com",
          new HashSet<>(),
          "EMP001",
          new Organization(),
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
          new HashSet<>(),
          "EMP002",
          new Organization(),
          new UserPreferences(),
          null,
          true,
          "admin",
          "admin",
          new Date(),
          new Date());

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
  }

  @Test
  void toGetAllUsers() throws Exception {
    // Arrange
    UserContext.setLoggedInUserPermissions(
        Collections.singleton(PermissionConstants.GET_ALL_EMPLOYEES));
    UserContext.setLoggedInUserOrganization(new Organization());

    List<User> users = Arrays.asList(user1, user2);
    when(employeeService.getAllEmployees()).thenReturn(users);

    // Act
    ResponseEntity<?> responseEntity = employeeController.getAllEmployees();

    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(users, responseEntity.getBody());
  }

  @Test
  void toGetUsersByEmail() throws Exception {
    // Arrange
    when(employeeService.getEmployeeByEmail(
            "dattu@example.com", UserContext.getLoggedInUserOrganization()))
        .thenReturn(user1);

    // Act & Assert
    mockMvc
        .perform(MockMvcRequestBuilders.get("/v1/users/email/{email}", "dattu@example.com"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(print());
  }

  @Test
  void testGetUserByEmail_UserNotFound() throws Exception {
    // Arrange
    String email = "abc@gmail.com";

    when(employeeService.getEmployeeByEmail(email, UserContext.getLoggedInUserOrganization()))
        .thenThrow(new UserNotFoundException("User not found: " + email));

    // Act & Assert
    assertThrows(
        UserNotFoundException.class,
        () -> {
          employeeController.getUserByEmail(email);
        });
  }

  @Test
  void testGetUserByEmail_InternalServerError() throws Exception {
    // Arrange
    String email = "abc@gmail.com";

    when(employeeService.getEmployeeByEmail(email, UserContext.getLoggedInUserOrganization()))
        .thenThrow(new RuntimeException("Internal server error"));

    // Act & Assert
    assertThrows(
        RuntimeException.class,
        () -> {
          employeeController.getUserByEmail(email);
        });
  }

  @Test
  public void testIsUserPresentWithMail_UserPresent() throws Exception {
    // Arrange
    String userEmail = "abc@gmail.com";
    User mockUser = new User();
    mockUser.setEmail(userEmail);
    when(employeeService.getEmployeeByEmail(userEmail, UserContext.getLoggedInUserOrganization()))
        .thenReturn(mockUser);

    // Act
    Boolean result = employeeController.isUserPresentWithMail(userEmail);

    // Assert
    assertTrue(result);
  }

  @Test
  public void testIsUserPresentWithMail_UserNotPresent() throws Exception {

    // Arrange
    String userEmail = "abc@gmail.com";
    when(employeeService.getEmployeeByEmail(userEmail, UserContext.getLoggedInUserOrganization()))
        .thenThrow(new UserNotFoundException("User not found"));

    // Act
    Boolean result = employeeController.isUserPresentWithMail(userEmail);

    // Assert
    assertFalse(result);
  }

  @Test
  void testGetUserByEmployeeId() throws Exception {
    // Arrange
    String employeeId = "ABC";

    when(employeeService.getEmployeeByEmployeeId(
            employeeId, UserContext.getLoggedInUserOrganization()))
        .thenReturn(user1);

    // Act & Assert
    mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/users/{employeeId}", employeeId)
            .contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void testGetUserByEmployeeId_UserNotFound() throws Exception {
    // Arrange
    String empId = "abc@gmail.com";
    when(employeeController.getUserByEmployeeId(empId))
        .thenThrow(new UserNotFoundException("User not found: " + empId));

    // Act & Assert
    mockMvc
        .perform(MockMvcRequestBuilders.get(basePath + "/empid/{empId}", empId))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  void testGetUserByEmployeeId_InternalServerError() throws Exception {
    // Arrange
    String empId = "ABCD";
    when(employeeService.getEmployeeByEmployeeId(empId, UserContext.getLoggedInUserOrganization()))
        .thenThrow(new RuntimeException("Internal server error"));

    // Act & Assert
    assertThrows(
        RuntimeException.class,
        () -> {
          employeeController.getUserByEmployeeId(empId);
        });
  }

  @Test
  void testgetLoggedinuser() throws Exception {
    // Arrange
    UserContext.setLoggedInUserEmail("dattu@gmail.com");
    when(employeeService.getEmployeeByEmail(
            "dattu@gmail.com", UserContext.getLoggedInUserOrganization()))
        .thenReturn(user1);

    // Act & Assert
    mockMvc.perform(
        MockMvcRequestBuilders.get(basePath + "/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(user1.toString()));
  }

  @Test
  void testGetLoggedInUserNotFound() throws Exception {
    // Arrange
    UserContext.setLoggedInUserEmail("nonexistent@example.com");
    when(employeeService.getEmployeeByEmail(
            "nonexistent@example.com", UserContext.getLoggedInUserOrganization()))
        .thenReturn(null);

    // Act & Assert
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/me").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testCreateEmployee_Success() throws Exception {
    // Arrange
    User user = new User();
    when(employeeService.createEmployee(user)).thenReturn(user);

    // Act
    ResponseEntity<?> responseEntity = employeeController.createEmployee(user, bindingResult);

    // Assert
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(user, responseEntity.getBody());
  }

  @Test
  public void testCreateEmployee_ValidationError() throws Exception {
    // Arrange: Set up a User object without mandatory fields
    User user = new User(); // assuming User has setters for each field

    // Create a list of errors
    List<ObjectError> errors = new ArrayList<>();
    errors.add(new FieldError("user", "firstName", "First name is mandatory"));
    errors.add(new FieldError("user", "lastName", "Last name is mandatory"));
    errors.add(new FieldError("user", "email", "Email is mandatory"));
    errors.add(new FieldError("user", "employeeId", "EmployeeID is mandatory"));

    // Mock the behavior of BindingResult
    when(bindingResult.hasErrors()).thenReturn(true);
    when(bindingResult.getAllErrors()).thenReturn(errors);

    // Act & Assert: Verify that BadRequestException is thrown
    BadRequestException exception =
        assertThrows(
            BadRequestException.class,
            () -> {
              employeeController.createEmployee(user, bindingResult);
            });

    // Check the message of the thrown exception
    assertEquals(
        "[First name is mandatory, Last name is mandatory, Email is mandatory, EmployeeID is mandatory]",
        exception.getMessage());
  }

  @Test
  public void testCreateEmployee_UserNotFoundException() throws Exception {
    // Arrange
    User user = new User();
    when(employeeService.createEmployee(user))
        .thenThrow(new UserNotFoundException("User not found"));

    // Act & Assert
    UserNotFoundException exception =
        assertThrows(
            UserNotFoundException.class,
            () -> {
              employeeController.createEmployee(user, bindingResult);
            });

    // Assert the exception message
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  public void testCreateEmployee_InternalServerError() throws Exception {
    // Arrange
    User user = new User();
    when(employeeService.createEmployee(user))
        .thenThrow(new RuntimeException("Internal server error"));

    // Act & Assert
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              employeeController.createEmployee(user, bindingResult);
            });

    // Assert the exception message
    assertEquals("Internal server error", exception.getMessage());
  }

  @Test
  public void testUpdateUser_Success() throws UserNotFoundException {
    // Arrange
    String employeeId = "ABC";
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    User updatedUser = new User();

    when(employeeService.updateEmployeeByEmployeeId(employeeId, updateUserRequest))
        .thenReturn(updatedUser);

    // Act
    ResponseEntity<?> responseEntity = employeeController.updateUser(employeeId, updateUserRequest);

    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(updatedUser, responseEntity.getBody());
  }

  @Test
  public void testUpdateUser_UserNotFoundException() throws UserNotFoundException {
    // Arrange
    String employeeId = "ABC";
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    when(employeeService.updateEmployeeByEmployeeId(employeeId, updateUserRequest))
        .thenThrow(new UserNotFoundException("User not found"));

    // Act & Assert
    UserNotFoundException exception =
        assertThrows(
            UserNotFoundException.class,
            () -> {
              employeeController.updateUser(employeeId, updateUserRequest);
            });

    // Assert the exception message
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  public void testChangeEmployeeStatus_Success() throws Exception {
    // Arrange
    String employeeId = "ABC";

    // Act & Assert
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/{employeeId}/status", employeeId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(Constants.USER_STATUS_UPDATED));
  }

  @Test
  public void testChangeEmployeeStatus_UserNotFoundException() throws Exception {
    // Arrange
    String employeeId = "ABC";
    doThrow(new UserNotFoundException("User not found"))
        .when(employeeService)
        .changeEmployeeStatus(employeeId);

    // Act & Assert
    UserNotFoundException exception =
        assertThrows(
            UserNotFoundException.class,
            () -> {
              employeeService.changeEmployeeStatus(employeeId);
            });

    // Assert the exception message
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  public void testChangeEmployeeStatus_InternalServerError() throws Exception {
    // Arrange
    String employeeId = "ABC";
    doThrow(new RuntimeException("Some internal error"))
        .when(employeeService)
        .changeEmployeeStatus(employeeId);

    // Act & Assert
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              employeeController.changeEmployeeStatus(employeeId);
            });

    // Assert the exception message
    assertEquals("Some internal error", exception.getMessage());
  }

  @Test
  public void testUpdateUserRoles_Success() throws Exception {
    // Arrange
    String employeeId = "ABC";
    UpdateUserRoleRequest newRoles = new UpdateUserRoleRequest();
    User updatedUser = new User();

    when(employeeService.updateEmployeeRolesDyEmployeeId(employeeId, newRoles))
        .thenReturn(updatedUser);

    // Act
    ResponseEntity<?> responseEntity = employeeController.updateUserRoles(employeeId, newRoles);

    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(updatedUser, responseEntity.getBody());
  }

  @Test
  public void testUpdateUserRoles_UserNotFoundException() throws Exception {
    // Arrange
    String employeeId = "ABC";
    UpdateUserRoleRequest newRoles = new UpdateUserRoleRequest();

    when(employeeService.updateEmployeeRolesDyEmployeeId(employeeId, newRoles))
        .thenThrow(new UserNotFoundException("User not found"));

    // Act & Assert
    assertThrows(
        UserNotFoundException.class,
        () -> {
          employeeController.updateUserRoles(employeeId, newRoles);
        });
  }
}
