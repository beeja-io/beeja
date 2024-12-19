package tac.beeja.recruitmentapi.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tac.beeja.recruitmentapi.client.FileClient;
import tac.beeja.recruitmentapi.exceptions.FeignClientException;
import tac.beeja.recruitmentapi.exceptions.UnAuthorisedException;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.repository.ApplicantRepository;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.response.FileDownloadResultMetaData;
import tac.beeja.recruitmentapi.response.FileResponse;
import tac.beeja.recruitmentapi.service.ApplicantService;
import tac.beeja.recruitmentapi.service.ReferralService;
import tac.beeja.recruitmentapi.utils.UserContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ReferralServiceImpl implements ReferralService {
  @Autowired ApplicantRepository applicantRepository;

  @Autowired ApplicantService applicantService;

  @Autowired FileClient fileClient;

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
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new Exception(e.getMessage());
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
      log.error(e.getMessage());
      throw new Exception(e.getMessage());
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
        throw new UnAuthorisedException("Constants.UNAUTHORISED_ACCESS");
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
          return finalMetaData.getFileName() != null ? finalMetaData.getFileName() : "cv_Beeja";
        }
      };
    } catch (Exception e) {
      throw new FeignClientException(e.getMessage());
    }
  }

  private static FileDownloadResultMetaData getMetaData(ResponseEntity<byte[]> fileResponse) {
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
