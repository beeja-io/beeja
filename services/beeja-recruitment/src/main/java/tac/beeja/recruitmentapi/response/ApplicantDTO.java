package tac.beeja.recruitmentapi.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tac.beeja.recruitmentapi.enums.ApplicantStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantDTO {
  private String id;
  private String applicantId;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String positionAppliedFor;
  private ApplicantStatus status;
  private String experience;
  private String resumeId;
  private String referredByEmployeeName;
  private Date createdAt;
}
