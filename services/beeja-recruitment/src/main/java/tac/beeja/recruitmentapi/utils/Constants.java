package tac.beeja.recruitmentapi.utils;

public class Constants {
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

  //    EXCEPTIONS:
  public static final String ERROR_IN_RESUME_UPLOAD = "Error in uploading Resume";
  public static final String ERROR_IN_CREATING_APPLICANT = "Error in creating applicant, ";
  public static final String ERROR_IN_GETTING_LIST_OF_APPLICANTS = "Error in getting applicants, ";
  public static final String ERROR_IN_UPDATING_APPLICANTS = "Error in updating applicants, ";
  public static final String NO_APPLICANT_FOUND_WITH_GIVEN_ID = "No Applicant found with given ID ";
  public static final String UNAUTHORISED_ACCESS_TO_DOWNLOAD_RESUME = "No Access for resumes, ";
}
