package com.beeja.api.accounts.controllers;

import com.beeja.api.accounts.annotations.HasPermission;
import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.model.dto.EmployeeNameDTO;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.requests.AddEmployeeRequest;
import com.beeja.api.accounts.requests.ChangeEmailAndPasswordRequest;
import com.beeja.api.accounts.requests.EmployeeOrgRequest;
import com.beeja.api.accounts.requests.UpdateUserRequest;
import com.beeja.api.accounts.requests.UpdateUserRoleRequest;
import com.beeja.api.accounts.response.CreatedUserResponse;
import com.beeja.api.accounts.response.EmployeeCount;
import com.beeja.api.accounts.service.EmployeeService;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class EmployeeController {

  @Autowired private EmployeeService employeeService;

  @Autowired UserRepository userRepository;

  @GetMapping("/me")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<User> getLoggedInUser() throws Exception {
    return ResponseEntity.ok(
        employeeService.getEmployeeByEmail(
            UserContext.getLoggedInUserEmail(), UserContext.getLoggedInUserOrganization()));
  }

  @PostMapping("/names")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<List<EmployeeNameDTO>> getEmployeeNames(
      @RequestBody List<String> employeeIds) {
    try {
      List<EmployeeNameDTO> employeeNames = employeeService.getEmployeeNamesByIds(employeeIds);
      return ResponseEntity.ok(employeeNames);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  @GetMapping("/{employeeId}")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<User> getUserByEmployeeId(@PathVariable String employeeId)
      throws Exception {
    return new ResponseEntity<>(
        employeeService.getEmployeeByEmployeeId(
            employeeId, UserContext.getLoggedInUserOrganization()),
        HttpStatus.OK);
  }

  /*
   * Below End Point is used in other services while authenticating User (Used in Auth Filters)
   * */

  @GetMapping("/email/{email}")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<User> getUserByEmail(@PathVariable String email) throws Exception {
    return new ResponseEntity<>(
        employeeService.getEmployeeByEmail(email, UserContext.getLoggedInUserOrganization()),
        HttpStatus.OK);
  }

  /*
   * Below End Point is used to check whether email is already registered or  not while updating from Employee Service
   * */
  @GetMapping("/exists/{email}")
  @HasPermission(PermissionConstants.CREATE_EMPLOYEE)
  public Boolean isUserPresentWithMail(@PathVariable String email) {

    try {
      User user =
          employeeService.getEmployeeByEmail(email, UserContext.getLoggedInUserOrganization());
      return user != null;
    } catch (Exception e) {
      return false;
    }
  }

  // NOTE: Currently Employee Service is responsible to send all employees
  @GetMapping
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<List<User>> getAllEmployees() throws Exception {
    return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
  }

  @PostMapping
  @HasPermission(PermissionConstants.CREATE_EMPLOYEE)
  public ResponseEntity<CreatedUserResponse> createEmployee(
      @RequestBody @Valid AddEmployeeRequest user, BindingResult bindingResult) throws Exception {
    if (bindingResult.hasErrors()) {
      List<String> errorMessages =
          bindingResult.getAllErrors().stream()
              .map(ObjectError::getDefaultMessage)
              .collect(Collectors.toList());
      throw new BadRequestException(errorMessages.toString());
    }
    CreatedUserResponse createdUser = employeeService.createEmployee(user);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  @PutMapping("/{employeeId}")
  @HasPermission(PermissionConstants.UPDATE_EMPLOYEE)
  public ResponseEntity<User> updateUser(
      @PathVariable String employeeId, @RequestBody UpdateUserRequest updatedUser) {
    return ResponseEntity.ok(employeeService.updateEmployeeByEmployeeId(employeeId, updatedUser));
  }

  @PutMapping("/{employeeId}/status")
  @HasPermission(PermissionConstants.INACTIVE_EMPLOYEE)
  public ResponseEntity<String> changeEmployeeStatus(@PathVariable String employeeId)
      throws Exception {
    employeeService.changeEmployeeStatus(employeeId);
    return new ResponseEntity<>(Constants.USER_STATUS_UPDATED, HttpStatus.OK);
  }

  @PutMapping("/roles/{employeeId}")
  @HasPermission(PermissionConstants.UPDATE_ROLES_AND_PERMISSIONS)
  public ResponseEntity<?> updateUserRolesByEmployeeId(
      @PathVariable String employeeId, @RequestBody UpdateUserRoleRequest newRoles)
      throws Exception {
    User updatedUser = employeeService.updateEmployeeRolesDyEmployeeId(employeeId, newRoles);
    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
  }

  @GetMapping("/permissions/{permission}")
  public ResponseEntity<List<User>> getUsersByPermissionAndOrganization(
      @PathVariable String permission) throws Exception {
    List<User> users = employeeService.getUsersByPermissionAndOrganization(permission);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{employeeId}/exists/{permission}")
  @HasPermission({
    PermissionConstants.CREATE_EMPLOYEE,
    PermissionConstants.UPDATE_EMPLOYEE,
    PermissionConstants.GET_ALL_EMPLOYEES
  })
  public ResponseEntity<Boolean> isEmployeeHasPermission(
      @PathVariable String employeeId, @PathVariable String permission) throws Exception {
    return ResponseEntity.ok(employeeService.isEmployeeHasPermission(employeeId, permission));
  }

  @GetMapping("/count")
  @HasPermission((PermissionConstants.READ_EMPLOYEE))
  public ResponseEntity<EmployeeCount> getEmployeeCountByOrganizationId() throws Exception {
    EmployeeCount uniqueEmployeeCount = employeeService.getEmployeeCountByOrganization();
    return ResponseEntity.ok(uniqueEmployeeCount);
  }

  @PostMapping("/emp-ids")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<List<User>> getUsersByEmployeeIds(
      @RequestBody EmployeeOrgRequest employeeOrgRequest) throws Exception {

    List<User> users = employeeService.getUsersByEmployeeIds(employeeOrgRequest.getEmployeeIds());

    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @PutMapping("/change-email-password")
  public ResponseEntity<String> changeEmailAndPassword(
      @Valid @RequestBody ChangeEmailAndPasswordRequest changeEmailAndPasswordRequest) {
    return ResponseEntity.ok(employeeService.changeEmailAndPassword(changeEmailAndPasswordRequest));
  }

  @PostMapping("/validate-employees")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public List<String> checkEmployeesPresentOrNot(@RequestBody List<String> employeeIds){
      return employeeService.checkEmployees(employeeIds);
  }
}
