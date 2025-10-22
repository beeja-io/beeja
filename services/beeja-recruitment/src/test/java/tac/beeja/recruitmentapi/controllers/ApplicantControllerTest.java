package tac.beeja.recruitmentapi.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.model.AssignedInterviewer;
import tac.beeja.recruitmentapi.request.AddCommentRequest;
import tac.beeja.recruitmentapi.request.ApplicantFeedbackRequest;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.service.ApplicantService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicantControllerTest {

    @InjectMocks
    private ApplicantController applicantController;

    @Mock
    private ApplicantService applicantService;

    @Mock
    private BindingResult bindingResult;

    @Test
    void shouldPostApplicantSuccessfully() throws Exception {
        // Arrange
        ApplicantRequest request = new ApplicantRequest();
        Applicant applicant = new Applicant();
        when(applicantService.postApplicant(request, false)).thenReturn(applicant);

        // Act
        ResponseEntity<Applicant> response = applicantController.postApplicant(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicant, response.getBody());
        verify(applicantService).postApplicant(request, false);
    }

    @Test
    void shouldGetAllApplicants() throws Exception {
        // Arrange
        List<Applicant> mockApplicants = List.of(new Applicant(), new Applicant());
        when(applicantService.getAllApplicantsInOrganization()).thenReturn(mockApplicants);

        // Act
        ResponseEntity<List<Applicant>> response = applicantController.getAllApplicants();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockApplicants, response.getBody());
        verify(applicantService).getAllApplicantsInOrganization();
    }

    @Test
    void shouldUpdateApplicantSuccessfully() throws Exception {
        // Arrange
        String applicantId = "app123";
        Map<String, Object> fields = Map.of("field", "value");
        Applicant updatedApplicant = new Applicant();
        when(applicantService.updateApplicant(applicantId, fields)).thenReturn(updatedApplicant);

        // Act
        ResponseEntity<Applicant> response = applicantController.updateApplicant(applicantId, fields);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedApplicant, response.getBody());
        verify(applicantService).updateApplicant(applicantId, fields);
    }

    @Test
    void shouldDownloadFileSuccessfully() throws Exception {
        // Arrange
        String fileId = "file123";
        ByteArrayResource resource = new ByteArrayResource("sample".getBytes()) {
            @Override
            public String getFilename() {
                return "resume.pdf";
            }
        };
        when(applicantService.downloadFile(fileId)).thenReturn(resource);

        // Act
        ResponseEntity<?> response = applicantController.downloadFile(fileId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().get("Content-Disposition").get(0).contains("resume.pdf"));
        assertEquals(resource, response.getBody());
        verify(applicantService).downloadFile(fileId);
    }

    @Test
    void shouldSubmitFeedbackSuccessfully() throws Exception {
        // Arrange
        String applicantID = "app456";
        ApplicantFeedbackRequest request = new ApplicantFeedbackRequest();
        Applicant applicant = new Applicant();
        when(applicantService.submitFeedback(applicantID, request)).thenReturn(applicant);

        // Act
        ResponseEntity<Applicant> response = applicantController.submitFeedBack(applicantID, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicant, response.getBody());
        verify(applicantService).submitFeedback(applicantID, request);
    }

    @Test
    void shouldAssignInterviewerSuccessfullyWhenNoValidationErrors() throws Exception {
        // Arrange
        String applicantID = "app789";
        AssignedInterviewer assignedInterviewer = new AssignedInterviewer();
        Applicant applicant = new Applicant();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(applicantService.assignInterviewer(applicantID, assignedInterviewer)).thenReturn(applicant);

        // Act
        ResponseEntity<?> response = applicantController.assignInterviewer(applicantID, assignedInterviewer, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicant, response.getBody());
        verify(applicantService).assignInterviewer(applicantID, assignedInterviewer);
    }

    @Test
    void shouldReturnBadRequestWhenAssignInterviewerValidationFails() throws Exception {
        // Arrange
        String applicantID = "app789";
        AssignedInterviewer assignedInterviewer = new AssignedInterviewer();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // Act
        ResponseEntity<?> response = applicantController.assignInterviewer(applicantID, assignedInterviewer, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(applicantService, never()).assignInterviewer(anyString(), any());
    }

    @Test
    void shouldGetApplicantById() throws Exception {
        // Arrange
        String applicantID = "app101";
        Applicant applicant = new Applicant();
        when(applicantService.getApplicantById(applicantID)).thenReturn(applicant);

        // Act
        ResponseEntity<Applicant> response = applicantController.getApplicantById(applicantID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicant, response.getBody());
        verify(applicantService).getApplicantById(applicantID);
    }

    @Test
    void shouldDeleteInterviewerByInterviewId() throws Exception {
        // Arrange
        String applicantID = "app102";
        String interviewID = "intv202";
        Applicant applicant = new Applicant();
        when(applicantService.deleteInterviewerByInterviewID(applicantID, interviewID)).thenReturn(applicant);

        // Act
        ResponseEntity<Applicant> response = applicantController.deleteInterviewerByInterviewID(applicantID, interviewID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicant, response.getBody());
        verify(applicantService).deleteInterviewerByInterviewID(applicantID, interviewID);
    }

    @Test
    void shouldAddCommentToApplicantSuccessfully() throws Exception {
        // Arrange
        AddCommentRequest commentRequest = new AddCommentRequest();
        Applicant applicant = new Applicant(); // Create a proper Applicant object
        when(bindingResult.hasErrors()).thenReturn(false);
        when(applicantService.addCommentToApplicant(commentRequest)).thenReturn(applicant);

        // Act
        ResponseEntity<?> response = applicantController.addCommentToApplicant(commentRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicant, response.getBody());
        verify(applicantService).addCommentToApplicant(commentRequest);
    }


    @Test
    void shouldReturnBadRequestWhenCommentValidationFails() throws Exception {
        // Arrange
        AddCommentRequest commentRequest = new AddCommentRequest();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // Act
        ResponseEntity<?> response = applicantController.addCommentToApplicant(commentRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(applicantService, never()).addCommentToApplicant(any());
    }


}
