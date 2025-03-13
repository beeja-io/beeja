package com.beeja.api.accounts.utils;

public class Constants {
  public static final String BEEJA = "BEEJA";
  public static final String USE_BUSINESS_EMAIL = "Please use a business email";
  public static final String ORGANIZATION_ALREADY_EXIST =
      "Organization already registered at Beeja";
  public static final String REGISTER_BEFORE_LOGIN = "Please Register with Beeja to authenticate";
  public static final String LOGIN_SUCCESSFULL = "Login Successfull";
  public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  public static final String TOKEN_VERIFICATION_FAILED = "Token Validation Failed";
  public static final String HTTP_ERROR = "Http Error";
  public static final String NOT_AUTHORISED = "Not Authorised - ACC";
  public static final String CANT_INACTIVE_SELF = "/ Can't Inactive Yourself";
  public static final String CANT_UPDATE_ROLES_SELF = "/ Can't Update Role Yourself";
  public static final String ACCESS_DENIED = "Access Denied - ACC";
  public static final String COOKIE_ACCESS_TOKEN = "authorization";
  public static final String UNAUTHORISED_ACCESS = "Unauthorized access";
  public static final String ERROR_IN_CHECKING_PERMISSION = "Error in Checking Permission";
  public static final String ERROR_IN_ASSIGNING_ROLE = "Error in Assigning Role - Employee";
  public static final String CANT_DELETE_SELF_ORGANIZATION = "Cannot delete your own organization";
  public static final String ERROR_CREATING_EMPLOYEE_FOR_ORG =
      "Error in Creating Employee for Organization";
  public static final String ERROR_IN_CREATE_ORGANIZATION = "Error in Creating Organization";
  public static final String ERROR_IN_UPDATING_ORGANIZATION = "Error in Updating Organization";
  public static final String ERROR_IN_FETCHING_ORGANIZATION_LOGO =
      "Error in Fetching Organization Logo";
  public static final String ERROR_IN_CREATING_USER = "Error in Creating User";

  //    Errors
  public static final String USER_NOT_FOUND = "User Not Found ";
  public static final String DEPARTMENT_ALREADY_EXISTS = "Department Already Exists";
  public static final String JOB_TITLE_ALREADY_EXISTS = "Job Title Already Exists";
  public static final String EMPLOYEE_TYPE_ALREADY_EXISTS = "Employee Type Already Exists";
  public static final String EXPENSE_CATEGORY_ALREADY_EXISTS = "Expense Category Type Already Exists";
  public static final String USER_ALREADY_FOUND = "User Already Found ";
  public static final String CANNOT_CREATE_ORGANIZATION_USER =
      "Cannot Create Organization User or Admin as it is already registered - please use different email";
  public static final String EMPLOYEE_WITH_ID_ALREADY_FOUND =
      "Employee With Given Id Is Already Found ";
  public static final String ERROR_RETRIEVING_USER = "Error In Retrieving User ";
  public static final String ERROR_RETRIEVING_USER_FOR_ORGANIZATION =
      "Error In Retrieving User, in order to create new user ";
  public static final String USER_STATUS_UPDATED = "User Status Successfully Updated, ";
  public static final String USER_UPDATE_ERROR = "Error Encountered in Updating User, ";
  public static final String USER_CREATE_ERROR = "Error Encountered in Creating User, ";
  public static final String EMPLOYEE_FEIGN_CLIENT_ERROR = "Error in Employee EmployeeFeignClient";
  public static final String ERROR_IN_FETCHING_EMPLOYEE_COUNT = "Error in Fetching Employee Count";
  public static final String ERROR_IN_UPDATING_ORG_LOGO = "Error in Updating Organization Logo";

  public static final String NO_REQUIRED_PERMISSIONS =
      "You have no required permissions to do this operation";

  //    ORGANIZATIONS
  public static final String ERROR_IN_CREATING_ORGANIZATION_ORG_EXISTS =
      "Organization with the same email domain already exists";
  public static final String ERROR_IN_INSTANTIATING_FEATURE_TOGGLES =
      "Organization Not Created, Feature Toggle Error";
  public static final String ERROR_IN_FETCHING_ORGANIZATIONS =
      "Error Encountered in Fetching Organizations";
  public static final String ERROR_IN_FETCHING_EMPLOYEES_OF_ORG =
      "Error Encountered in fetching employees of organization";

