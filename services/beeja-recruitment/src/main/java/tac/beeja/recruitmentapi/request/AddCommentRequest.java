package tac.beeja.recruitmentapi.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentRequest {
  @NotBlank(message = "Applicant ID must not be blank")
  private String applicantId;

  @NotBlank(message = "Comment must not be blank")
  private String comment;
}
