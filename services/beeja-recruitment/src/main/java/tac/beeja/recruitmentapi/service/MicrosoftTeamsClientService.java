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
     * Creates a Microsoft Teams meeting as a calendar event with Teams link.
     */
    public OnlineMeeting createTeamsMeeting(
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

            // Create meeting object
            OnlineMeeting onlineMeeting = new OnlineMeeting();
            onlineMeeting.setSubject(subject);
            onlineMeeting.setStartDateTime(startOdt);
            onlineMeeting.setEndDateTime(endOdt);

            // Optional: set lobby and permissions
            onlineMeeting.setLobbyBypassSettings(new LobbyBypassSettings() {{
                setScope(LobbyBypassScope.Organization);
            }});

            // Prepare participants
            List<MeetingParticipantInfo> attendees = new ArrayList<>();

            if (attendeeEmails != null) {
                for (String email : attendeeEmails) {
                    IdentitySet identitySet = new IdentitySet();
                    identitySet.setUser(new Identity() {{
                        setDisplayName(email);
                    }});

                    MeetingParticipantInfo participant = new MeetingParticipantInfo();
                    participant.setRole(OnlineMeetingRole.Presenter);
                    participant.setIdentity(identitySet);

                    attendees.add(participant);
                }
            }

            if (applicantEmail != null && !applicantEmail.isBlank()) {
                IdentitySet applicantIdentity = new IdentitySet();
                applicantIdentity.setUser(new Identity() {{
                    setDisplayName(applicantName != null ? applicantName : applicantEmail);
                }});

                MeetingParticipantInfo applicant = new MeetingParticipantInfo();
                applicant.setRole(OnlineMeetingRole.Attendee);
                applicant.setIdentity(applicantIdentity);

                attendees.add(applicant);
            }

            MeetingParticipants participants = new MeetingParticipants();
            participants.setAttendees(attendees);
            onlineMeeting.setParticipants(participants);

            log.info("Creating Teams meeting for account: {}", serviceAccount);

            // ✅ Actual call to Microsoft Graph to create Teams meeting
            OnlineMeeting createdMeeting = client
                    .users()
                    .byUserId(serviceAccount)
                    .onlineMeetings()
                    .post(onlineMeeting);

            log.info("Teams meeting created successfully. Subject: {}, Join URL: {}",
                    subject,
                    createdMeeting.getJoinWebUrl());

            return createdMeeting;

        } catch (Exception e) {
            log.error("Error creating Teams meeting: {}", e.getMessage(), e);
            throw new Exception("Failed to create Microsoft Teams meeting: " + e.getMessage(), e);
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