  public static final String ERROR_NO_ORGANIZATION_FOUND_WITH_PROVIDED_ID =
      "No Organization Found with provided Id";

  //    VALIDATION MESSAGES
  //    ORGANIZATION
  public static final String VALIDATION_ORGANIZATION_NAME_IS_REQUIRED =
      "Organization Name is Required";
  public static final String VALIDATION_ORGANIZATION_EMAIL_IS_REQUIRED =
      "Organization Email is Required";
  public static final String VALIDATION_ORGANIZATION_EMAIL_IS_INVALID_FORMAT =
      "Organization Email is not in proper format";
  public static final String ORGANIZATION_DALETE_FEIGN_ERROR =
      "Organization Delete Error Occured From Feign Client - Emp.";

  public static final String VALIDATION_ORGANIZATION_OWNER_CONTACT_EMAIL_IS_REQUIRED =
      "Organization Owner Contact Mail is Required";
  public static final String VALIDATION_ORGANIZATION_OWNER_CONTACT_EMAIL_IS_INVALID_FORMAT =
      "Organization Owner Contact Mail is Invalid Format";
  public static final String VALIDATION_ORGANIZATION_WEBSITE_REQUIRED =
      "Organization Website is Required";
  public static final String VALIDATION_ORGANIZATION_WEBSITE_INVALID_FORMAT =
      "Organization Website is not in proper format";

  public static final String ERROR_IN_ADDING_ROLE_TO_ORGANIZATION =
      "Error Occurred in Adding Role to Organization, ";
  public static final String ERROR_IN_UPDATING_ROLE_TO_ORGANIZATION =
      "Error Occurred in Updating Role to Organization, ";
  public static final String ERROR_IN_DELETING_ROLE_TO_ORGANIZATION =
      "Error Occurred in Deleting Role to Organization, ";
  public static final String ROLE_NOT_FOUND = "Role Not Found ";
  public static final String ROLE_ALREADY_FOUND = "Role Already Found ";
  public static final String ERROR_IN_DELETING_ROLE_AS_IT_IN_USE =
      "Error Occurred in Deleting Role because it is in use and assigned users is/are: ";

  public static final String FIELD_NOT_EXIST_ORGANIZATION_ENTITY =
      "Field does not exist in Organization class: ";
  public static final String ERROR_UPDATING_FIELD = "Error Updating Field: ";
  public static final String ERROR_PARSING_JSON = "Error Parsing JSON: ";
  public static final String ORGANIZATION_LOGO_NOT_FOUND = "Organization Logo Not Found";
  public static final String RESOURCE_NOT_FOUND = "Resource Not Found";
  public static final String ERROR_IN_FETCHING_ROLES = "Error in Fetching Roles";
  public static final String CANT_DELETE_DEFAULT_ROLE = "Cannot delete default role";
  public static final String CANT_UPDATE_DEFAULT_ROLE = "Cannot Update default role ";
  public static final String UNABLE_TO_FETCH_DETAILS_FROM_DATABASE =
      "Unable to fetch details from Database ";
  public static final String RESOURCE_UPDATING_ERROR_FEATURE_TOGGLE =
      "Resource Updating Error - Features, it will happen very rarely";
  public static final String ERROR_IN_CREATING_ROLE_TO_ORGANIZATION =
      "Error in Creating Role to Organization";

