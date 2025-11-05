package tac.beeja.recruitmentapi.response;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterviewScheduleResponse {

  private String interviewId;
  private String applicantId;
  private String applicantName;
  private String applicantEmail;
  private String positionAppliedFor;

  private List<String> interviewerEmails;
  private String interviewTitle;
  private String interviewDescription;

  private Date startDateTime;
  private Date endDateTime;
  private Integer durationInMinutes;

  // Microsoft Teams meeting details
  private String meetingId;
  private String meetingLink;
  private String joinWebUrl;
  private String onlineMeetingId;

  private String organizerEmail;
  private String status;
  private String message;

  private Date createdAt;
}
