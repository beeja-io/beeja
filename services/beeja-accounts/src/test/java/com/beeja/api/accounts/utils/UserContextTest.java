package com.beeja.api.accounts.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class UserContextTest {

  @Test
  void testSetLoggedInUser() {
    // Arrange
    String email = "abc@example.com";
    String name = "abc";
    Organization organization = new Organization();
    String employeeId = "123456";
    Set<Role> roles = new HashSet<>();
    Role role1 = new Role("1", "ROLE_SUPER_ADMIN", "Description", Set.of("READ_EMPLOYEE"), "orgId");
    roles.add(role1);
    Set<String> permissions = new HashSet<>();
    permissions.add("READ_EMPLOYEE");

    UserContext.setLoggedInUser(
        email, name, organization, employeeId, roles, permissions, "accessToken");

    // Asserts
    assertEquals(email, UserContext.getLoggedInUserEmail());
    assertEquals(name, UserContext.getLoggedInUserName());
    assertEquals(organization, UserContext.getLoggedInUserOrganization());
    assertEquals(employeeId, UserContext.getLoggedInEmployeeId());
    assertEquals(roles, UserContext.getLoggedInUserRoles());
    assertEquals(permissions, UserContext.getLoggedInUserPermissions());
  }
}
