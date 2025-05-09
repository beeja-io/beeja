package com.beeja.api.filemanagement.utils;

public class Constants {
  public static final String ACCESS_TOKEN_HEADER = "authorization";
  public static final String NO_ACCESS_TOKEN_ERROR = "NO AUTH_TOK_403";
  public static final String TOKEN_VERIFICATION_SUCCESSFULLY_FAILED_ERROR =
      "Your authentication has been failed, please try login again";
  public static final String TOKEN_VERIFICATION_FAILED = "Token Verification Failed ";

  //    File Entity Types;
  public static final String EMPLOYEE_ENTITY_TYPE = "employee";
  public static final String PROJECT_ENTITY_TYPE = "project";
  public static final String ORGANIZATION_ENTITY_TYPE = "organization";
  public static final String CLIENT_ENTITY_TYPE = "client";

  //    ERRORS
  public static final String MONGO_UPLOAD_FAILED = "Failed to upload file to Beeja DB";
  public static final String MONGO_FILE_DELETE_ERROR = "Failed to delete file from Beeja DB";
  public static final String SERVICE_DOWN_ERROR = "Something went wrong in our system ";
  public static final String UNAUTHORISED_ACCESS_ERROR = "You have no permission to access";
  public static final String FAILED_TO_UPDATE_BLOB = "Failed to Update the File with new Path, ";
  public static final String NO_FILE_FOUND_WITH_GIVEN_ID = "No file found with given Id : ";

  public static final String FILE_MISSING_IN_REQUEST_ERROR = "File is mandatory";
  public static final String INVALID_FILE_FORMATS = "Invalid file type.";
  public static final String SUPPORTED_FILE_TYPES = "Supported types are PDF, DOCX, DOC, PNG, JPEG";
  public static final String NOT_PERMITTED_TO_UPLOAD_OF_TYPE = "Not Permitted To Upload ";
  public static final String FAILED_TO_UPLOAD = "Failed to upload the File  ";
  public static final String EMPTY_FILE_NOT_ALLOWED = "Empty file not allowed";
  public static final String ERROR_SAVING_FILE = "Error saving file";
  public static final String FILE_NOT_FOUND_AT_PATH = "File not found at path: ";
  public static final String ERROR_READING_FILE = "Error reading file ";
  public static final String ERROR_DELETING_FILE = "Error deleting file: ";
  public static final String DOC_URL_RESOURCE_NOT_FOUND = "https://beeja-dev.techatcore.com/" ;
  public static final String BEEJA = "BEEJA";
  public static final String FILE_UPLOAD_FAILED = "Error accessing file." ;
  public static final String FILE_UPDATE_FAILED = "Error updating file with ID: ";
  public static final String ERROR_UPLOAD_UPDATE = "Error during uploadOrUpdate";
}
