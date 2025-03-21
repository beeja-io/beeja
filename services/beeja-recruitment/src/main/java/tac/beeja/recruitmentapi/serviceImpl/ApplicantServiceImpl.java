package tac.beeja.recruitmentapi.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import tac.beeja.recruitmentapi.client.AccountClient;
import tac.beeja.recruitmentapi.client.FileClient;
import tac.beeja.recruitmentapi.enums.ApplicantStatus;
import tac.beeja.recruitmentapi.exceptions.BadRequestException;
import tac.beeja.recruitmentapi.exceptions.FeignClientException;
import tac.beeja.recruitmentapi.exceptions.InterviewerException;
import tac.beeja.recruitmentapi.exceptions.ResourceNotFoundException;
import tac.beeja.recruitmentapi.exceptions.UnAuthorisedException;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.model.ApplicantComment;
import tac.beeja.recruitmentapi.model.AssignedInterviewer;
import tac.beeja.recruitmentapi.repository.ApplicantRepository;
import tac.beeja.recruitmentapi.request.AddCommentRequest;
import tac.beeja.recruitmentapi.request.ApplicantFeedbackRequest;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.request.FileRequest;
import tac.beeja.recruitmentapi.response.FileDownloadResultMetaData;
import tac.beeja.recruitmentapi.response.FileResponse;
import tac.beeja.recruitmentapi.service.ApplicantService;
import tac.beeja.recruitmentapi.utils.Constants;
import tac.beeja.recruitmentapi.utils.UserContext;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import static tac.beeja.recruitmentapi.utils.Constants.ERROR_IN_CREATING_APPLICANT;
import static tac.beeja.recruitmentapi.utils.Constants.ERROR_IN_GETTING_LIST_OF_APPLICANTS;
import static tac.beeja.recruitmentapi.utils.Constants.ERROR_IN_RESUME_UPLOAD;
import static tac.beeja.recruitmentapi.utils.Constants.ERROR_IN_UPDATING_APPLICANTS;
import static tac.beeja.recruitmentapi.utils.Constants.NO_APPLICANT_FOUND_WITH_GIVEN_ID;
import static tac.beeja.recruitmentapi.utils.Constants.RESUME_FILE_ENTITY;

@Service
@Slf4j
public class ApplicantServiceImpl implements ApplicantService {

  @Autowired FileClient fileClient;

  @Autowired ApplicantRepository applicantRepository;

  @Autowired MongoTemplate mongoTemplate;

  @Autowired AccountClient accountClient;

  @Override
  public Applicant postApplicant(ApplicantRequest applicant, boolean isReferral) throws Exception {


    //    accept only pdf, doc and docx for applicant.getResume()
    if (!applicant.getResume().getContentType().equals("application/pdf")
            && !applicant.getResume().getContentType().equals("application/msword")
            && !applicant.getResume().getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
      throw new BadRequestException("Only PDF, DOC and DOCX files are allowed");
    }
    Applicant newApplicant = new Applicant();
    newApplicant.setEmail(applicant.getEmail());
    newApplicant.setFirstName(applicant.getFirstName());
    newApplicant.setLastName(applicant.getLastName());
    newApplicant.setPhoneNumber(applicant.getPhoneNumber());
    newApplicant.setPositionAppliedFor(applicant.getPositionAppliedFor());
    newApplicant.setOrganizationId(UserContext.getLoggedInUserOrganization().get("id").toString());
    newApplicant.setApplicantId(generateApplicantId());
    newApplicant.setStatus(ApplicantStatus.APPLIED);
    newApplicant.setExperience(applicant.getExperience());
    String fileId;
    if (isReferral) {
      newApplicant.setReferredByEmployeeId(UserContext.getLoggedInEmployeeId());
      newApplicant.setReferredByEmployeeName(UserContext.getLoggedInUserName());
    }
    String fileName = newApplicant.getFirstName() +"."+ Objects.requireNonNull(applicant.getResume().getOriginalFilename()).substring(applicant.getResume().getOriginalFilename().lastIndexOf('.') + 1);
    FileRequest fileRequest = new FileRequest(applicant.getResume(), fileName , RESUME_FILE_ENTITY);
    try {
      ResponseEntity<?> fileResponse = fileClient.uploadFile(fileRequest);
      Map<String, Object> responseBody = (Map<String, Object>) fileResponse.getBody();
      fileId = responseBody.get("id").toString();
    } catch (Exception e) {
      throw new FeignClientException(ERROR_IN_RESUME_UPLOAD + e.getMessage());
    }

    newApplicant.setResumeId(fileId);
    try {
      return applicantRepository.save(newApplicant);
    } catch (Exception e) {
      throw new Exception(ERROR_IN_CREATING_APPLICANT + e.getMessage());
    }
  }


