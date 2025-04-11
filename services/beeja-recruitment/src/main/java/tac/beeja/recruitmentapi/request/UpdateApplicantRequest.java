package tac.beeja.recruitmentapi.request;

import java.util.List;
import lombok.Data;
import tac.beeja.recruitmentapi.enums.ApplicantStatus;
import tac.beeja.recruitmentapi.model.AssignedInterviewer;

@Data
public class UpdateApplicantRequest {
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String positionAppliedFor;
  private List<AssignedInterviewer> assignedInterviewers;
  private ApplicantStatus status;
}
