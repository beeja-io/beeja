package tac.beeja.recruitmentapi.serviceImpl;

import static tac.beeja.recruitmentapi.utils.Constants.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import tac.beeja.recruitmentapi.enums.ErrorCode;
import tac.beeja.recruitmentapi.enums.ErrorType;
import tac.beeja.recruitmentapi.exceptions.*;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.model.ApplicantComment;
import tac.beeja.recruitmentapi.model.AssignedInterviewer;
import tac.beeja.recruitmentapi.properties.OrganizationTest;
import tac.beeja.recruitmentapi.repository.ApplicantRepository;
import tac.beeja.recruitmentapi.request.AddCommentRequest;
import tac.beeja.recruitmentapi.request.ApplicantFeedbackRequest;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.request.FileRequest;
import tac.beeja.recruitmentapi.response.ApplicantDTO;
import tac.beeja.recruitmentapi.response.FileDownloadResultMetaData;
import tac.beeja.recruitmentapi.response.FileResponse;
import tac.beeja.recruitmentapi.response.PaginatedApplicantResponse;
import tac.beeja.recruitmentapi.service.ApplicantService;
import tac.beeja.recruitmentapi.utils.BuildErrorMessage;
import tac.beeja.recruitmentapi.utils.Constants;
import tac.beeja.recruitmentapi.utils.OrganizationCheck;
import tac.beeja.recruitmentapi.utils.UserContext;

@Service
@Slf4j
public class ApplicantServiceImpl implements ApplicantService {

  @Autowired FileClient fileClient;

  @Autowired ApplicantRepository applicantRepository;

  @Autowired MongoTemplate mongoTemplate;

  @Autowired AccountClient accountClient;

  @Autowired OrganizationTest organizationTest;

