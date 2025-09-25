package tac.beeja.recruitmentapi.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
        applicant.setStatus(ApplicantStatus.APPLIED);
    }

    @Test
    void testPostApplicant_SuccessfulCreationWithResumeUpload() throws Exception {
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

        Applicant result = applicantService.postApplicant(request, true);

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

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> applicantService.postApplicant(request, false));

        assertEquals("Only PDF, DOC and DOCX files are allowed", ex.getMessage());
        verifyNoInteractions(fileClient);
        verifyNoInteractions(applicantRepository);
    }

    @Test
    void getAllApplicantsInOrganization_withPermission_returnsAllApplicants() {
        String orgId = "org123";
        List<Applicant> applicants = List.of(new Applicant());

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserPermissions)
                    .thenReturn(Set.of(Constants.GET_ENTIRE_APPLICANTS));
            userContextMock.when(UserContext::getLoggedInUserOrganization)
                    .thenReturn(Map.of("id", orgId));
            when(applicantRepository.findAllByOrganizationId(orgId)).thenReturn(applicants);

            List<Applicant> result = applicantService.getAllApplicantsInOrganization();

            assertEquals(applicants, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getApplicantById_returnsApplicant() throws Exception {
        String applicantId = "app123";
        String orgId = "org123";
        Applicant applicant = new Applicant();

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", orgId));
            when(applicantRepository.findByIdAndOrganizationId(applicantId, orgId)).thenReturn(applicant);

            Applicant result = applicantService.getApplicantById(applicantId);

            assertEquals(applicant, result);
        }
    }

}