  private String generateApplicantId() {

    String organizationName = UserContext.getLoggedInUserOrganization().get("name").toString();

    String ORG_Prefix = organizationName.length()>=3 ? organizationName.toUpperCase().substring(0,3).toUpperCase() : organizationName.toUpperCase();

    String datePart = new SimpleDateFormat("MMddyy").format(new Date());

    long existingCount = applicantRepository.countByOrganizationId(UserContext.getLoggedInUserOrganization().get("id").toString());
    int newCount = (int) (existingCount + 1);

    String sequencePart = String.format("%04d", newCount);

    return ORG_Prefix + datePart + sequencePart;

  }

  @Override
  public List<Applicant> getAllApplicantsInOrganization() throws Exception {
    try {
      if (UserContext.getLoggedInUserPermissions().contains(Constants.GET_ENTIRE_APPLICANTS)) {
        return applicantRepository.findAllByOrganizationId(
            UserContext.getLoggedInUserOrganization().get("id").toString());
      }
      Query query = new Query();
      query.addCriteria(
          Criteria.where("assignedInterviewers")
              .elemMatch(Criteria.where("employeeId").is(UserContext.getLoggedInEmployeeId())));

      List<Applicant> applicants = mongoTemplate.find(query, Applicant.class);
      return applicants;

    } catch (Exception e) {
      throw new Exception(ERROR_IN_GETTING_LIST_OF_APPLICANTS + e.getMessage());
    }
  }

  @Override
  public Applicant updateApplicant(String applicantId, Map<String, Object> fields)
      throws Exception {
    try {
      Applicant applicant =
          applicantRepository.findByIdAndOrganizationId(
              applicantId, UserContext.getLoggedInUserOrganization().get("id").toString());
      if (applicant == null) {
        throw new Exception(NO_APPLICANT_FOUND_WITH_GIVEN_ID);
      }

      for (Map.Entry<String, Object> entry : fields.entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        try {
          Field field = ReflectionUtils.findField(Applicant.class, key);
          if (field != null) {
            field.setAccessible(true);
            if (field.getType() == ApplicantStatus.class) {
              if (value instanceof String) {
                value = ApplicantStatus.valueOf((String) value);
              }
            }
            ReflectionUtils.setField(field, applicant, value);
          } else {
            throw new Exception("Field " + key + " not found in Applicant class.");
          }
        } catch (Exception e) {
          throw new RuntimeException("Error updating field " + key + ": " + e.getMessage());
        }
      }
      return applicantRepository.save(applicant);
    } catch (Exception e) {
      throw new Exception(ERROR_IN_UPDATING_APPLICANTS + e.getMessage());
    }
  }

  @Override
  public ByteArrayResource downloadFile(String fileId) throws Exception {

    /*Checking File Type
     * If file tye is not expense, then throwing an error
     */
    try {
      ResponseEntity<?> response = fileClient.getFileById(fileId);
      LinkedHashMap<String, Object> responseBody =
          (LinkedHashMap<String, Object>) response.getBody();

      ObjectMapper objectMapper = new ObjectMapper();
      FileResponse file = objectMapper.convertValue(responseBody, FileResponse.class);
      if (!Objects.equals(file.getEntityType(), RESUME_FILE_ENTITY)) {
        throw new UnAuthorisedException(Constants.UNAUTHORISED_ACCESS_TO_DOWNLOAD_RESUME);
      }
    } catch (Exception e) {
      throw new FeignClientException(e.getMessage());
    }

    try {
      ResponseEntity<byte[]> fileResponse = fileClient.downloadFile(fileId);
      byte[] fileData = fileResponse.getBody();
      FileDownloadResultMetaData finalMetaData = getMetaData(fileResponse);

      return new ByteArrayResource(Objects.requireNonNull(fileData)) {
        @Override
        public String getFilename() {
          return finalMetaData.getFileName() != null
              ? finalMetaData.getFileName()
              : "Beeja_Resume.pdf";
        }
      };
    } catch (Exception e) {
      throw new FeignClientException(e.getMessage());
    }
  }

  @Override
  public Applicant submitFeedback(
      String applicantId, ApplicantFeedbackRequest applicantFeedbackRequest) {
    Query query =
        new Query(
            Criteria.where("_id")
                .is(applicantId)
                .and("assignedInterviewers")
                .elemMatch(Criteria.where("employeeId").is(UserContext.getLoggedInEmployeeId())));

    Update update =
        new Update().set("assignedInterviewers.$.feedback", applicantFeedbackRequest.getFeedback());

    Applicant applicant =
        mongoTemplate.findAndModify(
            query, update, new FindAndModifyOptions().returnNew(true), Applicant.class);

    if (applicant == null) {
      throw new ResourceNotFoundException(NO_APPLICANT_FOUND_WITH_GIVEN_ID);
    }
    return applicant;
  }

