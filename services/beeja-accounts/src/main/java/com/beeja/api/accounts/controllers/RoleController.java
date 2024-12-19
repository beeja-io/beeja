package com.beeja.api.accounts.controllers;

import com.beeja.api.accounts.annotations.HasPermission;
import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.requests.AddRoleRequest;
import com.beeja.api.accounts.service.RoleService;
import com.beeja.api.accounts.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents the controller for managing roles within an organization. Endpoints for
 * adding, updating, and deleting roles will be accessed based on the permissions of the logged-in
 * user and their associated organization. It contains: {@code Get All Roles Of Organization},
 * {@code Add Roles}, {@code Update Roles}, {@code Delete Roles}
 */
@RestController
@RequestMapping("/v1/roles")
public class RoleController {

  @Autowired RoleService roleService;

  @GetMapping
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<List<Role>> getAllRolesOfOrganization() throws Exception {
    return new ResponseEntity<>(
        roleService.getAllRolesOfOrganization(UserContext.getLoggedInUserOrganization()),
        HttpStatus.OK);
  }

  @PostMapping
  @HasPermission(PermissionConstants.CREATE_ROLES_OF_ORGANIZATIONS)
  public ResponseEntity<Role> addRolesToOrganization(
      @Valid @RequestBody AddRoleRequest newRole, BindingResult bindingResult) throws Exception {
    if (bindingResult.hasErrors()) {
      List<String> errorMessages =
          bindingResult.getAllErrors().stream()
              .map(ObjectError::getDefaultMessage)
              .collect(Collectors.toList());
      throw new BadRequestException(errorMessages.toString());
    }
    return ResponseEntity.status(HttpStatus.OK).body(roleService.addRoleToOrganization(newRole));
  }

  @PutMapping("/{roleId}")
  @HasPermission(PermissionConstants.UPDATE_ROLES_OF_ORGANIZATIONS)
  public ResponseEntity<Role> updateRolesOfOrganization(
      @PathVariable String roleId, @RequestBody AddRoleRequest updatedRole) throws Exception {
    return ResponseEntity.status(HttpStatus.OK)
        .body(roleService.updateRolesOfOrganization(roleId, updatedRole));
  }

  @DeleteMapping("/{roleId}")
  @HasPermission(PermissionConstants.DELETE_ROLES_OF_ORGANIZATIONS)
  public ResponseEntity<?> deleteRoleOfOrganizationById(@PathVariable String roleId)
      throws Exception {
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(roleService.deleteRolesOfOrganization(roleId));
  }
}
