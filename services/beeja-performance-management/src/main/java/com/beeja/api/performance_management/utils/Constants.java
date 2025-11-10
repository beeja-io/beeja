package com.beeja.api.performance_management.utils;

public class Constants {
  public static final String NAME = "name";
  public static final String EMAIL = "email";
  public static final String AUTHORIZATION = "authorization";
  public static final String ID = "id";

  public static final String USER_FAILED_AUTHENTICATE = "User failed to authenticate";
  public static final String ACCESS_DENIED = "Access denied";
  public static final String DOC_URL_RESOURCE_NOT_FOUND =
      "https://docs.beeja.io/docs/resource-not-found";
  public static final String BEEJA = "BEEJA";
  public static final String NO_REQUIRED_PERMISSIONS =
      "You do not have the required permissions to access this resource";

  // ============= LOG ERRORS For Questionnaires =============/
  public static final String ERROR_UPDATING_QUESTIONNAIRE = "Error occured while updating the questionnaire";
  public static final String ERROR_SAVING_QUESTIONNAIRE = "Errror occured while saving the questionnaire";
  public static final String ERROR_INVALID_QUESTIONNAIRE = "Questionnaire not found with id: ";
  public static final String ERROR_INVALID_DEPARTMENT = "Invalid department:";
  public static final String ERROR_QUESTIONNAIRE_NOT_FOUND  = "Questionnaire not found with id: ";
  public static final String ERROR_QUESTION_LIST_EMPTY = "Question list cannot be empty" ;

  // ============ LOG INFO For Questionnaires ===============/
  public static final String INFO_CREATING_QUESTIONNAIRE = "Creating new questionnaire: {}";
  public static final String INFO_QUESTIONNAIRE_CREATED = "Questionnaire created successfully with ID: {}";
  public static final String INFO_FETCHING_ALL_QUESTIONNAIRES = "Fetching all questionnaires";;
  public static final String INFO_FETCHING_QUESTIONNAIRE_BY_ID = "Fetching questionnaire with ID: {}";
  public static final String INFO_UPDATING_QUESTIONNAIRE_BY_ID = "Updating questionnaire with ID: {}";
  public static final String INFO_UPDATING_QUESTIONS_FOR_QUESTIONNAIRE = "Updating questions for questionnaire ID: {}";
  public static final String INFO_QUESTIONNAIRE_UPDATED_SUCCESS = "Questionnaire updated successfully: {}";
  public static final String INFO_DELETING_QUESTIONNAIRE_BY_ID = "Deleting questionnaire with ID: {}";
  public static final String INFO_QUESTIONNAIRE_DELETED = "Deleted questionnaire with ID: {}";

  //================ LOG ERRORS for Evaluation Cycle ==============/
  public static final String ERROR_CANNOT_UPDATE_PUBLISHED_CYCLE = "Cannot update a published evaluation cycle";
  public static final String ERROR_EVALUATION_CYCLE_NOT_FOUND = "Evaluation cycle not found with id: ";
  public static final String ERROR_STATUS_CANNOT_BE_NULL = "Status cannot be null";
  public static final String ERROR_FAILED_UPDATE_CYCLE_STATUS = "Failed to update cycle status for id: {}";
  public static final String ERROR_UPDATING_EVALUATION_CYCLE_STATUS = "Error occurred while updating evaluation cycle status";
  public static final String ERROR_NO_ACTIVE_EVALUATION_CYCLE = "No active evaluation cycle found";
  public static final String ERROR_SAVING_EVALUATION_CYCLE = "Error occurred while saving the evaluation cycle.";
  public static final String ERROR_UPDATING_EVALUATION_CYCLE = "Error occured while updating the evaluation cycle.";
  public static final String ERROR_QUESTIONNAIRE_NOT_FOUND_FOR_CYCLE = "Questionnaire not found for cycle: {}";
  public static final String ERROR_NO_CYCLES_FOUND = "No evaluation cycles found with status: {}";

