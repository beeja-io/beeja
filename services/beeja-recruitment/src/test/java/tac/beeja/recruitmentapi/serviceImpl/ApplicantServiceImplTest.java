package tac.beeja.recruitmentapi.serviceImpl;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.bouncycastle.asn1.x509.X509ObjectIdentifiers.organization;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import tac.beeja.recruitmentapi.client.AccountClient;
import tac.beeja.recruitmentapi.client.FileClient;
import tac.beeja.recruitmentapi.enums.ApplicantStatus;
import tac.beeja.recruitmentapi.exceptions.*;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.model.AssignedInterviewer;
import tac.beeja.recruitmentapi.repository.ApplicantRepository;
import tac.beeja.recruitmentapi.request.AddCommentRequest;
import tac.beeja.recruitmentapi.request.ApplicantFeedbackRequest;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.request.FileRequest;
import tac.beeja.recruitmentapi.response.FileResponse;
import tac.beeja.recruitmentapi.utils.Constants;
import tac.beeja.recruitmentapi.utils.UserContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicantServiceImplTest {

    @InjectMocks
    private ApplicantServiceImpl applicantService;

    @Mock
    private FileClient fileClient;

    @Mock
    private ApplicantRepository applicantRepository;

    @Mock
    private AccountClient accountClient;

    @Captor
    ArgumentCaptor<Applicant> applicantCaptor;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private Applicant applicant;
    private Map<String, Object> orgData;


    @BeforeEach
    void setUp() {
        Map<String, Object> org = new HashMap<>();
        org.put("id", "org123");
        org.put("name", "TechCorp");
        UserContext.setLoggedInUserOrganization(org);
        UserContext.setLoggedInEmployeeId("emp001");
        UserContext.setLoggedInUserName("Alice Smith");
        applicant = new Applicant();
        applicant.setApplicantId("TEST1234");
        applicant.setAssignedInterviewers(Collections.singletonList(new AssignedInterviewer()));
    }

    @Test
    void testPostApplicant_SuccessfulCreationWithResumeUpload() throws Exception {
        // Arrange
        MockMultipartFile resume = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", "Test content".getBytes());
        ApplicantRequest request = new ApplicantRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhoneNumber("1234567890");
        request.setPositionAppliedFor("Software Engineer");
        request.setExperience("5 years");
        request.setResume(resume);

        Map<String, Object> fileUploadResponse = new HashMap<>();
        fileUploadResponse.put("id", "file123");

        when(fileClient.uploadFile(any(FileRequest.class)))
                .thenReturn((ResponseEntity) new ResponseEntity<>(fileUploadResponse, HttpStatus.OK));


        when(applicantRepository.countByOrganizationId("org123")).thenReturn(9L);
        when(applicantRepository.save(any(Applicant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Applicant result = applicantService.postApplicant(request, true);

        // Assert
        assertNotNull(result);
        assertEquals(ApplicantStatus.APPLIED, result.getStatus());
        assertEquals("file123", result.getResumeId());
        assertEquals("emp001", result.getReferredByEmployeeId());
        assertEquals("Alice Smith", result.getReferredByEmployeeName());
        verify(applicantRepository).save(applicantCaptor.capture());
        assertTrue(applicantCaptor.getValue().getApplicantId().startsWith("TEC"));
    }

    @Test
    void testPostApplicant_InvalidFileFormat_ThrowsBadRequest() {
        // Arrange
        MockMultipartFile resume = new MockMultipartFile(
                "file", "resume.txt", "text/plain", "Invalid format".getBytes());

        ApplicantRequest request = new ApplicantRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhoneNumber("1234567890");
        request.setPositionAppliedFor("QA Engineer");
        request.setExperience("3 years");
        request.setResume(resume);

        // Act + Assert
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> applicantService.postApplicant(request, false));

        assertEquals("Only PDF, DOC and DOCX files are allowed", ex.getMessage());
        verifyNoInteractions(fileClient);
        verifyNoInteractions(applicantRepository);
    }

    @Test
    void getAllApplicantsInOrganization_withPermission_returnsAllApplicants() {
        // Arrange
        String orgId = "org123";
        List<Applicant> applicants = List.of(new Applicant());

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserPermissions)
                    .thenReturn(Set.of(Constants.GET_ENTIRE_APPLICANTS));
            userContextMock.when(UserContext::getLoggedInUserOrganization)
                    .thenReturn(Map.of("id", orgId));
            when(applicantRepository.findAllByOrganizationId(orgId)).thenReturn(applicants);

            // Act
            List<Applicant> result = applicantService.getAllApplicantsInOrganization();

            // Assert
            assertEquals(applicants, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getApplicantById_returnsApplicant() throws Exception {
        // Arrange
        String applicantId = "app123";
        String orgId = "org123";
        Applicant applicant = new Applicant();

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", orgId));
            when(applicantRepository.findByIdAndOrganizationId(applicantId, orgId)).thenReturn(applicant);

            // Act
            Applicant result = applicantService.getApplicantById(applicantId);

            // Assert
            assertEquals(applicant, result);
        }
    }

    @Test
    void updateApplicant_updatesFieldsSuccessfully() throws Exception {
        // Arrange
        String applicantId = "app123";
        String orgId = "org123";
        Applicant applicant = new Applicant();
        applicant.setStatus(ApplicantStatus.APPLIED);

        Map<String, Object> fields = Map.of("status", "REJECTED");

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization)
                    .thenReturn(Map.of("id", orgId));
            when(applicantRepository.findByIdAndOrganizationId(applicantId, orgId)).thenReturn(applicant);
            when(applicantRepository.save(any(Applicant.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Applicant result = applicantService.updateApplicant(applicantId, fields);

            // Assert
            assertEquals(ApplicantStatus.REJECTED, result.getStatus());
        }
    }

    @Test
    void changeStatusOfApplicant_shouldUpdateStatus_whenValidStatus() throws Exception {
        // Arrange
        Applicant mockApplicant = new Applicant();
        mockApplicant.setApplicantId("APP001");
        mockApplicant.setStatus(ApplicantStatus.APPLIED);

        when(applicantRepository.findByIdAndOrganizationId("APP001", "org123"))
                .thenReturn(mockApplicant);
        when(applicantRepository.save(any(Applicant.class)))
                .thenReturn(mockApplicant);

        // Act
        Applicant updatedApplicant = applicantService.changeStatusOfApplicant("APP001", "HIRED");

        // Assert
        assertEquals(ApplicantStatus.HIRED, updatedApplicant.getStatus());
        verify(applicantRepository).save(mockApplicant);
    }

    @Test
    void addCommentToApplicant_addsNewComment() throws Exception {
        // Arrange
        AddCommentRequest request = new AddCommentRequest();
        request.setApplicantId("app123");
        request.setComment("New Comment");

        Applicant applicant = new Applicant();
        applicant.setApplicantComments(new ArrayList<>());

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org123"));
            userContextMock.when(UserContext::getLoggedInUserEmail).thenReturn("test@example.com");
            userContextMock.when(UserContext::getLoggedInUserName).thenReturn("John Doe");

            when(applicantRepository.findByIdAndOrganizationId(anyString(), anyString())).thenReturn(applicant);
            when(applicantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Applicant result = applicantService.addCommentToApplicant(request);

            // Assert
            assertEquals(1, result.getApplicantComments().size());
        }
    }

    @Test
    void deleteInterviewerByInterviewID_removesInterviewer() throws Exception {
        // Arrange
        String applicantId = "app123";
        String orgId = "org123";
        String interviewId = "INT123";

        AssignedInterviewer ai = new AssignedInterviewer();
        ai.setInterviewId(interviewId);

        Applicant applicant = new Applicant();
        applicant.setAssignedInterviewers(new ArrayList<>(List.of(ai)));

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", orgId));
            when(applicantRepository.findByIdAndOrganizationId(applicantId, orgId)).thenReturn(applicant);
            when(applicantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Applicant result = applicantService.deleteInterviewerByInterviewID(applicantId, interviewId);

            // Assert
            assertTrue(result.getAssignedInterviewers().isEmpty());
        }
    }

    @Test
    void changeStatusOfApplicant_invalidStatus_throwsBadRequest() {
        // Arrange
        when(applicantRepository.findByIdAndOrganizationId("APP001", "org123"))
                .thenReturn(new Applicant());

        // Act + Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> applicantService.changeStatusOfApplicant("APP001", "UNKNOWN"));

        assertEquals("BAD_REQUEST,INVALID_APPLICANT_STATUS,Invalid applicant status provided: UNKNOWN", exception.getMessage());
        verify(applicantRepository, never()).save(any());
    }

    @Test
    void deleteInterviewerByInterviewID_whenInterviewIdNotFound_doesNothing() throws Exception {
        // Arrange
        String applicantId = "app123";
        String orgId = "org123";
        String interviewId = "INT999"; // not present

        AssignedInterviewer ai = new AssignedInterviewer();
        ai.setInterviewId("INT123");

        Applicant applicant = new Applicant();
        applicant.setAssignedInterviewers(new ArrayList<>(List.of(ai)));

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", orgId));
            when(applicantRepository.findByIdAndOrganizationId(applicantId, orgId)).thenReturn(applicant);
            when(applicantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Applicant result = applicantService.deleteInterviewerByInterviewID(applicantId, interviewId);

            // Assert
            assertEquals(1, result.getAssignedInterviewers().size());
            assertEquals("INT123", result.getAssignedInterviewers().get(0).getInterviewId());
        }
    }

    @Test
    void testPostApplicant_whenFileUploadFails_throwsException() {
        // Arrange
        MockMultipartFile resume = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", "Test content".getBytes());

        ApplicantRequest request = new ApplicantRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhoneNumber("1234567890");
        request.setPositionAppliedFor("Software Engineer");
        request.setExperience("5 years");
        request.setResume(resume);

        when(fileClient.uploadFile(any(FileRequest.class)))
                .thenThrow(new RuntimeException("Upload failed"));

        // Act + Assert
        assertThrows(RuntimeException.class, () -> applicantService.postApplicant(request, true));
    }

    @Test
    void deleteInterviewerByInterviewID_removesCorrectInterviewer_whenMultipleInterviewers() throws Exception {
        // Arrange
        String applicantId = "app123";
        String interviewIdToDelete = "INT123";
        String interviewIdToKeep = "INT124";

        AssignedInterviewer ai1 = new AssignedInterviewer();
        ai1.setInterviewId(interviewIdToDelete);

        AssignedInterviewer ai2 = new AssignedInterviewer();
        ai2.setInterviewId(interviewIdToKeep);

        Applicant applicant = new Applicant();
        applicant.setAssignedInterviewers(new ArrayList<>(List.of(ai1, ai2)));

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org123"));
            when(applicantRepository.findByIdAndOrganizationId(applicantId, "org123")).thenReturn(applicant);
            when(applicantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Applicant result = applicantService.deleteInterviewerByInterviewID(applicantId, interviewIdToDelete);

            // Assert
            assertEquals(1, result.getAssignedInterviewers().size());
            assertEquals(interviewIdToKeep, result.getAssignedInterviewers().get(0).getInterviewId());
        }
    }


    @Test
    void assignInterviewer_interviewerAlreadyAssigned_throwsException() throws Exception {
        // Arrange
        String applicantId = "app123";
        AssignedInterviewer assignedInterviewer = new AssignedInterviewer();
        assignedInterviewer.setEmployeeId("employee123");

        Applicant applicant = new Applicant();
        applicant.setAssignedInterviewers(Collections.singletonList(assignedInterviewer));

        when(applicantRepository.findById(applicantId)).thenReturn(Optional.of(applicant));

        // Act + Assert
        assertThrows(BadRequestException.class, () -> applicantService.assignInterviewer(applicantId, assignedInterviewer));
    }

    @Test
    void assignInterviewer_success() throws Exception {
        // Arrange
        String applicantId = "app123";
        AssignedInterviewer assignedInterviewer = new AssignedInterviewer();
        assignedInterviewer.setEmployeeId("employee123");

        Applicant applicant = new Applicant();
        applicant.setAssignedInterviewers(new ArrayList<>());

        when(applicantRepository.findById(applicantId)).thenReturn(Optional.of(applicant));
        ResponseEntity<Boolean> employeeResponse = ResponseEntity.ok(true);
        when(accountClient.isEmployeeHasPermission("employee123", Constants.TAKE_INTERVIEW))
                .thenReturn(employeeResponse);

        // Mock the UserContext
        Map<String, Object> orgMap = new HashMap<>();
        orgMap.put("name", "TestOrg");
        UserContext.setLoggedInUserOrganization(orgMap);

        // Mock save
        when(applicantRepository.save(any(Applicant.class))).thenReturn(applicant);

        // Act
        Applicant result = null;
        try {
            result = applicantService.assignInterviewer(applicantId, assignedInterviewer);
        } catch (Exception e) {
            e.printStackTrace(); // Print the exception to see if save failed.
            fail("Exception thrown during assignInterviewer: " + e.getMessage());
        }

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getAssignedInterviewers().size());
        assertEquals("employee123", result.getAssignedInterviewers().get(0).getEmployeeId());
    }

    @Test
    void getApplicantById_whenRepositoryThrowsException_wrapsException() {
        // Arrange
        String applicantId = "app123";
        String orgId = "org123";

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization)
                    .thenReturn(Map.of("id", orgId));

            when(applicantRepository.findByIdAndOrganizationId(applicantId, orgId))
                    .thenThrow(new RuntimeException("DB error"));

            // Act & Assert
            Exception ex = assertThrows(Exception.class, () -> applicantService.getApplicantById(applicantId));
            assertTrue(ex.getMessage().contains("Error in getting applicant by ID"));
        }
    }



    @Test
    void updateApplicant_withEmptyFields_returnsSameApplicant() throws Exception {
        // Arrange
        String applicantId = "app123";
        String orgId = "org123";
        Applicant applicant = new Applicant();
        applicant.setStatus(ApplicantStatus.APPLIED);

        Map<String, Object> fields = Collections.emptyMap();

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization)
                    .thenReturn(Map.of("id", orgId));
            when(applicantRepository.findByIdAndOrganizationId(applicantId, orgId))
                    .thenReturn(applicant);
            when(applicantRepository.save(any(Applicant.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            Applicant result = applicantService.updateApplicant(applicantId, fields);

            // Assert
            assertEquals(ApplicantStatus.APPLIED, result.getStatus()); // Still same
        }
    }

    @Test
    void deleteInterviewerByInterviewID_success() throws Exception{
        Applicant applicant = new Applicant();
        AssignedInterviewer interviewer = new AssignedInterviewer();
        interviewer.setInterviewId("int123");
        applicant.setAssignedInterviewers(new ArrayList<>(List.of(interviewer)));
        when(applicantRepository.findByIdAndOrganizationId(anyString(), anyString())).thenReturn(applicant);
        when(applicantRepository.save(any(Applicant.class))).thenAnswer(i -> i.getArguments()[0]);

        Applicant result = applicantService.deleteInterviewerByInterviewID("app123", "int123");

        assertNotNull(result);
        assertTrue(result.getAssignedInterviewers().isEmpty());
    }

    @Test
    void downloadFile_FileClientGetFileByIdFailure() throws Exception {
        when(fileClient.getFileById("file123")).thenThrow(new RuntimeException("File not found"));

        assertThrows(FeignClientException.class, () -> applicantService.downloadFile("file123"));
    }

    @Test
    void submitFeedback_Success() {
        ApplicantFeedbackRequest feedbackRequest = new ApplicantFeedbackRequest();
        feedbackRequest.setFeedback("Good");

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), Mockito.eq(Applicant.class))).thenReturn(applicant);

        Applicant result = applicantService.submitFeedback("TEST1234", feedbackRequest);

        assertNotNull(result);
        assertEquals("TEST1234", result.getApplicantId());
    }

    @Test
    void submitFeedback_ApplicantNotFound() {
        ApplicantFeedbackRequest feedbackRequest = new ApplicantFeedbackRequest();
        feedbackRequest.setFeedback("Good");

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), Mockito.eq(Applicant.class))).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> applicantService.submitFeedback("TEST1234", feedbackRequest));
    }



    @Test
    void assignInterviewer_applicantNotFound_throwsResourceNotFoundException() {
        AssignedInterviewer interviewer = new AssignedInterviewer();
        interviewer.setEmployeeId("interviewerId");

        when(applicantRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> applicantService.assignInterviewer("applicantId", interviewer));
    }

    @Test
    void assignInterviewer_interviewerAlreadyAssigned_throwsBadRequestException() throws Exception {
        AssignedInterviewer interviewer = new AssignedInterviewer();
        interviewer.setEmployeeId("interviewerId");
        applicant.setAssignedInterviewers(Collections.singletonList(interviewer));

        when(applicantRepository.findById(anyString())).thenReturn(Optional.of(applicant));

        assertThrows(BadRequestException.class, () -> applicantService.assignInterviewer("applicantId", interviewer));
    }

    @Test
    void getApplicantById_failure_throwsException() throws Exception {
        when(applicantRepository.findByIdAndOrganizationId(anyString(), anyString())).thenThrow(new RuntimeException("Get by id failed"));

        assertThrows(Exception.class, () -> applicantService.getApplicantById("applicantId"));
    }

}



