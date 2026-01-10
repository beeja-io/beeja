package tac.beeja.recruitmentapi.request;

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
public class UpdateInterviewRequest {

  private List<String> interviewerEmails;

  private String interviewTitle;

  private String interviewDescription;

  private Date startDateTime;

  @Positive(message = "Duration must be a positive number")
  private Integer durationInMinutes;
}
