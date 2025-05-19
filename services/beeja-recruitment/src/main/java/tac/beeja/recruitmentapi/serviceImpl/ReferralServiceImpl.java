package tac.beeja.recruitmentapi.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tac.beeja.recruitmentapi.client.FileClient;
import tac.beeja.recruitmentapi.exceptions.BadRequestException;
import tac.beeja.recruitmentapi.exceptions.ConflictException;
import tac.beeja.recruitmentapi.exceptions.FeignClientException;
import tac.beeja.recruitmentapi.exceptions.UnAuthorisedException;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.repository.ApplicantRepository;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.response.FileDownloadResultMetaData;
import tac.beeja.recruitmentapi.response.FileResponse;
import tac.beeja.recruitmentapi.service.ApplicantService;
import tac.beeja.recruitmentapi.service.ReferralService;
import tac.beeja.recruitmentapi.utils.BuildErrorMessage;
import tac.beeja.recruitmentapi.enums.ErrorCode;
import tac.beeja.recruitmentapi.enums.ErrorType;
import tac.beeja.recruitmentapi.utils.Constants;
import tac.beeja.recruitmentapi.utils.UserContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static tac.beeja.recruitmentapi.utils.Constants.DUPLICATE_APPLICANT;

@Slf4j
@Service
public class ReferralServiceImpl implements ReferralService {
  @Autowired ApplicantRepository applicantRepository;

  @Autowired ApplicantService applicantService;

  @Autowired FileClient fileClient;

  @Autowired
  ObjectMapper objectMapper;


  @Override
  public Applicant newReferral(ApplicantRequest applicantRequest) throws Exception {
    ApplicantRequest newApplicant = new ApplicantRequest();
    newApplicant.setEmail(applicantRequest.getEmail());
    newApplicant.setFirstName(applicantRequest.getFirstName());
    newApplicant.setLastName(applicantRequest.getLastName());
    newApplicant.setExperience(applicantRequest.getExperience());
    newApplicant.setPhoneNumber(applicantRequest.getPhoneNumber());
    newApplicant.setPositionAppliedFor(applicantRequest.getPositionAppliedFor());
    newApplicant.setResume(applicantRequest.getResume());
    Applicant applicant = null;
    try {
      applicant = applicantService.postApplicant(newApplicant, true);
    }  catch (ConflictException c) {
      log.error(DUPLICATE_APPLICANT + newApplicant.getEmail());

      throw new ConflictException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.CONFLICT,
                      ErrorCode.DUPLICATE_APPLICANT,
                      DUPLICATE_APPLICANT
              )
      );
    }
    catch (Exception e) {
      log.error(Constants.REFERRAL_CREATION_FAILED + newApplicant.getEmail(), e.getMessage());
      throw new Exception(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.INTERNAL_SERVER_ERROR,
                      ErrorCode.REFERRAL_CREATION_FAILED,
                      Constants.REFERRAL_CREATION_FAILED + newApplicant.getEmail()));
    }
    return applicant;
  }

  @Override
  public List<Applicant> getMyReferrals() throws Exception {
    try {
      return applicantRepository.findByReferredByEmployeeIdAndOrganizationId(
          UserContext.getLoggedInEmployeeId(),
          UserContext.getLoggedInUserOrganization().get("id").toString());
    } catch (Exception e) {
      log.error(Constants.GET_REFERRALS_FAILED + UserContext.getLoggedInEmployeeId(), e.getMessage());
      throw new Exception(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.DB_ERROR,
                      ErrorCode.GET_REFERRALS_FAILED,
                      Constants.GET_REFERRALS_FAILED + UserContext.getLoggedInEmployeeId()));
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
      if (!Objects.equals(file.getEntityType(), "resume")) {
        log.error(Constants.UNAUTHORISED_RESUME_ACCESS + fileId);
        throw new UnAuthorisedException(
                BuildErrorMessage.buildErrorMessage(
                        ErrorType.AUTHORIZATION_ERROR,
                        ErrorCode.UNAUTHORISED_RESUME_ACCESS,
                        Constants.UNAUTHORISED_RESUME_ACCESS + fileId));
      }
    } catch (Exception e) {
      log.error(Constants.GET_FILE_METADATA_FAILED + fileId, e.getMessage());
      throw new FeignClientException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.FEIGN_CLIENT_ERROR,
                      ErrorCode.GET_FILE_METADATA_FAILED,
                      Constants.GET_FILE_METADATA_FAILED+ fileId));
    }

    try {
      ResponseEntity<byte[]> fileResponse = fileClient.downloadFile(fileId);
      byte[] fileData = fileResponse.getBody();
      FileDownloadResultMetaData finalMetaData = getMetaData(fileResponse);

      return new ByteArrayResource(Objects.requireNonNull(fileData)) {
        @Override
        public String getFilename() {
          return finalMetaData.getFileName() != null ? finalMetaData.getFileName() : "cv_Beeja";
        }
      };
    } catch (Exception e) {
      log.error(Constants.FILE_DOWNLOAD_FAILED + fileId, e.getMessage());
      throw new FeignClientException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.FEIGN_CLIENT_ERROR,
                      ErrorCode.FILE_DOWNLOAD_FAILED,
                      Constants.FILE_DOWNLOAD_FAILED + fileId));
    }
  }

  public static FileDownloadResultMetaData getMetaData(ResponseEntity<byte[]> fileResponse) {
    HttpHeaders headers = fileResponse.getHeaders();
    String contentDisposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
    String createdBy = headers.getFirst("createdby");
    String organizationId = headers.getFirst("organizationid");
    String entityId = headers.getFirst("entityId");
    String filename = null;

    if (contentDisposition != null && !contentDisposition.isEmpty()) {
      int startIndex = contentDisposition.indexOf("filename=\"") + 10;
      int endIndex = contentDisposition.lastIndexOf("\"");
      if (endIndex != -1) {
        filename = contentDisposition.substring(startIndex, endIndex);
      }
    }
    return new FileDownloadResultMetaData(filename, createdBy, entityId, organizationId);
  }
}
