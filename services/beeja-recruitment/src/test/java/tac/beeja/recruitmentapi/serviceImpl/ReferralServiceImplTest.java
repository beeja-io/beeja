package tac.beeja.recruitmentapi.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tac.beeja.recruitmentapi.client.FileClient;
import tac.beeja.recruitmentapi.exceptions.FeignClientException;
import tac.beeja.recruitmentapi.exceptions.UnAuthorisedException;
import tac.beeja.recruitmentapi.model.Applicant;
import tac.beeja.recruitmentapi.repository.ApplicantRepository;
import tac.beeja.recruitmentapi.request.ApplicantRequest;
import tac.beeja.recruitmentapi.response.FileDownloadResultMetaData;
import tac.beeja.recruitmentapi.response.FileResponse;
import tac.beeja.recruitmentapi.service.ApplicantService;
import tac.beeja.recruitmentapi.utils.UserContext;
import org.springframework.mock.web.MockMultipartFile;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferralServiceImplTest {

    @InjectMocks
    private ReferralServiceImpl referralService;

    @Mock
    private ApplicantRepository applicantRepository;

    @Mock
    private ApplicantService applicantService;

    @Mock
    private FileClient fileClient;


    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        Map<String, Object> orgMap = Map.of("id", "org123");
        UserContext.setLoggedInEmployeeId("emp456");
        UserContext.setLoggedInUserOrganization(orgMap);
    }

    @Test
    void shouldCreateNewReferralSuccessfully() throws Exception {
        // Arrange
        ApplicantRequest request = new ApplicantRequest();
        Applicant applicant = new Applicant();
        when(applicantService.postApplicant(request, true)).thenReturn(applicant);

        // Act
        Applicant result = referralService.newReferral(request);

        // Assert
        assertEquals(applicant, result);
        verify(applicantService).postApplicant(request, true);
    }

    @Test
    void shouldGetMyReferralsSuccessfully() throws Exception {
        // Arrange
        List<Applicant> referrals = List.of(new Applicant(), new Applicant());
        when(applicantRepository.findByReferredByEmployeeIdAndOrganizationId("emp456", "org123"))
                .thenReturn(referrals);

        // Act
        List<Applicant> result = referralService.getMyReferrals();

        // Assert
        assertEquals(referrals, result);
        verify(applicantRepository).findByReferredByEmployeeIdAndOrganizationId("emp456", "org123");
    }



    @Test
    void shouldExtractMetaDataFromResponseHeadersCorrectly() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"testfile.txt\"");
        headers.add("createdby", "admin");
        headers.add("organizationid", "org1");
        headers.add("entityId", "entity42");

        ResponseEntity<byte[]> response = new ResponseEntity<>(new byte[0], headers, 200);

        // Act
        FileDownloadResultMetaData metaData = ReferralServiceImpl.getMetaData(response);

        // Assert
        assertEquals("testfile.txt", metaData.getFileName());
        assertEquals("admin", metaData.getCreatedBy());
        assertEquals("org1", metaData.getOrganizationId());
        assertEquals("entity42", metaData.getEntityId());
    }

    @Test
    void shouldThrowExceptionWhenPostApplicantFails() throws Exception {
        ApplicantRequest request = new ApplicantRequest();
        when(applicantService.postApplicant(request, true)).thenThrow(new RuntimeException("Service down"));

        Exception exception = assertThrows(Exception.class, () -> referralService.newReferral(request));
        assertEquals("Service down", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFetchingMyReferralsFails() {
        when(applicantRepository.findByReferredByEmployeeIdAndOrganizationId("emp456", "org123"))
                .thenThrow(new RuntimeException("DB error"));

        Exception exception = assertThrows(Exception.class, () -> referralService.getMyReferrals());
        assertEquals("DB error", exception.getMessage());
    }


    @Test
    void shouldThrowFeignClientExceptionWhenFileByIdFails() {
        // Arrange
        String fileId = "file123";
        when(fileClient.getFileById(fileId)).thenThrow(new RuntimeException("File fetch error"));

        // Act + Assert
        Exception exception = assertThrows(FeignClientException.class, () -> referralService.downloadFile(fileId));
        assertEquals("File fetch error", exception.getMessage());
    }


    @Test
    void downloadFile_fileClientGetFileByIdException() throws Exception {
        String fileId = "testFileId";
        when(fileClient.getFileById(fileId)).thenThrow(new RuntimeException("FileClient getFileById failed"));

        assertThrows(FeignClientException.class, () -> referralService.downloadFile(fileId));

        verify(fileClient, times(1)).getFileById(fileId);
        verify(fileClient, never()).downloadFile(fileId);
    }
    

    @Test
    void getMetaData_shouldHandleMissingFilenameGracefully() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;");
        headers.add("createdby", "admin");
        headers.add("organizationid", "org1");
        headers.add("entityId", "entity42");

        ResponseEntity<byte[]> response = new ResponseEntity<>(new byte[0], headers, 200);

        FileDownloadResultMetaData metaData = ReferralServiceImpl.getMetaData(response);

        assertNull(metaData.getFileName());
        assertEquals("admin", metaData.getCreatedBy());
        assertEquals("org1", metaData.getOrganizationId());
        assertEquals("entity42", metaData.getEntityId());
    }


    @Test
    void newReferral_shouldCopyFieldsAndCallPostApplicant() throws Exception {
        // Arrange
        MockMultipartFile mockResume = new MockMultipartFile("resume", "resume.pdf", "application/pdf", "dummy-content".getBytes());

        ApplicantRequest request = new ApplicantRequest();
        request.setEmail("john@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("1234567890");
        request.setPositionAppliedFor("Developer");
        request.setResume(mockResume);
        request.setExperience("5 years");

        Applicant expectedApplicant = new Applicant();
        when(applicantService.postApplicant(any(ApplicantRequest.class), eq(true)))
                .thenReturn(expectedApplicant);

        // Act
        Applicant result = referralService.newReferral(request);

        // Assert
        assertEquals(expectedApplicant, result);
        verify(applicantService).postApplicant(argThat(arg ->
                request.getEmail().equals(arg.getEmail()) &&
                        request.getFirstName().equals(arg.getFirstName()) &&
                        request.getResume().getOriginalFilename().equals(arg.getResume().getOriginalFilename())
        ), eq(true));
    }

    @Test
    void downloadFile_shouldDownloadFileSuccessfully() throws Exception {
        String fileId = "testFileId";
        FileResponse fileResponse = new FileResponse();
        fileResponse.setEntityType("resume");

        LinkedHashMap<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("entityType", "resume");

        HttpHeaders downloadHeaders = new HttpHeaders();
        downloadHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.pdf\"");
        downloadHeaders.add("createdby", "admin");
        downloadHeaders.add("organizationid", "org1");
        downloadHeaders.add("entityId", "entity42");

        ResponseEntity<byte[]> downloadResponse = new ResponseEntity<>("test content".getBytes(), downloadHeaders, HttpStatus.OK);
        ResponseEntity<?> getFileResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

       // when(fileClient.getFileById(fileId)).thenReturn(getFileResponse);
        when(objectMapper.convertValue(anyMap(), eq(FileResponse.class))).thenReturn(fileResponse);
        when(fileClient.downloadFile(fileId)).thenReturn(downloadResponse);

        ByteArrayResource result = referralService.downloadFile(fileId);

        assertNotNull(result);
        assertEquals("test.pdf", result.getFilename());
        assertEquals("test content", new String(result.getByteArray()));

        verify(fileClient).getFileById(fileId);
        verify(fileClient).downloadFile(fileId);
    }

    @Test
    void downloadFile_shouldThrowFeignClientExceptionWhenFileByIdFails() throws Exception {
        String fileId = "testFileId";
        when(fileClient.getFileById(fileId)).thenThrow(new RuntimeException("FileClient getFileById failed"));

        assertThrows(FeignClientException.class, () -> referralService.downloadFile(fileId));

        verify(fileClient).getFileById(fileId);
        verify(fileClient, never()).downloadFile(fileId);
    }




}
