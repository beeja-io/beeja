package tac.beeja.recruitmentapi.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.models.*;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tac.beeja.recruitmentapi.config.MicrosoftTeamsProperties;
import tac.beeja.recruitmentapi.exceptions.BadRequestException;

@Service
@Slf4j
public class MicrosoftTeamsClientService {

  @Autowired private MicrosoftTeamsProperties teamsProperties;

  private GraphServiceClient graphClient;

  private GraphServiceClient getGraphClient() {
    if (graphClient == null) {
      try {
        ClientSecretCredential clientSecretCredential =
            new ClientSecretCredentialBuilder()
                .clientId(teamsProperties.getClientId())
                .clientSecret(teamsProperties.getClientSecret())
                .tenantId(teamsProperties.getTenantId())
                .build();

        graphClient =
            new GraphServiceClient(clientSecretCredential, teamsProperties.getScope());
      } catch (Exception e) {
        log.error("Failed to initialize Microsoft Graph client: {}", e.getMessage(), e);
        throw new BadRequestException("Failed to initialize Microsoft Teams client");
      }
    }
    return graphClient;
  }

    /**
     * Creates a calendar event with Microsoft Teams meeting link and sends email invitations to attendees.
     * This method creates both the calendar event and the Teams meeting, ensuring attendees receive invitations.
     */
    public Event createCalendarEventWithTeamsMeeting(
            String subject,
            String description,
            Date startDateTime,
            Integer durationInMinutes,
            List<String> attendeeEmails,
            String applicantEmail,
            String applicantName
    ) throws Exception {

        try {
            GraphServiceClient client = getGraphClient();
            String serviceAccount = teamsProperties.getServiceAccountEmail();

            OffsetDateTime startOdt = startDateTime.toInstant().atZone(ZoneId.of("UTC")).toOffsetDateTime();
            OffsetDateTime endOdt = startOdt.plusMinutes(durationInMinutes);

            Event event = new Event();
            event.setSubject(subject);

            ItemBody body = new ItemBody();
            body.setContentType(BodyType.Html);
            body.setContent(description);
            event.setBody(body);

            DateTimeTimeZone start = new DateTimeTimeZone();
            start.setDateTime(startOdt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            start.setTimeZone("UTC");
            event.setStart(start);

            DateTimeTimeZone end = new DateTimeTimeZone();
            end.setDateTime(endOdt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            end.setTimeZone("UTC");
            event.setEnd(end);

            List<Attendee> attendees = new ArrayList<>();

            for (String email : attendeeEmails) {
                Attendee attendee = new Attendee();
                EmailAddress emailAddress = new EmailAddress();
                emailAddress.setAddress(email);
                attendee.setEmailAddress(emailAddress);
                attendee.setType(AttendeeType.Required);
                attendees.add(attendee);
            }

            if (applicantEmail != null && !applicantEmail.isEmpty()) {
                Attendee applicantAttendee = new Attendee();
                EmailAddress applicantEmailAddress = new EmailAddress();
                applicantEmailAddress.setAddress(applicantEmail);
                applicantEmailAddress.setName(applicantName);
                applicantAttendee.setEmailAddress(applicantEmailAddress);
                applicantAttendee.setType(AttendeeType.Required);
                attendees.add(applicantAttendee);
            }
            
            event.setAttendees(attendees);

            event.setIsOnlineMeeting(true);
            event.setOnlineMeetingProvider(OnlineMeetingProviderType.TeamsForBusiness);

            Event createdEvent = client
                    .users()
                    .byUserId(serviceAccount)
                    .events()
                    .post(event);

            return createdEvent;

        } catch (Exception e) {
            log.error("Error creating calendar event with Teams meeting: {}", e.getMessage(), e);
            throw new Exception("Failed to create calendar event with Teams meeting: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a Teams meeting event by ID.
     */
    public Event getEvent(String eventId) throws Exception {
        try {
            GraphServiceClient client = getGraphClient();
            String userEmail = teamsProperties.getServiceAccountEmail();
            Event event = client.users().byUserId(userEmail).events().byEventId(eventId).get();
            log.info("Retrieved event successfully: {}", eventId);
            return event;
        } catch (Exception e) {
            log.error("Error retrieving event {}: {}", eventId, e.getMessage(), e);
            throw new Exception("Failed to retrieve Teams meeting: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a Teams meeting event by ID.
     */
    public void deleteEvent(String eventId) throws Exception {
        try {
            GraphServiceClient client = getGraphClient();
            String userEmail = teamsProperties.getServiceAccountEmail();
            client.users().byUserId(userEmail).events().byEventId(eventId).delete();
            log.info("Deleted event successfully: {}", eventId);
        } catch (Exception e) {
            log.error("Error deleting event {}: {}", eventId, e.getMessage(), e);
            throw new Exception("Failed to delete Teams meeting: " + e.getMessage(), e);
        }
    }
}
