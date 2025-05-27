package tac.beeja.recruitmentapi.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import tac.beeja.recruitmentapi.enums.ApplicantStatus;

@Data
public class ApplicantRequest {
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String positionAppliedFor;
  private MultipartFile resume;
  private ApplicantStatus status;
  private String experience;
}
