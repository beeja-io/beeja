package com.beeja.api.financemanagementservice.Utils;

public class Constants {

  public static final String BEEJA = "BEEJA";
  public static final String APPEND_EXCEPTION_MESSAGE = "& Message = ";

  //    PERMISSIONS
  public static final String CREATE_LOAN = "CLON";
  public static final String READ_LOAN = "RLON";
  public static final String UPDATE_LOAN = "ULON";
  public static final String DELETE_LOAN = "DLON";
  public static final String STATUS_CHANGE_LOAN = "SCLON";
  public static final String CREATE_BULK_PAYSLIPS = "CBPS";
  public static final String READ_EMPLOYEE = "REMP";
  public static final String GET_ALL_LOANS = "GALON";

  public static final String PAYSLIP_ENTITY_TYPE = "payslip";

  //    Errors
  public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  public static final String ERROR_SAVING_LOAN = "Error happened while saving loan in beeja";
  public static final String ERROR_FETCHING_LOANS = "An error occurred while fetching loans";
  public static final String ERROR_FETCHING_DEVICES = "An error occurred while fetching devices";
  public static final String ERROR_SUBMITTING_LOAN_REQUEST = "Error in submitting the loan request";
  public static final String ERROR_SAVING_DEVICE_DETAILS = "Error in saving the device details";
  public static final String ERROR_UPDATING_DEVICE_DETAILS = "Error in updating the device details";
  public static final String DEVICE_NOT_FOUND = "Device not found";
  public static final String EMP_ORG_ID = ": , Employee organization ID: ";
  public static final String PRODUCT_ID_ALREADY_EXISTS = "Product ID already exists: ";
  public static final String ERROR_ORGANIZATION_MISMATCH = "Organization ID mismatch";
  public static final String ERROR_IN_AUTHORISATION = "Failed to Authorise: ";
  public static final String ERROR_RETRIEVING_LOAN = "Error In Retrieving Loan ";
  public static final String UNAUTHORISED_ACCESS_ERROR = "You have no permission to access";
  public static final String SERVICE_DOWN_ERROR = "Something went wrong in our system";

  public static final String ERROR_SAVING_HEALTH_INSURANCE = "Error while saving health insurance ";
  public static final String ERROR_UPDATING_HEALTH_INSURANCE =
      "Error while updating health insurance ";
  public static final String ERROR_DELETING_HEALTH_INSURANCE =
      "Error while deleting health insurance ";
  public static final String ERROR_FETCHING_HEALTH_INSURANCE =
      "Error while fetching health insurance ";
  public static final String ERROR_DURING_SENDING_MAIL = "Error While Sending Email ";
  public static final String ERROR_FETCHING_DEVICE_DETAILS =
      "Error While Fetching Details of Devices";
  public static final String ERROR_DELETING_DEVICE_DETAILS =
      "Error While Deleting the Device Details";

  //    VALIDATION MESSAGES
  public static final String LOAN_NOT_FOUND = "Loan Not Found ";
  public static final String NO_REQUIRED_PERMISSIONS =
      "You have no required permissions to do this operation";

  // GCP PATH GENERATOR
  public static final String expense = "expense";

  public static final String id = "id";
  public static final String PDF = ".pdf";
  public static final String I_D = "_id";
  public static final String PAYSLIP_ = "Payslip_";
  public static final String zipFile = "zipFile";
  public static final String organization = "organizations";

  // Bulk Payslips successful
  public static final String BULK_PAYSLIP_ADDED_SUCCESSFULLY =
      "Successfully Uploaded Bulk Payslips";
  public static final String SUCCESSFULLY_UPLOADED = "Successfully uploaded for ";
  public static final String LOAN_STATUS_UPDATED = "Loan Status Updated Successfully";
  public static final String READ_DEVICES = "RDEV";
  public static final String UPDATE_DEVICES = "UDEV";
  public static final String CREATE_DEVICES = "CDEV";
  public static final String DELETE_DEVICES = "DDEV";
  public static final String CREATE_HEALTH_INSURANCE = "CHIN";
  public static final String READ_HEALTH_INSURANCE = "RHIN";
  public static final String UPDATE_HEALTH_INSURANCE = "UHIN";
  public static final String DELETE_HEALTH_INSURANCE = "DHIN";
  public static final String HEALTH_INSURANCE_NOT_FOUND = "Health Insurance not found ";

