package com.beeja.api.projectmanagement.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants {

  // ========== General Constants ==========
  public static final String ID = "id";
  public static final String NAME = "name";
  public static final String EMAIL = "email";
  public static final String ADDRESS = "address";
  public static final String BANK_DETAILS = "bankDetails";
  public static final String DOC_URL = "https://docs.beeja.io/";
  public static final String BEEJA = "BEEJA";
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  public static final String FILE_TYPE_PROJECT = "project";
  public static final String ENTITY_TYPE_CLIENT = "client";
  public static final String ENTITY_TYPE_INVOICE = "invoice";
  public static final String ORGANIZATION_MISSING = "Organization information is missing.";
  public static final String YEAR_MONTH = "yyyyMM";
  public static final String ENTITY_TYPE_CONTRACT = "contract";

  // ========== Success Messages ==========
  public static final String USER_SUCCESSFULLY_AUTHENTICATED = "User Successfully Authenticated";
  public static final String CLIENT_FOUND_EMAIL = "Client Found with provided email";

  // ========== Authentication & Authorization Errors ==========
  public static final String NO_REQUIRED_PERMISSIONS = "Unauthorised to Access";
  public static final String ACCESS_DENIED = "ACCESS_DENIED";
  public static final String USER_FAILED_AUTHENTICATE = "User is failed to authenticate request";

  // ========== Validation Errors ==========
  public static final String FIELD_NOT_EXIST_IN_ENTITY = "Field '%s' does not exist in entity";
  public static final String INVALID_ENUM_VALUE =
      "Invalid value '%s' for field %s. Allowed values: %s";
  public static final String INVALID_JSON_STRUCTURE =
      "Invalid structure for field '%s', expected an object.";

  // ========== Resource Errors ==========
  public static final String RESOURCE_ALREADY_FOUND = "%s with %s '%s' already exists.";
  public static final String RESOURCE_NOT_FOUND = "Static resource not found : ";

  // ========== Client Errors ==========
  public static final String CLIENT_NOT_FOUND = "Client Not Found with provided clientId";
  public static final String ERROR_SAVING_CLIENT = "Error while saving new client to database";
  public static final String ERROR_UPDATING_CLIENT = "Error while updating client in database";
  public static final String ERROR_IN_FETCHING_CLIENTS =
      "Error while fetching client from database";
  public static final String ERROR_FETCHING_CLIENTS = "%s with ID '%s' encountered a DB error.";
  public static final String ERROR_IN_GENERATING_CLIENT_ID = "Error in generating clientId";

  // ========== Project Errors ==========
  public static final String PROJECT_NOT_FOUND = "Project not found with given projectId";
  public static final String PROJECT_NOT_FOUND_WITH_CLIENT =
      "Project Not Found with provided projectId & corresponding clientId";
  public static final String ERROR_SAVING_PROJECT = "Error while saving project to database";
  public static final String ERROR_UPDATING_PROJECT = "Error while updating project in database";
  public static final String ERROR_FETCHING_PROJECT =
      "Error While Fetching project with provided projectId";
  public static final String ERROR_FETCHING_PROJECTS_WITH_CLIENT =
      "Error while fetching projects with provided clientId";
  public static final String ERROR_FETCHING_PROJECTS_WITH_ORGANIZATION =
      "Error while fetching projects with provided organizationId";
  public static final String FETCH_ERROR_FOR_PROJECT_MANAGERS = "Failed to fetch Project Manager names for IDs";

  // ========== Contract Errors ==========
  public static final String CONTRACT_NOT_FOUND = "Contract not found with given contractId";
  public static final String ERROR_SAVING_CONTRACT = "Failed to save contract";
  public static final String ERROR_UPDATING_CONTRACT = "Failed to update contract";
  public static final String FEIGN_CLIENT_ERROR = "Failed to fetch employee names: {}";

  //=========== Contract Log Messages =======

  public static final String CONTRACT_FETCHING = "Fetching contract with ID: {}";
  public static final String RESOURCES_SIZE = "Successfully fetched {} employee names";
  public static final String NO_RESOURCE_FOUND = "No resource allocations found for contract ID: {}";

  // ========== File Service Errors ==========
  public static final String ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE =
      "Error in uploading file to file service";
  public static final String ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE =
      "Error in Fetching Data from File Service";

  // ========== System Errors ==========
  public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  public static final String DB_ERROR_IN_SAVING_DETAILS = "Failed to update client details: %s";
  public static final String FILE_SIZE_EXCEED = "The logo exceeds its maximum permitted size";
  public static final String NO_PERMISSION = " You do not have permission to access this resource ";
  public static final String UNAUTHORISED_ACCESS = "NO REQUIRED PERMISSIONS";
  public static final String ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE =
      "Error in Downloading File from File Service";
  public static final String FILE_NOT_FOUND = " No file found with given Id : ";
  public static final String UNAUTHORISED_TO_READ_OTHERS_DOCUMENTS =
      "UNAUTHORISED TO READ OTHERS DOCUMENTS";
  public static final String UNAUTHORISED_TO_CREATE_DOCUMENTS =
      "UNAUTHORISED TO CREATE OTHERS DOCUMENTS";
  public static final String ERROR_IN_DELETING_FILE_FROM_FILE_SERVICE =
      "Error in Deleting File from File Service";
  public static final String SOMETHING_WENT_WRONG = "Something went wrong !!!";
  public static final String NULL_RESPONSE_FROM_FILE_CLIENT =
      "Received null response from file service for fileId: ";
  public static final String ERROR_IN_UPDATING_FILE_FROM_FILE_SERVICE =
      "Error in Updating File in File Service";
    public static final String ERROR_IN_VALIDATE_PROJECT_MANAGERS = "Error while validating project managers via account service" ;
  public static final String ERROR_IN_VALIDATE_PROJECT_RESOURCES = "Error while validating project resources via account service";
    public static final String NO_RESOURCES_FOUND = "No resources found for contracts of clientId: {}";

    // ========== Utility ==========
  public static String format(String message, Object... args) {
    return String.format(message, args);
  }

  public static final String ERROR_IN_LOGO_UPLOAD = "Error in uploading Logo";
  public static final String FILE_NOT_ALLOWED =
      "File type not allowed. Allowed types: image/jpeg, image/jpg, image/png, image/webp";
  public static final String  DELETED_SUCCESSFULLY = "Deleted Successfully logo file with id {}";

  //============ Invoice Constants =============//

  public static final String INVOICE_PDF_SUCCESS = "Invoice generation completed successfully";
  public static final String INVOICE_PDF_FAILED = "Invoice PDF generation or upload failed: {}";
  public static final String CUSTOMER_TAX_ID = "Customer Tax ID set as Tax Id: {}";
  public static final String ORG_TAX_ID = "Organization Tax ID set as Tax Id: {}";
  public static final String INVOICE_NOT_FOUND = "Invoice not found with given invoiceId";
  public static final String CONTRACT_ENDED = "Cannot generate invoice. Contract {} ended on {}";
  public static final String INVOICE_PREFIX = "INV-";
  public static final String GEN_INVOICE_ID_REMITTANCE = "Generated invoiceId: {}, remittanceReferenceNumber: {}";
  public static final String GEN_INVOICE_ID_FAILED = "Failed to generate invoice Id for contractId: {}";
  public static final String INVOICE_DELETED_SUCCESSFULLY = "Invoice deleted successfully for id: {}";
  public static final String NOT_AUTHORIZED_TO_DELETE_INVOICE = "Not authorized to delete invoice";
  public static final String LINE = "________________________________________________________________________________";
}