  @Override
  public Applicant postApplicant(ApplicantRequest applicant, boolean isReferral) throws Exception {
    Query query = new Query();
    query.addCriteria(
        Criteria.where("email")
            .is(applicant.getEmail())
            .and("positionAppliedFor")
            .is(applicant.getPositionAppliedFor())
            .and("organizationId")
            .is(UserContext.getLoggedInUserOrganization().get("id").toString()));
    List<Applicant> existingApplicants = mongoTemplate.find(query, Applicant.class);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -6);
    Date sixMonthsAgo = calendar.getTime();
    for (Applicant existingApplicant : existingApplicants) {
      Date createdAt = existingApplicant.getCreatedAt();
      if (createdAt != null && createdAt.after(sixMonthsAgo)) {
        log.error(
            DUPLICATE_APPLICATION_LOG, applicant.getEmail(), applicant.getPositionAppliedFor());
        throw new ConflictException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.CONFLICT, ErrorCode.DUPLICATE_APPLICANT, DUPLICATE_APPLICANT));
      }
    }
    //    accept only pdf, doc and docx for applicant.getResume()
    if (!applicant.getResume().getContentType().equals("application/pdf")
        && !applicant.getResume().getContentType().equals("application/msword")
        && !applicant
            .getResume()
            .getContentType()
            .equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
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
    String fileName =
        newApplicant.getFirstName()
            + "."
            + Objects.requireNonNull(applicant.getResume().getOriginalFilename())
                .substring(applicant.getResume().getOriginalFilename().lastIndexOf('.') + 1);
    FileRequest fileRequest = new FileRequest(applicant.getResume(), fileName, RESUME_FILE_ENTITY);
    try {
      ResponseEntity<?> fileResponse = fileClient.uploadFile(fileRequest);
      Map<String, Object> responseBody = (Map<String, Object>) fileResponse.getBody();
      fileId = responseBody.get("id").toString();
    } catch (Exception e) {
      log.error(ERROR_IN_RESUME_UPLOAD);
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.FEIGN_CLIENT_ERROR, ErrorCode.FILE_UPLOAD_FAILED, ERROR_IN_RESUME_UPLOAD));
    }

    newApplicant.setResumeId(fileId);
    try {
      return applicantRepository.save(newApplicant);
    } catch (Exception e) {
      log.error(ERROR_IN_CREATING_APPLICANT, e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.APPLICANT_CREATION_FAILED,
              ERROR_IN_CREATING_APPLICANT));
    }
  }

  @Override
  public PaginatedApplicantResponse getPaginatedApplicants(
      Integer page,
      Integer limit,
      String applicantId,
      String firstName,
      String positionAppliedFor,
      ApplicantStatus status,
      String experience,
      Date fromDate,
      Date toDate,
      String sortBy,
      String sortDirection) {

    int pageNumber = (page != null && page >= 1) ? page - 1 : 0;
    int pageSize = (limit != null && limit > 0 && limit <= 100) ? limit : 10;

    Pageable pageable =
        (sortBy != null && "asc".equalsIgnoreCase(sortDirection))
            ? PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortBy))
            : PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.DESC, sortBy != null ? sortBy : "createdAt"));

    List<Criteria> criteriaList = new ArrayList<>();

    if (applicantId != null && !applicantId.isEmpty()) {
      criteriaList.add(Criteria.where("applicantId").is(applicantId));
    }
    if (firstName != null && !firstName.isEmpty()) {
      criteriaList.add(Criteria.where("firstName").regex("^" + firstName + "$", "i"));
    }
    if (positionAppliedFor != null && !positionAppliedFor.isEmpty()) {
      criteriaList.add(
          Criteria.where("positionAppliedFor").regex("^" + positionAppliedFor + "$", "i"));
    }
    if (status != null) {
      criteriaList.add(Criteria.where("status").is(status));
    }
    if (experience != null && !experience.isEmpty()) {
      criteriaList.add(Criteria.where("experience").regex("^" + experience + "$", "i"));
    }
    if (fromDate != null && toDate != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(toDate);
      cal.set(Calendar.HOUR_OF_DAY, 23);
      cal.set(Calendar.MINUTE, 59);
      cal.set(Calendar.SECOND, 59);
      cal.set(Calendar.MILLISECOND, 999);
      toDate = cal.getTime();
      criteriaList.add(Criteria.where("createdAt").gte(fromDate).lte(toDate));
    } else if (fromDate != null) {
      criteriaList.add(Criteria.where("createdAt").gte(fromDate));
    } else if (toDate != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(toDate);
      cal.set(Calendar.HOUR_OF_DAY, 23);
      cal.set(Calendar.MINUTE, 59);
      cal.set(Calendar.SECOND, 59);
      cal.set(Calendar.MILLISECOND, 999);
      toDate = cal.getTime();
      criteriaList.add(Criteria.where("createdAt").lte(toDate));
    }

    Criteria finalCriteria = new Criteria();
    if (!criteriaList.isEmpty()) {
      finalCriteria.andOperator(criteriaList.toArray(new Criteria[0]));
    }

    Query countQuery = new Query(finalCriteria);
    long totalRecords = mongoTemplate.count(countQuery, Applicant.class);
    if (OrganizationCheck.isValidOrganizationId(
        UserContext.getLoggedInUserOrganization().get("id").toString(),
        organizationTest.getOrganizationId())) {
      log.info("Total records in organization: {}", totalRecords);
    }

    Query paginatedQuery = new Query(finalCriteria).with(pageable);
    List<Applicant> applicants = mongoTemplate.find(paginatedQuery, Applicant.class);

    List<ApplicantDTO> applicantDTOs =
        applicants.stream()
            .map(
                applicant ->
                    new ApplicantDTO(
                        applicant.getId(),
                        applicant.getApplicantId(),
                        applicant.getFirstName(),
                        applicant.getLastName(),
                        applicant.getEmail(),
                        applicant.getPhoneNumber(),
                        applicant.getPositionAppliedFor(),
                        applicant.getStatus(),
                        applicant.getExperience(),
                        applicant.getReferredByEmployeeName(),
                        applicant.getCreatedAt()))
            .collect(Collectors.toList());

    int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

    return new PaginatedApplicantResponse(
        applicantDTOs, pageNumber + 1, pageSize, totalRecords, totalPages);
  }

  private String generateApplicantId() {

    String organizationName = UserContext.getLoggedInUserOrganization().get("name").toString();

    String ORG_Prefix =
        organizationName.length() >= 3
            ? organizationName.toUpperCase().substring(0, 3).toUpperCase()
            : organizationName.toUpperCase();

    String datePart = new SimpleDateFormat("MMddyy").format(new Date());

    long existingCount =
        applicantRepository.countByOrganizationId(
            UserContext.getLoggedInUserOrganization().get("id").toString());
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
      log.error(ERROR_IN_GETTING_LIST_OF_APPLICANTS, e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.APPLICANT_FETCH_FAILED,
              ERROR_IN_GETTING_LIST_OF_APPLICANTS));
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
        log.error(NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId);
        throw new Exception(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND,
                ErrorCode.APPLICANT_NOT_FOUND,
                NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId));
      }

      for (Map.Entry<String, Object> entry : fields.entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        try {
          Field field = ReflectionUtils.findField(Applicant.class, key);
          if (field != null) {
            field.setAccessible(true);
            if (field.getType() == ApplicantStatus.class && value instanceof String) {
              value = ApplicantStatus.valueOf((String) value);
            }
            ReflectionUtils.setField(field, applicant, value);
          } else {
            String message = "Field " + key + " not found in Applicant class.";
            log.error(message);
            throw new Exception(
                BuildErrorMessage.buildErrorMessage(
                    ErrorType.DATA_PROCESSING_ERROR, ErrorCode.FIELD_NOT_FOUND, message));
          }
        } catch (Exception e) {
          String message = "Error updating field " + key + ": " + e.getMessage();
          log.error(message, e.getMessage());
          throw new Exception(
              BuildErrorMessage.buildErrorMessage(
                  ErrorType.DATA_PROCESSING_ERROR, ErrorCode.FIELD_UPDATE_FAILED, message));
        }
      }

      try {
        return applicantRepository.save(applicant);
      } catch (Exception e) {
        log.error(Constants.ERROR_IN_SAVING_UPDATED_APPLICANT, e.getMessage());
        throw new Exception(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.DB_ERROR,
                ErrorCode.APPLICANT_UPDATE_FAILED,
                Constants.ERROR_IN_SAVING_UPDATED_APPLICANT));
      }

    } catch (Exception e) {
      log.error(ERROR_IN_UPDATING_APPLICANTS, e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.INTERNAL_SERVER_ERROR,
              ErrorCode.APPLICANT_UPDATE_FAILED,
              ERROR_IN_UPDATING_APPLICANTS));
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
        log.error(Constants.UNAUTHORISED_ACCESS_TO_DOWNLOAD_RESUME);
        throw new UnAuthorisedException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.AUTHORIZATION_ERROR,
                ErrorCode.UNAUTHORIZED_FILE_ACCESS,
                Constants.UNAUTHORISED_ACCESS_TO_DOWNLOAD_RESUME));
      }
    } catch (Exception e) {
      log.error(Constants.ERROR_FETCH_FILE_METADATA + fileId);
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.FEIGN_CLIENT_ERROR,
              ErrorCode.FILE_METADATA_FETCH_FAILED,
              Constants.ERROR_FETCH_FILE_METADATA + fileId));
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
      log.error(Constants.ERROR_IN_DOWNLOAD_FILE + fileId);
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.FEIGN_CLIENT_ERROR,
              ErrorCode.FILE_DOWNLOAD_FAILED,
              Constants.ERROR_IN_DOWNLOAD_FILE + fileId));
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
      log.error(NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId);
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND,
              ErrorCode.APPLICANT_NOT_FOUND_FOR_FEEDBACK,
              NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId));
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
                () ->
                    new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                            ErrorType.RESOURCE_NOT_FOUND,
                            ErrorCode.APPLICANT_NOT_FOUND,
                            NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId)));

    if (applicant.getAssignedInterviewers() == null) {
      applicant.setAssignedInterviewers(new ArrayList<>());
    }

    boolean isAlreadyAssigned =
        applicant.getAssignedInterviewers().stream()
            .anyMatch(
                interviewer ->
                    interviewer.getEmployeeId().equals(assignedInterviewer.getEmployeeId()));

    if (isAlreadyAssigned) {
      log.error(Constants.INTERVIEWER_ALREADY_ASSIGNED);
      throw new BadRequestException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.BAD_REQUEST,
              ErrorCode.INTERVIEWER_ALREADY_ASSIGNED,
              Constants.INTERVIEWER_ALREADY_ASSIGNED));
    }

    ResponseEntity<?> employeeResponse;
    try {
      employeeResponse =
          accountClient.isEmployeeHasPermission(
              assignedInterviewer.getEmployeeId(), Constants.TAKE_INTERVIEW);
    } catch (Exception e) {
      log.error(Constants.INTERVIEWER_PERMISSION_FETCH_FAILED);
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.FEIGN_CLIENT_ERROR,
              ErrorCode.INTERVIEWER_PERMISSION_FETCH_FAILED,
              Constants.INTERVIEWER_PERMISSION_FETCH_FAILED));
    }

    if (employeeResponse.getStatusCode().isError()) {
      log.error(Constants.INTERVIEWER_PERMISSION_FETCH_FAILED);
      throw new InterviewerException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.FEIGN_CLIENT_ERROR,
              ErrorCode.INTERVIEWER_PERMISSION_FETCH_FAILED,
              Constants.INTERVIEWER_PERMISSION_FETCH_FAILED));
    }

    Boolean hasPermission = (Boolean) employeeResponse.getBody();
    if (Boolean.FALSE.equals(hasPermission)) {
      log.error(Constants.INTERVIEWER_PERMISSION_DENIED);
      throw new InterviewerException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.INTERVIEWER_PERMISSION_DENIED,
              Constants.INTERVIEWER_PERMISSION_DENIED));
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
      log.error(Constants.INTERVIEWER_ASSIGNMENT_FAILED, e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.INTERVIEWER_ASSIGNMENT_FAILED,
              Constants.INTERVIEWER_ASSIGNMENT_FAILED));
    }
  }

  @Override
  public Applicant getApplicantById(String applicantId) throws Exception {
    try {
      Applicant applicant =
          applicantRepository.findByIdAndOrganizationId(
              applicantId, UserContext.getLoggedInUserOrganization().get("id").toString());
      if (applicant == null) {
        log.error(NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId);
        throw new ResourceNotFoundException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND,
                ErrorCode.APPLICANT_NOT_FOUND,
                NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId));
      }
      return applicant;
    } catch (ResourceNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error(Constants.ERROR_IN_GETTING_APPLICANT + applicantId, e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.GET_APPLICANT_FAILED,
              Constants.ERROR_IN_GETTING_APPLICANT + applicantId));
    }
  }

  @Override
  public Applicant deleteInterviewerByInterviewID(String applicantId, String interviewId)
      throws Exception {
    Applicant applicant =
        applicantRepository.findByIdAndOrganizationId(
            applicantId, UserContext.getLoggedInUserOrganization().get("id").toString());
    if (applicant == null) {
      log.error(NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId);
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND,
              ErrorCode.APPLICANT_NOT_FOUND,
              NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId));
    }
    List<AssignedInterviewer> assignedInterviewers = applicant.getAssignedInterviewers();
    if (assignedInterviewers == null || assignedInterviewers.isEmpty()) {
      log.error(Constants.NO_INTERVIEWER_ASSIGNED + applicantId);
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND,
              ErrorCode.NO_INTERVIEWER_ASSIGNED,
              Constants.NO_INTERVIEWER_ASSIGNED + applicantId));
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
      log.error(NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicant.getApplicantId());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND,
              ErrorCode.APPLICANT_NOT_FOUND,
              NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicant.getApplicantId()));
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
      log.error(NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId);
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND,
              ErrorCode.APPLICANT_NOT_FOUND,
              NO_APPLICANT_FOUND_WITH_GIVEN_ID + applicantId));
    }
    try {
      ApplicantStatus applicantStatus = ApplicantStatus.valueOf(status);
      applicant.setStatus(applicantStatus);
      return applicantRepository.save(applicant);
    } catch (IllegalArgumentException e) {
      log.error(Constants.INVALID_APPLICANT_STATUS + status, e.getMessage());
      throw new BadRequestException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.BAD_REQUEST,
              ErrorCode.INVALID_APPLICANT_STATUS,
              Constants.INVALID_APPLICANT_STATUS + status));
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
