package tac.beeja.recruitmentapi.serviceImpl;

import com.microsoft.graph.models.Event;

import java.util.Calendar;
import java.util.Date;

import com.microsoft.graph.models.OnlineMeeting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tac.beeja.recruitmentapi.config.MicrosoftTeamsProperties;
import tac.beeja.recruitmentapi.exceptions.BadRequestException;
import tac.beeja.recruitmentapi.exceptions.ResourceNotFoundException;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.model.Interview;
import tac.beeja.recruitmentapi.repository.ApplicantRepository;
import tac.beeja.recruitmentapi.repository.InterviewRepository;
import tac.beeja.recruitmentapi.request.ScheduleInterviewRequest;
import tac.beeja.recruitmentapi.response.InterviewScheduleResponse;
import tac.beeja.recruitmentapi.service.InterviewScheduleService;
import tac.beeja.recruitmentapi.service.MicrosoftTeamsClientService;
import tac.beeja.recruitmentapi.utils.UserContext;

@Service
@Slf4j
public class InterviewScheduleServiceImpl implements InterviewScheduleService {

  @Autowired private MicrosoftTeamsClientService teamsClientService;

  @Autowired private InterviewRepository interviewRepository;

  @Autowired private ApplicantRepository applicantRepository;

  @Autowired private MicrosoftTeamsProperties teamsProperties;

  @Override
  public InterviewScheduleResponse scheduleInterview(ScheduleInterviewRequest request)
      throws Exception {
    log.info("Scheduling interview for applicant: {}", request.getApplicantId());

    try {
      Applicant applicant =
          applicantRepository
              .findByApplicantIdAndOrganizationId(request.getApplicantId(), UserContext.getLoggedInUserOrganization().get("id").toString())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Applicant not found with ID: " + request.getApplicantId()));

      if (request.getStartDateTime().before(new Date())) {
        throw new BadRequestException("Interview start time must be in the future");
      }

      if (request.getInterviewerEmails() == null || request.getInterviewerEmails().isEmpty()) {
        throw new BadRequestException("At least one interviewer email is required");
      }

      if (request.getDurationInMinutes() <= 0) {
        throw new BadRequestException("Duration must be greater than 0");
      }

      String description = buildInterviewDescription(applicant, request.getInterviewDescription());
      Event calendarEvent =
          teamsClientService.createCalendarEventWithTeamsMeeting(
              request.getInterviewTitle(),
              description,
              request.getStartDateTime(),
              request.getDurationInMinutes(),
              request.getInterviewerEmails(),
              applicant.getEmail(),
              applicant.getFirstName() + " " + applicant.getLastName());

      Interview interview = new Interview();
      interview.setApplicantId(applicant.getApplicantId());
      interview.setApplicantName(applicant.getFirstName() + " " + applicant.getLastName());
      interview.setApplicantEmail(applicant.getEmail());
      interview.setPositionAppliedFor(applicant.getPositionAppliedFor());
      interview.setInterviewerEmails(request.getInterviewerEmails());
      interview.setInterviewTitle(request.getInterviewTitle());
      interview.setInterviewDescription(request.getInterviewDescription());
      interview.setStartDateTime(request.getStartDateTime());
      interview.setDurationInMinutes(request.getDurationInMinutes());

      interview.setCalendarEventId(calendarEvent.getId());
      interview.setMeetingId(calendarEvent.getId());

      String joinUrl = null;
      if (calendarEvent.getOnlineMeeting() != null && calendarEvent.getOnlineMeeting().getJoinUrl() != null) {
        joinUrl = calendarEvent.getOnlineMeeting().getJoinUrl();
      }
      
      interview.setMeetingLink(joinUrl);
      interview.setJoinWebUrl(joinUrl);
      interview.setOnlineMeetingId(
              calendarEvent.getOnlineMeeting() != null && calendarEvent.getOnlineMeeting().getJoinUrl() != null
              ? calendarEvent.getOnlineMeeting().getJoinUrl()
              : null);
      interview.setOrganizerEmail(teamsProperties.getServiceAccountEmail());
      interview.setOrganizationId(UserContext.getLoggedInUserOrganization().get("id").toString());
      
      try {
        interview.setCreatedBy(UserContext.getLoggedInUserEmail());
      } catch (Exception e) {
        log.warn("Could not set created by: {}", e.getMessage());
        interview.setCreatedBy("system");
      }

      Interview savedInterview = interviewRepository.save(interview);

      log.info(
          "Interview scheduled successfully. Interview ID: {}, Meeting ID: {}",
          savedInterview.getId(),
          savedInterview.getMeetingId());

      return buildInterviewScheduleResponse(savedInterview, "success", "Interview scheduled successfully");

    } catch (ResourceNotFoundException | BadRequestException e) {
      log.error("Validation error while scheduling interview: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error scheduling interview: {}", e.getMessage(), e);
      throw new Exception("Failed to schedule interview: " + e.getMessage(), e);
    }
  }

