package tac.beeja.recruitmentapi.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class AssignedInterviewer {

  @NotNull(message = "Employee ID is required")
  @NotEmpty(message = "Employee ID cannot be empty")
  private String employeeId;

  @NotNull(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  @NotNull(message = "Full name is required")
  @NotEmpty(message = "Full name cannot be empty")
  private String fullName;

  @NotNull(message = "Interview type is required")
  @NotEmpty(message = "Interview type cannot be empty")
  private String interviewType;

  @NotNull(message = "Order of interview is required")
  @NotEmpty(message = "Order of interview cannot be empty")
  private String orderOfInterview;

  @NotNull(message = "Interview date and time is required")
  @Future(message = "Interview date and time should be in the future")
  private Date interviewDateAndTime;

  private String interviewId;

  private String feedback;
}
