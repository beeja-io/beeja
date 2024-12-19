package com.beeja.api.expense.utils;

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

  public static void setLoggedInUser(
      String email,
      String name,
      String employeeId,
      Map<String, Object> organization,
      Set<String> permissions) {
    loggedInUserEmail = email;
    loggedInUserName = name;
    loggedInEmployeeId = employeeId;
    loggedInUserOrganization = organization;
    loggedInUserPermissions = permissions;
  }
}
