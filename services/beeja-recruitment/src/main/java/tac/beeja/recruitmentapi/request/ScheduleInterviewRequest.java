package tac.beeja.recruitmentapi.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleInterviewRequest {

  @NotBlank(message = "Applicant ID is required")
  private String applicantId;

  @NotEmpty(message = "At least one interviewer email is required")
  private List<String> interviewerEmails;

  @NotBlank(message = "Interview title is required")
  private String interviewTitle;

  private String interviewDescription;

  @NotNull(message = "Start date and time is required")
  private Date startDateTime;

  @NotNull(message = "Duration is required")
  @Positive(message = "Duration must be a positive number")
  private Integer durationInMinutes;
}
