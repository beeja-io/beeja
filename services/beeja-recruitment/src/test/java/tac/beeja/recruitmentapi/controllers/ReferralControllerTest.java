package tac.beeja.recruitmentapi.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.service.ReferralService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferralControllerTest {

    @InjectMocks
    private ReferralController referralController;

    @Mock
    private ReferralService referralService;

    @Test
    void shouldCreateNewReferralSuccessfully() throws Exception {
        // Arrange
        ApplicantRequest request = new ApplicantRequest();
        Applicant applicant = new Applicant();
        when(referralService.newReferral(request)).thenReturn(applicant);

        // Act
        Applicant response = referralController.newReferral(request);

        // Assert
        assertEquals(applicant, response);
        verify(referralService).newReferral(request);
    }

    @Test
    void shouldGetAllMyReferralsSuccessfully() throws Exception {
        // Arrange
        List<Applicant> referrals = List.of(new Applicant(), new Applicant());
        when(referralService.getMyReferrals()).thenReturn(referrals);

        // Act
        List<Applicant> response = referralController.getAllMyReferrals();

        // Assert
        assertEquals(referrals.size(), response.size());
        assertEquals(referrals, response);
        verify(referralService).getMyReferrals();
    }

    @Test
    void shouldDownloadResumeSuccessfully() throws Exception {
        // Arrange
        String resumeId = "resume123";
        ByteArrayResource resource = new ByteArrayResource("sample-resume".getBytes());
        when(referralService.downloadFile(resumeId)).thenReturn(resource);

        // Act
        ByteArrayResource response = referralController.downloadResume(resumeId);

        // Assert
        assertEquals(resource, response);
        verify(referralService).downloadFile(resumeId);
    }
}