  public static final String USER_NOT_FOUND = "User not found or not authorized for employeeId: ";
  public static final String FEIGN_CLIENT_ERROR_NOT_FOUND =
      "Feign Client Error: Not found user with employeeId: ";
  public static final String FEIGN_CLIENT_ERROR = "Feign Client Error: ";
  public static final String ERROR_CLOSING_PDF_DOCUMENT = "Error while closing PDF document";
  public static final String ERROR_CHANGING_LOAN_STATUS = "Error while changing loan status";
  public static final String ENCRYPTED_PDF_FILES_NOT_SUPPORTED =
      "Encrypted PDF files are not supported.";
  public static final String FOR_EMPID = " for employeeId: ";
  //  MESSAGES

  public static final String INVALID_ENTITY_TYPE = "Invalid entity type";
  public static final String THIS_METHOD_NOT_SUPPORTED = "This method is not supported.";
  public static final String LOAN_TYPE_CANNOT_BE_NULL = "Loan Type cannot be null";
  public static final String LOAN_STATUS_MUST_NOT_BE_NULL = "Loan Status cannot be null";
  public static final String MONTHLY_EMI_MUST_GREATER_THAN_ZERO =
      "Monthly EMI must be greater than 0";
  public static final String EMI_TENURE_MUST_GREATER_THAN_ZERO =
      "EMI Tenure must be greater than 0";
  public static final String EMPID_MUST_NOT_NULL = "employeeId must not be null";
  public static final String EMPNAME_MUST_NOT_NULL = "employeeName must not be null";
  public static final String AMT_GREATER_THAN_ZERO = "Amount must be greater than 0";
  public static final String ORG_ID_NOT_NULL = "organizationId must not be null";
  public static final String VALUE_MUST_GREATER_THAN_ZERO = "value must be greater than 0";

  //    AUTHORIZATION FILTER
  public static final String AZP = "azp";
  public static final String NAME = "name";
  public static final String EMAIL = "email";
  public static final String AUTHORIZATION = "authorization";
  public static final String ID = "id";
  public static final String ACCESS_TOKEN = "?access_token=";
  public static final String ACCESS_DENIED = "Access Denied";
  public static final String HTTP_ERROR = "HTTP Error: ";
  public static final String TOKEN_VALIDATION_EXCEPTIONS = "Token Validation Exception: ";
  public static final String BEEJA_EMAIL = "Beeja Email: ";
  public static final String REACHED_END_OF_LOGIC = "REACHED END of logic";
  public static final String AUTH_CLIENT_ID = "Auth ClientId: ";
  public static final String USER_VERIFIED_AND_STATUS = "User Verified and status is: ";
  public static final String USER_SUCCESSFULLY_AUTHENTICATED = "User Successfully Authenticated";
  public static final String USER_FAILED_AUTHENTICATE = "User is failed to authenticate request";
  public static final String USER_RESPONSE_FROM_ACCOUNT_SERVICE =
      "User response from account service: ";
  public static final String ERROR_AUTH_PROVIDER_SENT_INVALID_RESPONSE =
      "Error happened because, auth provider sent invalid response";
  public static final String CLIENT_ID_NOT_MATCHED_BEEJA_AUTH =
      "Error happened because, client Id not matched to Beeja Auth Provider";

  //  LOGS

  public static final String ERROR_OCCORRED_DURING_BULK_PAY_SLIPS_UPLOAD =
      "An error occurred during bulk pay slips upload";

  //  INVENTORY SERVICE

  public static final String DEVICE_SEQUENCE = "device_sequence";
  public static final String LOAN_SEQUENCE = "loan_sequence";
  public static final String SEQ = "seq";
  public static final String APPROVE = "APPROVE";
  public static final String REJECT = "REJECT";
  public static final String WITH_LOAN_NUM = " with loanNumber ";
  public static final String ERROR_HEALTH_INSURANCE_ALREADY_FOUND =
      "Health Insurance Already Found";
  public static final String ERROR_CREATING_HEALTH_INSURANCE =
      "Error while creating health insurance ";
  public static final String DOC_URL_RESOURCE_NOT_FOUND = "https://beeja-dev.techatcore.com/";

  // PAYROLL SETTINGS PERMISSIONS
  public static final String CREATE_PAYROLL = "CPAY";
  public static final String UPDATE_PAYROLL = "UPAY";
  public static final String READ_PAYROLL = "RPAY";

  public static final double EPF_EMPLOYEE_CONTRIBUTION_PERCENTAGE = 12.0;
  public static final double EPF_EMPLOYER_CONTRIBUTION_PERCENTAGE = 12.0;
  public static final double ESI_EMPLOYEE_CONTRIBUTION_PERCENTAGE = 0.75;
  public static final double ESI_EMPLOYER_CONTRIBUTION_PERCENTAGE = 3.25;
}