  @Override
  public InterviewScheduleResponse getInterviewById(String interviewId) throws Exception {
     try {
      Interview interview =
          interviewRepository
              .findById(interviewId)
              .orElseThrow(
                  () -> new ResourceNotFoundException("Interview not found with ID: " + interviewId));

      return buildInterviewScheduleResponse(interview, "success", "Interview details retrieved successfully");

    } catch (ResourceNotFoundException e) {
      log.error("Interview not found: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error fetching interview: {}", e.getMessage(), e);
      throw new Exception("Failed to fetch interview details: " + e.getMessage(), e);
    }
  }

  @Override
  public void cancelInterview(String interviewId) throws Exception {
    log.info("Cancelling interview with ID: {}", interviewId);

    try {
      Interview interview =
          interviewRepository
              .findByIdAndOrganizationId(interviewId, UserContext.getLoggedInUserOrganization().get("id").toString())
              .orElseThrow(
                  () -> new ResourceNotFoundException("Interview not found with ID: " + interviewId));

      String eventIdToDelete = interview.getCalendarEventId() != null ? interview.getCalendarEventId() : interview.getMeetingId();
      if (eventIdToDelete != null) {
        teamsClientService.deleteEvent(eventIdToDelete);
      }

      interviewRepository.deleteById(interviewId);

      log.info("Interview cancelled successfully: {}", interviewId);

    } catch (ResourceNotFoundException e) {
      log.error("Interview not found: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error cancelling interview: {}", e.getMessage(), e);
      throw new Exception("Failed to cancel interview: " + e.getMessage(), e);
    }
  }

  private String buildInterviewDescription(Applicant applicant, String customDescription) {
    StringBuilder description = new StringBuilder();
    description.append("<h3>Interview Details</h3>");
    description.append("<p><strong>Applicant:</strong> ")
        .append(applicant.getFirstName())
        .append(" ")
        .append(applicant.getLastName())
        .append("</p>");
    description.append("<p><strong>Email:</strong> ").append(applicant.getEmail()).append("</p>");
    description.append("<p><strong>Position:</strong> ")
        .append(applicant.getPositionAppliedFor())
        .append("</p>");
    description.append("<p><strong>Experience:</strong> ")
        .append(applicant.getExperience())
        .append("</p>");

    if (customDescription != null && !customDescription.isEmpty()) {
      description.append("<br/><h4>Additional Notes:</h4>");
      description.append("<p>").append(customDescription).append("</p>");
    }

    return description.toString();
  }

  private InterviewScheduleResponse buildInterviewScheduleResponse(
      Interview interview, String status, String message) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(interview.getStartDateTime());
    calendar.add(Calendar.MINUTE, interview.getDurationInMinutes());
    Date endDateTime = calendar.getTime();

    return InterviewScheduleResponse.builder()
        .interviewId(interview.getId())
        .applicantId(interview.getApplicantId())
        .applicantName(interview.getApplicantName())
        .applicantEmail(interview.getApplicantEmail())
        .positionAppliedFor(interview.getPositionAppliedFor())
        .interviewerEmails(interview.getInterviewerEmails())
        .interviewTitle(interview.getInterviewTitle())
        .interviewDescription(interview.getInterviewDescription())
        .startDateTime(interview.getStartDateTime())
        .endDateTime(endDateTime)
        .durationInMinutes(interview.getDurationInMinutes())
        .meetingId(interview.getMeetingId())
        .meetingLink(interview.getMeetingLink())
        .joinWebUrl(interview.getJoinWebUrl())
        .onlineMeetingId(interview.getOnlineMeetingId())
        .organizerEmail(interview.getOrganizerEmail())
        .status(status)
        .message(message)
        .createdAt(interview.getCreatedAt())
        .build();
  }
}
