package com.beeja.api.accounts.service;

import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.requests.AddRoleRequest;
import java.util.List;

public interface RoleService {
  Role addRoleToOrganization(AddRoleRequest newRole) throws Exception;

  Role updateRolesOfOrganization(String roleId, AddRoleRequest updatedRole) throws Exception;

  Role deleteRolesOfOrganization(String roleId) throws Exception;

  List<Role> getAllRolesOfOrganization(Organization organization) throws Exception;
}
