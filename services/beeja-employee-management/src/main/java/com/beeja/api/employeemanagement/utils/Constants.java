package com.beeja.api.employeemanagement.utils;

public class Constants {
  public static final String BEEJA = "BEEJA";
  public static final String UNAUTHORISED_TO_READ_OTHERS_DOCUMENTS =
      "UNAUTHORISED TO READ OTHERS DOCUMENTS";
  public static final String UNAUTHORISED_TO_WRITE_OTHERS_DOCUMENTS =
      "UNAUTHORISED TO WRITE OTHERS DOCUMENTS";
  public static final String ERROR_OCCURRED_WHILE_UPLOADING = "ERROR OCCURRED WHILE UPLOADING, ";
  public static final String UNAUTHORISED_ACCESS = "NO REQUIRED PERMISSIONS";
  public static final String FILE_EMPTY = "FILE IS EMPTY. PLEASE UPLOAD A VALID FILE";
  public static final String INVALID_FILE_TYPE = "INVALID FILE TYPE. ALLOWED TYPES ARE";
  public static final String NO_RESOURCE_FOUND = "NO FILE RESOURCE FOUND WITH PROVIDED FILE ID.";
  public static final String INVALID_FILE_ID = "INVALID FILE ID: NO FILE ID PROVIDED";
  public static final String UNAUTHORISED_TO_UPDATE_PROFILE_PIC =
      "Unauthorised access to update profile pic";
  public static final String SUCCESSFULLY_UPDATED_PROFILE_PHOTO =
      "Successfully updated profile photo";

  public static final String UNAUTHORISED = "No Required Permissions. Please contact your admin";
  public static final String EMPLOYEE_NOT_FOUND = "Employee not found";
  public static final String ERROR_IN_FETCHING_DATA_FROM_ACCOUNT_SERVICE =
      "Error in Fetching Data from Accounts";
  public static final String ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE =
      "Error in Fetching Data from File Service";
  public static final String ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE =
      "Error in Downloading File from File Service";
  public static final String ERROR_IN_DELETING_FILE_FROM_FILE_SERVICE =
      "Error in Deleting File from File Service";
  public static final String ERROR_IN_UPDATING_FILE_IN_FILE_SERVICE =
      "Error in updating File at File Service";
  public static final String ERROR_IN_CONVERTING_FILE_TO_DOWNLOADABLE_FILE =
      "Error in Converting File to Downloadable File";
  public static final String EMAIL_ALREADY_REGISTERED = "Email is already registered";
  public static final String SOMETHING_WENT_WRONG = "Something went wrong!";
  public static final String ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE =
      "Error in uploading file to file service";
  public static final String FILE_LIMIT_ERROR =
      "File size exceeds the maximum allowed limit! (Max is 10MB)";

  public static final String ERROR_IN_SAVING_DETAILS = "Error occurred while saving provided data";
  public static final String IMPROPER_PAYLOAD = "Improper Payload Received";
  //  DOC URLS

  public static final String PAGE_NUMBER_INVALID = "Page number must be greater than 0.";
  public static final String PAGE_SIZE_INVALID = "Page size must be greater than 0.";
  public static final String PAGE_SIZE_EXCEEDS_LIMIT = "Page size must not exceed 100.";

  public static final String DOC_URL_RESOURCE_NOT_FOUND = "https://beeja-dev.techatcore.com/";

  public static final String INVALID_PROFILE_PIC_FORMATS =
      "Please provide valid file formats (jpeg, jpg, png)";
  public static final String NO_NUMERIC_FOUND = "No numeric part found in empId : ";
  public static final String ERROR_IN_EXTRACTING_EMP_NUMBER = "Failed to extract numeric employee number from empId: {}" ;
}
