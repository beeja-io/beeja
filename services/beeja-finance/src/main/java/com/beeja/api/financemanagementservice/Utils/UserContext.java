package com.beeja.api.financemanagementservice.Utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

public class UserContext {
  @Getter @Setter private static String loggedInUserEmail;

  @Getter @Setter private static String loggedInUserName;

  @Getter @Setter private static String loggedInEmployeeId;

  @Getter @Setter private static Map<String, Object> loggedInUserOrganization;

  @Getter @Setter private static Set<String> loggedInUserPermissions;

  @Getter @Setter private static String accessToken;

  public static void setLoggedInUser(
      String email,
      String name,
      String employeeId,
      Map<String, Object> organization,
      Set<String> permissions,
      String token) {
    loggedInUserEmail = email;
    loggedInUserName = name;
    loggedInEmployeeId = employeeId;
    loggedInUserOrganization = organization;
    loggedInUserPermissions = permissions;
    accessToken = token;
  }
}
