package com.beeja.api.projectmanagement.utils;

public class Constants {
  public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  public static final String NO_REQUIRED_PERMISSIONS = "Unauthorised to Access";

  public static final String USER_SUCCESSFULLY_AUTHENTICATED = "User Successfully Authenticated";
  public static final String USER_FAILED_AUTHENTICATE = "User is failed to authenticate request";
  public static final String ACCESS_DENIED = "ACCESS_DENIED";
  public static final String ID = "id";
  public static final String NAME = "name";
  public static final String EMAIL = "email";
  public static final String RESOURCE_ALREADY_FOUND = "%s with %s '%s' already exists.";
  public static final String RESOURCE_NOT_FOUND ="%s with %s '%s' doesn't exists.";
  public static final String ERROR_FETCHING_CLIENTS ="%s with %s '%s' DB ERROR" ;
  public static final String FIELD_NOT_EXIST_IN_ENTITY = "Field '%s' does not exist in entity";
  public static final String DB_ERROR_IN_SAVING_DETAILS = "Failed to save project details: ";


  public static String format(String message, Object... args) {
    return String.format(message, args);
  }

}
