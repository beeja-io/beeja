package com.beeja.api.accounts.utils;

import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class UserContext {
  @Getter @Setter private static String loggedInUserEmail;

  @Getter @Setter private static String loggedInUserName;

  @Getter @Setter private static Organization loggedInUserOrganization;

  @Getter @Setter private static String loggedInEmployeeId;

  @Getter @Setter private static Set<Role> loggedInUserRoles;

  @Getter @Setter private static Set<String> loggedInUserPermissions;
  @Getter @Setter private static String accessToken;

  public static void setLoggedInUser(
      String email,
      String name,
      Organization organization,
      String employeeId,
      Set<Role> roles,
      Set<String> permissions,
      String token) {
    loggedInUserEmail = email;
    loggedInUserName = name;
    loggedInUserOrganization = organization;
    loggedInEmployeeId = employeeId;
    loggedInUserRoles = roles;
    loggedInUserPermissions = permissions;
    accessToken = token;
  }



}
