package tac.beeja.recruitmentapi.request;

import lombok.Data;
import tac.beeja.recruitmentapi.enums.ApplicantStatus;
import tac.beeja.recruitmentapi.model.AssignedInterviewer;

import java.util.List;

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
