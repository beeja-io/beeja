package tac.beeja.recruitmentapi.model;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "interviews")
public class Interview {
  @Id private String id;

  private String applicantId;
  private String applicantName;
  private String applicantEmail;
  private String positionAppliedFor;

  private List<String> interviewerEmails;
  private String interviewTitle;
  private String interviewDescription;

  private Date startDateTime;
  private Integer durationInMinutes;

  // Microsoft Teams meeting details
  private String meetingId;
  private String meetingLink;
  private String joinWebUrl;
  private String onlineMeetingId;

  // Additional meeting details
  private String organizerEmail; // hiring@techatcore.com
  private String organizationId;

  @Field("created_at")
  @CreatedDate
  private Date createdAt;

  @Field("modified_at")
  @LastModifiedDate
  private Date modifiedAt;

  private String createdBy;
  private String modifiedBy;
}
