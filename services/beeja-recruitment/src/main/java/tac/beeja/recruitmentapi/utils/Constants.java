package tac.beeja.recruitmentapi.utils;

public class Constants {

  public static final String BEEJA = "BEEJA";
  public static final String NO_REQUIRED_PERMISSIONS = "No Required Permissions to do this action";
  public static final String RESUME_FILE_ENTITY = "resume";

  //    PERMISSIONS:
  public static final String CREATE_APPLICANT = "CAPT";
  public static final String GET_APPLICANTS = "GAAPT";
  public static final String GET_ENTIRE_APPLICANTS = "GENAPT";
  public static final String READ_APPLICANT_RESUME = "RRSM";
  public static final String UPDATE_ENTIRE_APPLICANT = "UENTAP";
  public static final String UPDATE_APPLICANT = "UAPL";
  public static final String TAKE_INTERVIEW = "TINT";
  public static final String DELETE_INTERVIEW = "DINT";

  public static final String ACCESS_REFFERRAL = "ACREF";

  //    EXCEPTIONS:
  public static final String ERROR_IN_RESUME_UPLOAD = "Error in uploading Resume";
  public static final String ERROR_IN_CREATING_APPLICANT = "Error in creating applicant, ";
  public static final String ERROR_IN_GETTING_LIST_OF_APPLICANTS = "Error in getting applicants, ";
  public static final String ERROR_IN_UPDATING_APPLICANTS = "Error in updating applicants, ";
  public static final String NO_APPLICANT_FOUND_WITH_GIVEN_ID = "No Applicant found with given ID : ";
  public static final String UNAUTHORISED_ACCESS_TO_DOWNLOAD_RESUME = "No Access for resumes, ";
  public static final String DOC_URL_RESOURCE_NOT_FOUND = "https://beeja-dev.techatcore.com/" ;
  public static final String ERROR_IN_SAVING_UPDATED_APPLICANT = "Error in Saving Updated Applicant";
  public static final String INTERVIEWER_ALREADY_ASSIGNED = "Interviewer already assigned to this applicant" ;
  public static final String INTERVIEWER_PERMISSION_FETCH_FAILED = "Error in fetching interviewer permission, please check interviewer Employee ID";
  public static final String INTERVIEWER_PERMISSION_DENIED = "Interviewer does not have the required permission";
  public static final String INTERVIEWER_ASSIGNMENT_FAILED = "Error in assigning interviewer";
  public static final String ERROR_IN_GETTING_APPLICANT = "Error in getting applicant by ID :";
  public static final String NO_INTERVIEWER_ASSIGNED = "No interviewer assigned to applicant with ID: ";
  public static final String INVALID_APPLICANT_STATUS = "Invalid applicant status provided: " ;
  public static final String REFERRAL_CREATION_FAILED = "Error while creating referral for email:";
  public static final String GET_REFERRALS_FAILED = "Error while fetching referrals for employee:";
  public static final String UNAUTHORISED_RESUME_ACCESS = "Unauthorized access attempt to download file with ID:" ;
  public static final String GET_FILE_METADATA_FAILED = "Error while fetching file metadata for file ID:";
  public static final String FILE_DOWNLOAD_FAILED = "Error while downloading file with ID: ";
  public static final String ERROR_FETCH_FILE_METADATA = "Error while fetching file metadata for fileId: ";
  public static final String ERROR_IN_DOWNLOAD_FILE = "Error while downloading file for fileId : ";
  public static final String DUPLICATE_APPLICANT = "Applicant already applied for this position in the last 6 months.";
  public static final String DUPLICATE_APPLICATION_LOG = "Duplicate application within 6 months detected for email: {} and position: {}";
}