  @Override
  public Applicant assignInterviewer(String applicantId, AssignedInterviewer assignedInterviewer)
      throws Exception {
    Applicant applicant =
        applicantRepository
            .findById(applicantId)
            .orElseThrow(
                () -> new ResourceNotFoundException("No applicant found with the given ID"));
    if (applicant.getAssignedInterviewers() == null) {
      applicant.setAssignedInterviewers(new ArrayList<>());
    }
    boolean isAlreadyAssigned =
        applicant.getAssignedInterviewers().stream()
            .anyMatch(
                interviewer ->
                    interviewer.getEmployeeId().equals(assignedInterviewer.getEmployeeId()));

    if (isAlreadyAssigned) {
      throw new BadRequestException("Interviewer already assigned to this applicant");
    }
    ResponseEntity<?> employeeResponse;
    try {
      employeeResponse =
          accountClient.isEmployeeHasPermission(
              assignedInterviewer.getEmployeeId(), Constants.TAKE_INTERVIEW);
    } catch (Exception e) {
      throw new FeignClientException(
          "Error in fetching interviewer permission, please check interviewer Employee ID");
    }
    if (employeeResponse.getStatusCode().isError()) {
      throw new InterviewerException("Error in checking permission");
    }
    Boolean hasPermission = (Boolean) employeeResponse.getBody();
    if (Boolean.FALSE.equals(hasPermission)) {
      throw new InterviewerException("Interviewer does not have the required permission");
    }

    String uuid = UUID.randomUUID().toString();
    String interviewId =
        UserContext.getLoggedInUserOrganization()
                .get("name")
                .toString()
                .substring(0, 2)
                .toUpperCase()
            + uuid.substring(uuid.length() - 4).toUpperCase()
            + new SimpleDateFormat("ddMM").format(new Date());
    assignedInterviewer.setInterviewId(interviewId);

    applicant.getAssignedInterviewers().add(assignedInterviewer);
    try {
      return applicantRepository.save(applicant);
    } catch (Exception e) {
      throw new Exception("Error in assigning interviewer");
    }
  }

  @Override
  public Applicant getApplicantById(String applicantId) throws Exception {
    try {
      Applicant applicant =
          applicantRepository.findByIdAndOrganizationId(
              applicantId, UserContext.getLoggedInUserOrganization().get("id").toString());
      if (applicant == null) {
        throw new ResourceNotFoundException("No applicant found with the given ID");
      }
      return applicant;
    } catch (Exception e) {
      throw new Exception("Error in getting applicant by ID");
    }
  }

  @Override
  public Applicant deleteInterviewerByInterviewID(String applicantId, String interviewId)
      throws Exception {
    Applicant applicant =
        applicantRepository.findByIdAndOrganizationId(
            applicantId, UserContext.getLoggedInUserOrganization().get("id").toString());
    if (applicant == null) {
      throw new ResourceNotFoundException("No applicant found with the given ID");
    }
    List<AssignedInterviewer> assignedInterviewers = applicant.getAssignedInterviewers();
    if (assignedInterviewers == null || assignedInterviewers.isEmpty()) {
      throw new ResourceNotFoundException("No interviewer assigned to this applicant");
    }
    assignedInterviewers.removeIf(
        assignedInterviewer -> assignedInterviewer.getInterviewId().equals(interviewId));
    applicant.setAssignedInterviewers(assignedInterviewers);
    return applicantRepository.save(applicant);
  }

  @Override
  public Applicant addCommentToApplicant(AddCommentRequest addCommentRequest) throws Exception {
    Applicant applicant =
        applicantRepository.findByIdAndOrganizationId(
            addCommentRequest.getApplicantId(),
            UserContext.getLoggedInUserOrganization().get("id").toString());

    if (applicant == null) {
      throw new Exception("No Applicant found");
    }

    List<ApplicantComment> applicantComments = applicant.getApplicantComments();
    if (applicantComments == null) {
      applicantComments = new ArrayList<>();
    }

    int nextCommentId =
        applicantComments.stream().map(ApplicantComment::getId).max(Integer::compare).orElse(0) + 1;

    ApplicantComment newComment = new ApplicantComment();
    newComment.setId(nextCommentId);
    newComment.setCommentedByEmail(UserContext.getLoggedInUserEmail());
    newComment.setCommentedByName(UserContext.getLoggedInUserName());
    newComment.setMessage(addCommentRequest.getComment());
    newComment.setCreatedAt(new Date());
    applicantComments.add(newComment);
    applicant.setApplicantComments(applicantComments);

    return applicantRepository.save(applicant);
  }

  @Override
  public Applicant changeStatusOfApplicant(String applicantId, String status) throws Exception {
    Applicant applicant =
        applicantRepository.findByIdAndOrganizationId(
            applicantId, UserContext.getLoggedInUserOrganization().get("id").toString());
    if (applicant == null) {
      throw new ResourceNotFoundException("No applicant found with the given ID");
    }
    try {
      ApplicantStatus applicantStatus = ApplicantStatus.valueOf(status);
      applicant.setStatus(applicantStatus);
      return applicantRepository.save(applicant);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid status provided");
    }
  }

  private static FileDownloadResultMetaData getMetaData(ResponseEntity<byte[]> fileResponse) {
    HttpHeaders headers = fileResponse.getHeaders();
    String createdBy = headers.getFirst("createdby");
    String organizationId = headers.getFirst("organizationid");
    String entityId = headers.getFirst("entityId");
    String filename = headers.getFirst("fileName");
    return new FileDownloadResultMetaData(filename, createdBy, entityId, organizationId);
  }
}
