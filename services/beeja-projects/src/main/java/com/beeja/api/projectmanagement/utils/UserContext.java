package com.beeja.api.projectmanagement.utils;

import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Utility class for storing information about the currently logged-in user.
 */
public class UserContext {
  @Getter @Setter private static String loggedInUserEmail;

  @Getter @Setter private static String loggedInUserName;

  @Getter @Setter private static String loggedInEmployeeId;

  @Getter @Setter private static Map<String, Object> loggedInUserOrganization;

  @Getter @Setter private static Set<String> loggedInUserPermissions;

  @Getter @Setter private static String loggedInUserToken;

  /**
   * Sets the details of the currently logged-in user.
   * @param email the email of the logged-in user
   * @param name the name of the logged-in user
   * @param employeeId the employee ID of the logged-in user
   * @param organization a map containing the organization details of the logged-in user
   * @param permissions a set of permissions associated with the logged-in user
   * @param token the authentication token of the logged-in user
   */
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
    loggedInUserToken = token
    ;
  }
}