  //  DOC URLS
  public static final String DOC_URL_RESOURCE_NOT_FOUND = "https://beeja-dev.techatcore.com/";
  public static final String CAME_TO_PUBLIC_ROUTE = "Request came to public endpoint: ";
  public static final String NEW_USER_AND_ORG_CREATION_INITIATED =
      "Initiated new user and organization creation, request: ";
  public static final String USER_NOT_FOUND_CREATING_ORG =
      "User not found and about to create organization";
  public static final String ORGANIZATION_CREATED = "Organization Created ";
  public static final String ABOUT_TO_CREATE_ROLE =
      "About to create super admin role for organization ";
  public static final String ROLE_CREATED = "Default role created for organization ";
  public static final String ERROR_OCCURRED_WHILE_CREATING_DEFAULT_ROLE =
      "Error occurred while creating default role ";
  public static final String ABOUT_TO_CREATE_FEATURE_TOGGLE_FOR_ORG =
      "About to create feature toggle for org: ";
  public static final String FEATURE_TOGGLE_CREATED = "Feature toggle created ";
  public static final String ERROR_OCCURRED_WHILE_CREATING_FEATURE_TOGGLE =
      "Error occurred while creating feature toggle ";
  public static final String CREATED_NEW_USER_FOR_ORGANIZATION =
      "Created new user for organization ";
  public static final String NEW_USER_FOR_ORG_REQUEST_ABOUT_TO_REACH_EMP_SERVICE =
      "New User request for organization request is about to reach Employee Service ";
  public static final String NEW_USER_FOR_ORG_ERROR_IN_EMP_SERVICE =
      "Error occurred while creating employee in employee service ";
  public static final String NEW_USER_SUCCESSFULLY_CREATED_IN_EMP_SERVICE =
      "New User is created successfully in Employee Service ";
  public static final String ORGANIZATION_AND_USER_CREATED =
      "New Organization and user is created ";
  public static final String NOTIFICATION_REQUEST_SENT_FOR_NEW_ORG =
      "Notification request sent to notification service to send email to new organization ";
  public static final String TERMS_NOT_ACCEPTED = "Terms Not Accepted";
  public static final String USER_IS_ABOUT_TO_VERIFY =
      "User requested to verify from his/her email ";
  public static final String ERROR_NO_EMPLOYMENT_TYPES_FOUND =
      "No employment types found for the organization";
  public static final String ERROR_INVALID_EMPLOYMENT_TYPE_CODE = "INVALID_EMPLOYMENT_TYPE_CODE";
  public static final String ERROR_ORGANIZATION_SETTINGS_NOT_FOUND =
      "Organization settings not found for organization ID";
  public static final String ERROR_IN_SAVING_DETAILS = "Error in saving details";
  public static final String COUNTRY_DELETED_SUCCESSFULLY = "Country deleted successfully.";
  public static final String STATES_ADDED_SUCCESSFULLY = "States added successfully.";
  public static final String COUNTRY_NOT_AVAILABLE = "country not available in this organization";
  public static final String ROLES_ASYNC_CALL_STARTED =
      "ASYNC call Started for creating roles for org: ";
  public static final String ROLES_ASYNC_CALL_ENDED =
      "ASYNC call Ended for creating roles for org: ";

  public static final String KEY_NOT_FOUND = "Key not found";
  public static final String ORGANIZATION_NOT_FOUND = "Organization not found";
  public static final String PATTERN_MUST_NOT_NULL = "Pattern type must not be null.";
  public static final String PATTERN_LENGTH_MUST_BE_GREATER_THAN_ONE =
      "Pattern length must be at least 1.";
  public static final String PREFIX_MUST_NOT_NULL = "Prefix must not be blank.";
  public static final String PREFIX_MUST_NOT_EXCEED_TEN_CHARS =
      "Prefix must not exceed 10 characters.";
  public static final String INITIAL_SEQUENCE_MUST_GREATER_THAN_ZERO =
      "Initial sequence must be 0 or greater.";
  public static final String STATUS_IS_REQUIRED = "Status is required";
  public static final String ASYNC_CALL_STARTED_FOR_GENERATING_PATTERNS =
      "Async Call Started for Generating Patterns";
  public static final String ASYNC_CALL_ENDED_FOR_GENERATING_PATTERNS =
      "Async Call Ended for Generating Patterns";
  public static final String NO_PATTERN_FOUND_WITH_PROVIDED_ID = "No Pattern Type with provided ID";
  public static final String SAME_ID_PATTERN_ALREADY_REGISTERED = "Already Same Pattern Registered";
  public static final String NO_EMPLOYEE_TYPES_DEFINED =
      "No employee types defined for the organization.";
  public static final String INVALID_EMPLOYMENT_TYPE = "Invalid employment type: ";
  public static final String CURRENT_PASSWORD_IS_INCORRECT = "Current password is incorrect.";
  public static final String NEW_PASSWORD_MUST_MATCH_CONFIRMATION_PASSWORD =
      "New Password Must Match Confirmation Password";
  public static final String UPDATED = "Updated";

  public static final String GENERATING_DEFAULT_VALUES = "Generating Values for organization :  {} ";
  public static final String ERROR_GENERATING_DEFAULT_VALUES = "Error in generating {} values for organization : {} ,";
  public static final String SUCCESSFULLY_GENERATED_DEFAULTS = "Successfully generated values for organization : {} ,";
}