  // =============== LOG INFO For Evaluation Cycle ===============/
  public static final String INFO_CREATING_EVALUATION_CYCLE = "Creating new evaluation cycle: {}";
  public static final String INFO_EVALUATION_CYCLE_CREATED = "Created evaluation cycle successfully: ";
  public static final String INFO_FETCHING_ALL_CYCLES = "Fetching all cycles";
  public static final String INFO_FETCHING_EVALUATIONCYCLE_BY_ID = "Fetching evaluation cycle by id: {}";
  public static final String INFO_FETCHING_EVALUATIONCYCLE_WITH_QUESTIONNAIRE_BY_ID = "Fetching evaluation cycle with questionnaire by id: {}";
  public static final String INFO_UPDATING_EVALUATION_CYCLE = "Updating evaluation cycle with id: {}";
  public static final String INFO_EVALUATION_CYCLE_UPDATED_SUCCESSFULLY = "Updated evaluation cycle successfully: {}";
  public static final String INFO_UPDATING_EVALUATION_CYCLE_STATUS = "Updating status for evaluation cycle: {}";
  public static final String INFO_UPDATED_CYCLE_STATUS = "Successfully updated evaluation cycle status to {} for id: {}";
  public static final String INFO_DELETED_EVALUATION_CYCLE = "Deleted evaluation cycle: {}";
  public static final String INFO_FETCH_CYCLES_BY_STATUS = "Fetching evaluation cycles with status: {}";
  public static final String INFO_FULL_UPDATE_START = "Starting full update for EvaluationCycle with ID: {}";
  public static final String INFO_EXISTING_CYCLE_FETCHED = "Fetched existing cycle: {} (status: {})";
  public static final String INFO_UPDATING_CYCLE_FIELDS = "Updating cycle fields for cycle ID: {}";
  public static final String INFO_UPDATING_QUESTIONS = "Updating questions for cycle ID: {}";
  public static final String INFO_SELF_EVAL_DEADLINE_ERROR = "Self-evaluation deadline must be before or equal to feedback deadline";
  public static final String INFO_FULL_UPDATE_COMPLETED = "Full update completed for EvaluationCycle with ID: {}";
  public static final String DUPLICATE_QUESTIONNAIRE_WITH_SAME_QUESTIONS = "Duplicate questionnaire with same questions already exists";
  public static final String MISSING_CYCLE_DATE_FIELDS = "All cycle date fields must be provided";
  public static final String SELF_EVAL_DEADLINE_BEFORE_START = "Self-evaluation deadline must be on or after the start date";
  public static final String FEEDBACK_DEADLINE_BEFORE_END_DATE = "Feedback deadline must be on or after the end date";
  public static final String START_DATE_AFTER_END_DATE = "Start date must be on or before the end date";
  public static final String INFO_CREATING_CYCLE_WITH_QUESTIONS = "Creating evaluation cycle with questions: {}";
  public static final String ERROR_CREATING_QUESTIONNAIRE = "Failed to create questionnaire";

  // ========= LOG For Feedback Providers =========/
  public static final String FEEDBACK_REQUEST_NULL = "FeedbackFormRequest is null";
  public static final String ERROR_ASSIGNING_FEEDBACK_PROVIDER = "Failed to assigning feedback providers for employeeId={}";
  public static final String INFO_FEEDBACK_FORM_PROCESSED = "Feedback form processed for employeeId = {}";
  public static final String WARN_BAD_REQUEST_EXCEPTION = "BadRequestException: {}";
  public static final String INFO_UPDATING_FEEDBACK_PROVIDERS = "Updating feedback providers for employeeId={}, orgId={}";
  public static final String EMPLOYEE_ID_NOT_EMPTY = "Employee ID must not be null or empty";
  public static final String ASSIGNED_REVIEWERS_NOT_EMPTY = "Assigned reviewers list must not be empty";
  public static final String PROVIDERS_NOT_FOUND = "Feedback Providers not found for employeeId=";
  public static final String CYCLE_ID_MISMATCH = "Cycle ID mismatch for employeeId=";
  public static final String QUESTIONNAIRE_ID_MISMATCH = "Questionnaire ID mismatch for employeeId=";
  public static final String INVALID_REVIEWER_DETAIL_RETURNED = "Invalid reviewer detail returned: {}";
  public static final String ERROR_FETCHING_ASSIGNED_EMPLOYEES = "Error while fetching assigned employees for reviewer: {}";
  public static final String ERROR_FETCHING_CYCLE_NAME = "Unable to fetch cycle name for cycleId {}: {}";
  public static final String ERROR_FETCHING_FORMS = "Error while fetching forms for employeeId {} reviewerId {}: {}";
    // ========= LOG For Feedback Receivers =========//
    public static final String FEEDBACK_RECEIVERS_NULL = "Adding FeedbackReceivers ReceiverRequest is null";
    public static final String DUPLICATE_EMPLOYEE_ID = "Duplicate Employee ID found in request";
    public static final String RECEIVER_LIST_CANNOT_BE_EMPTY = "Receiver list cannot be empty";
    public static final String RECEIVER_DETAIL_NOT_EMPTY = "Employee ID, Full Name, and Department are required for each receiver";
    public static final String CYCLE_ID_QUESTIONNAIRE_ID_EMPTY = "Cycle ID or Questionnaire ID is empty. cycleId={}, questionnaireId={}";
    public static final String RECEIVER_CYCLE_QUESTIONNAIRE_EMPTY = "Receiver list cannot be empty for cycleId={}, questionnaireId={}";
    public static final String EMPTY_EMPLOYEE_ID = "Skipping receiver with empty employeeId";
    public static final String REMOVE_RECEIVER = "Removed {} old receivers not present in incoming list";
    public static final String NO_RECEIVER_FOUND = "No receivers found for organizationId={}, cycleId={}, questionnaireId={}";
    public static final String NOT_ASSIGNED = "No reviewers assigned for employeeId={}, providerId={}";


