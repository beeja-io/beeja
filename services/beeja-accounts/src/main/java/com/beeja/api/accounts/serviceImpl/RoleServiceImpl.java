package com.beeja.api.accounts.serviceImpl;

import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import com.beeja.api.accounts.exceptions.ConflictException;
import com.beeja.api.accounts.exceptions.CustomAccessDenied;
import com.beeja.api.accounts.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.repository.OrganizationRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.requests.AddRoleRequest;
import com.beeja.api.accounts.service.RoleService;
import com.beeja.api.accounts.utils.BuildErrorMessage;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** This class represents the business logic for managing roles within an organization. */
@Service
public class RoleServiceImpl implements RoleService {

  @Autowired OrganizationRepository organizationRepository;

  @Autowired RolesRepository rolesRepository;

  @Autowired UserRepository userRepository;

  @Override
  public Role addRoleToOrganization(AddRoleRequest newRole) throws Exception {
    Organization organization = UserContext.getLoggedInUserOrganization();

    Role existedRole;
    try {
      existedRole =
          rolesRepository.findByNameAndOrganizationId(newRole.getName(), organization.getId());
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_IN_FETCHING_ROLES));
    }

    boolean roleExistsByName = existedRole != null;

    if (roleExistsByName) {
      throw new ResourceAlreadyFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_EXISTS_ERROR,
              ErrorCode.ROLE_ALREADY_FOUND,
              Constants.ROLE_ALREADY_FOUND + newRole.getName()));
    }

    Role role = new Role();
    role.setName(newRole.getName());
    if (newRole.getDescription() != null) {
      role.setDescription(newRole.getDescription());
    }
    role.setPermissions(newRole.getPermissions());
    role.setOrganizationId(organization.getId());
    try {
      return rolesRepository.save(role);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_CREATING_ERROR,
              Constants.ERROR_IN_CREATING_ROLE_TO_ORGANIZATION));
    }
  }

  @Override
  public Role updateRolesOfOrganization(String roleId, AddRoleRequest updatedRole)
      throws Exception {
    Role roleToBeUpdated;
    try {
      roleToBeUpdated =
          rolesRepository.findByIdAndOrganizationId(
              roleId, UserContext.getLoggedInUserOrganization().getId());
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_IN_FETCHING_ROLES));
    }
    if (roleToBeUpdated == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.ROLE_NOT_FOUND,
              Constants.ROLE_NOT_FOUND + roleId));
    }
    if (roleToBeUpdated.getName().startsWith("Super Admin")) {
      throw new CustomAccessDenied(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.CANT_UPDATE_DEFAULT_ROLE));
    }
    if (updatedRole.getName() != null
        && !Objects.equals(roleToBeUpdated.getName(), updatedRole.getName())) {
      if (rolesRepository.findByNameAndOrganizationId(
              updatedRole.getName(), UserContext.getLoggedInUserOrganization().getId())
          != null) {
        throw new ResourceAlreadyFoundException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_EXISTS_ERROR,
                ErrorCode.ROLE_ALREADY_FOUND,
                Constants.ROLE_ALREADY_FOUND + updatedRole.getName()));
      }
    }

    if (updatedRole.getName() != null) {
      roleToBeUpdated.setName(updatedRole.getName());
    }
    if (updatedRole.getDescription() != null) {
      roleToBeUpdated.setDescription(updatedRole.getDescription());
    }
    if (updatedRole.getPermissions() != null) {
      roleToBeUpdated.setPermissions(updatedRole.getPermissions());
    }
    try {
      return rolesRepository.save(roleToBeUpdated);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.ERROR_IN_UPDATING_ROLE_TO_ORGANIZATION));
    }
  }

  @Override
  public Role deleteRolesOfOrganization(String roleId) throws Exception {
    Role roleToBeDeleted =
        rolesRepository.findByIdAndOrganizationId(
            roleId, UserContext.getLoggedInUserOrganization().getId());
    if (roleToBeDeleted != null) {
      if (roleToBeDeleted.getName().startsWith("Super Admin")) {
        throw new CustomAccessDenied(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.AUTHORIZATION_ERROR,
                ErrorCode.CANNOT_SAVE_CHANGES,
                Constants.CANT_DELETE_DEFAULT_ROLE));
      }
      List<User> allUsersWithRole = userRepository.findByRoles(roleToBeDeleted);

      if (!allUsersWithRole.isEmpty()) {
        throw new ConflictException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.CONFLICT_ERROR,
                ErrorCode.RESOURCE_IN_USE,
                Constants.ERROR_IN_DELETING_ROLE_AS_IT_IN_USE + allUsersWithRole.size()));
      }
      rolesRepository.delete(roleToBeDeleted);
      return roleToBeDeleted;
    } else {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.ROLE_NOT_FOUND,
              Constants.ROLE_NOT_FOUND + roleId));
    }
  }

  @Override
  public List<Role> getAllRolesOfOrganization(Organization organization) throws Exception {
    try {
      return rolesRepository.findByOrganizationId(organization.getId());
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_IN_FETCHING_ROLES));
    }
  }
}
