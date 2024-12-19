package com.beeja.api.employeemanagement.utils;

import com.beeja.api.employeemanagement.model.clients.accounts.OrganizationDTO;
import com.beeja.api.employeemanagement.model.clients.accounts.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class UserContext {
  @Getter @Setter private static String loggedInUserEmail;

  @Getter @Setter private static String loggedInUserName;

  @Getter @Setter private static String loggedInEmployeeId;

  @Getter @Setter private static OrganizationDTO loggedInUserOrganization;

  @Getter @Setter private static Set<String> loggedInUserPermissions;
  @Getter @Setter private static UserDTO loggedInUserDTO;

  @Getter @Setter private static String accessToken;

  public static void setLoggedInUser(
      String email,
      String name,
      String employeeId,
      OrganizationDTO organization,
      Set<String> permissions,
      UserDTO userDTO,
      String token) {
    loggedInUserEmail = email;
    loggedInUserName = name;
    loggedInEmployeeId = employeeId;
    loggedInUserOrganization = organization;
    loggedInUserPermissions = permissions;
    loggedInUserDTO = userDTO;
    accessToken = token;
  }
}