    //===============LOG For Feedback Responses ==============/
    public static final String ORG_ID_NOT_FOUND_IN_CONTEXT = "Organization ID not found in context";
    public static final String REQUEST_BODY_CANNOT_BE_NULL = "Request body cannot be null";
    public static final String NO_RESPONSES_FOUND_FOR_EMPLOYEE = "No responses found for employeeId=";
    public static final String CYCLE_NOT_FOUND_FOR_ID = "Cycle not found for id=";
    public static final String EVALUATION_CYCLE_NOT_FOUND_WITH_ID = "Evaluation cycle not found with id=";
    public static final String FEEDBACK_SUBMITTED_SUCCESSFULLY = "Feedback submitted successfully for employeeId={}, formId={}";
    public static final String REVIEWER_MARKED_COMPLETED = "Reviewer {} marked as COMPLETED for employee {}";
    public static final String REVIEWER_NOT_FOUND_IN_FEEDBACK_PROVIDER = "Reviewer {} not found in feedback provider for employee {}";
    public static final String ERROR_SAVING_FEEDBACK_RESPONSE = "Error saving feedback response: {}";
    public static final String ERROR_SAVING_FEEDBACK_RESPONSE_SIMPLE = "Error saving feedback response";
    public static final String NO_FEEDBACK_RESPONSES_FOUND = "No feedback responses found for cycleId: {}";
    public static final String CYCLE_NOT_FOUND = "Cycle not found for ID: {}";
    public static final String MULTIPLE_FEEDBACK_PROVIDERS_FOUND = "Multiple FeedbackProvider entries found for employeeId={}, cycleId={}, orgId={}";
    public static final String REVIEWER_MARKED_COMPLETED_DUPLICATE = "Reviewer {} marked COMPLETED in duplicate provider {}";

    //============== Fegin client Errors ===============/
    public static final String ACCOUNT_CLIENT_ERROR = "Failed to fetch employee details from Account service: {}";
    public static final String EMPLOYEE_CLIENT_ERROR = "Failed to fetch department details from EmployeeFeignClient for reviewerId: {}";
    public static final String INVALID_ERROR = "Exception in getEmployeesAssignedToReviewer: ";

    // ========= LOG For Rating Computation ========= //
    public static final String COMPUTING_RATING = "Computing rating for employeeId={}, cycleId={}, orgId={}";
    public static final String NO_SELF_EVALUATIONS_FOUND_FOR_EMPLOYEE = "No self-evaluations found for employeeId={}";
    public static final String FINAL_RATING_COMPUTED_AND_SAVED = "Final rating computed and saved for employeeId={}, cycleId={}";
    public static final String FETCHING_RATINGS = "Fetching ratings for employeeId={}, cycleId={}, orgId={}";
    public static final String NO_RATINGS_FOUND_FOR_EMPLOYEE = "No ratings found for employeeId={}, cycleId={}";
    public static final String PUBLISHING_FINAL_RATING = "Publishing final rating with id={}";
    public static final String FINAL_RATING_NOT_FOUND = "Final rating not found with ID={}";
    public static final String FINAL_RATING_PUBLISHED_SUCCESSFULLY = "Final rating published successfully for id={}";

    // ========= LOG For Self-Evaluation Validation ========= //
    public static final String INVALID_SELF_EVALUATION_MISSING_EMPLOYEE_ID = "Invalid self-evaluation: missing employee ID";
    public static final String INVALID_SELF_EVALUATION_NO_RESPONSES = "Invalid self-evaluation: no responses provided for employee {}";
    public static final String SELF_EVALUATION_ALREADY_SUBMITTED = "Self-evaluation already submitted by employee {}";
    public static final String SELF_EVALUATION_SUBMITTED_SUCCESSFULLY = "Self-evaluation submitted successfully for employee {}";
    public static final String NULL_OR_EMPTY_EMPLOYEE_ID_WHILE_FETCHING_SELF_EVAL = "Attempted to fetch self-evaluations with null or empty employee ID";
    public static final String FETCHED_SELF_EVALUATIONS_FOR_EMPLOYEE = "Fetched {} self-evaluation(s) for employee {}";
}