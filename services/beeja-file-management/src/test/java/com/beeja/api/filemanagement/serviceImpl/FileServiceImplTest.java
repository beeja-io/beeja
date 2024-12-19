package com.beeja.api.filemanagement.serviceImpl;

import com.google.cloud.storage.*;
import com.beeja.api.filemanagement.config.properties.AllowedContentTypes;
import com.beeja.api.filemanagement.config.properties.GCSProperties;
import com.beeja.api.filemanagement.exceptions.FileNotFoundException;
import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.repository.FileRepository;
import com.beeja.api.filemanagement.requests.FileUploadRequest;
import com.beeja.api.filemanagement.response.FileDownloadResult;
import com.beeja.api.filemanagement.response.FileResponse;
import com.beeja.api.filemanagement.utils.Constants;
import com.beeja.api.filemanagement.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("local")
public class FileServiceImplTest {
    @InjectMocks
    private FileServiceImpl fileServiceImpl;


    @Mock
    private FileRepository fileRepository;


    @Mock
    private File file;
    @Mock
    private GCSProperties gcsProperties;
    @Mock
    private GCSProperties.Bucket bucket;

    @Mock
    private Bucket bucket1;

    @Mock
    private Storage storage;

    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private AllowedContentTypes allowedContentTypes;


    @Mock
    private Blob blob;

    private FileUploadRequest fileUploadRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lenient().when(gcsProperties.getBucket()).thenReturn(bucket); // Mocking bucket
        lenient().when(bucket.getName()).thenReturn("bucket-name");
        lenient().when(allowedContentTypes.getAllowedTypes()).thenReturn(new String[]{"application/pdf", "image/png"});
    }

    @Test
    void uploadFile_Success() throws Exception {

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = Map.of("id", "org1");

            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);
            mockedUserContext.when(UserContext::getLoggedInEmployeeId).thenReturn("user1");
            mockedUserContext.when(UserContext::getLoggedInUserName).thenReturn("User One");
            MultipartFile mockFile = mock(MultipartFile.class);
            when(mockFile.getContentType()).thenReturn("application/pdf");
            when(mockFile.getSize()).thenReturn(1024L);
            when(mockFile.getOriginalFilename()).thenReturn("testFile.pdf");
            when(mockFile.getBytes()).thenReturn(new byte[1024]);

            fileUploadRequest = new FileUploadRequest();
            fileUploadRequest.setFile(mockFile);
            fileUploadRequest.setName("testFile");
            fileUploadRequest.setEntityId("123");
            fileUploadRequest.setEntityType("employee");
            fileUploadRequest.setDescription("Test file upload");

            File savedFile = new File();
            savedFile.setId("123");
            savedFile.setOrganizationId("org1");
            savedFile.setFileType("General");
            when(fileRepository.save(any(File.class))).thenReturn(savedFile);

            BlobId mockBlobId = BlobId.of("bucket-name", "organizations/org1/employee/123/General/123");
            BlobInfo mockBlobInfo = BlobInfo.newBuilder(mockBlobId).setContentType("application/pdf").build();
            Blob mockBlob = mock(Blob.class);

            when(gcsProperties.getBucket()).thenReturn(bucket);
            when(bucket.getName()).thenReturn("bucket-name");
            when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(mockBlob);

            File result = fileServiceImpl.uploadFile(fileUploadRequest);

            assertNotNull(result, "Resulting file object should not be null");
            assertEquals("123", result.getId(), "File ID should match");
            assertEquals("org1", result.getOrganizationId());

            verify(fileRepository, times(1)).save(any(File.class));
            verify(storage, times(1)).create(any(BlobInfo.class), any(byte[].class));
            verify(mockFile, times(1)).getSize();
            verify(mockFile, times(1)).getOriginalFilename();
            verify(mockFile, times(1)).getBytes();
        }
    }

    @Test
    public void updateFile_Success() throws Exception {
        String fileId = "123";
        String organizationId = "org1";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                new byte[1024]
        );

        FileUploadRequest fileUploadRequest = mock(FileUploadRequest.class);
        when(fileUploadRequest.getFile()).thenReturn(mockFile); // Mock the file

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = new HashMap<>();
            mockedOrganization.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);

            File fileToUpdate = new File();
            fileToUpdate.setId(fileId);
            fileToUpdate.setOrganizationId(organizationId);
            when(fileRepository.findByOrganizationIdAndId(organizationId, fileId)).thenReturn(fileToUpdate);


            when(storage.get(eq("bucket-name"), anyString())).thenReturn(blob);

            when(fileRepository.save(any(File.class))).thenReturn(fileToUpdate);
            File updatedFile = fileServiceImpl.updateFile(fileId, fileUploadRequest);

            assertNotNull(updatedFile);
            assertEquals(fileId, updatedFile.getId());
            assertEquals(organizationId, updatedFile.getOrganizationId());
            verify(fileRepository, times(1)).save(any(File.class));
            verify(blob, times(1)).delete();
        }
    }

    @Test
    void uploadOrUpdateFile_FileDoesNotExist_ShouldUploadFile() throws Exception {
        String entityId = "entityId1";
        String fileType = "image/png";
        String organizationId = "org1";
        String entityType = "expense";

        Map<String, String> mockedOrganization = new HashMap<>();
        mockedOrganization.put("id", organizationId);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "profilePic.png", "image/png", "mock file content".getBytes());

        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(mockFile);
        fileUploadRequest.setEntityId(entityId);
        fileUploadRequest.setFileType(fileType);
        fileUploadRequest.setEntityType(entityType);
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);
            when(fileRepository.findByEntityIdAndFileTypeAndOrganizationId(entityId, fileType, organizationId))
                    .thenReturn(null);
            when(fileRepository.save(any(File.class))).thenAnswer(invocation -> {
                File savedFile = invocation.getArgument(0);
                savedFile.setId("fileId123");
                return savedFile;
            });
            Blob mockBlob = mock(Blob.class);
            when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(mockBlob);
            File result = fileServiceImpl.uploadOrUpdateFile(fileUploadRequest);
            assertNotNull(result, "Result file should not be null");
            assertEquals("fileId123", result.getId());
            assertEquals(organizationId, result.getOrganizationId());
            assertEquals(entityType, result.getEntityType());
            assertEquals(fileType, result.getFileType());
            verify(fileRepository, times(1))
                    .findByEntityIdAndFileTypeAndOrganizationId(entityId, fileType, organizationId);
            verify(storage, times(1)).create(any(BlobInfo.class), any(byte[].class));
        }
    }


    @Test
    void uploadFile_ShouldThrowGenericExceptionWhenUnhandledErrorOccurs() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "testFile.pdf", "application/pdf", "dummy data".getBytes());
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(mockFile);
        fileUploadRequest.setEntityId("entityId");
        fileUploadRequest.setFileType("TestType");

        Map<String, String> mockedOrganization = new HashMap<>();
        mockedOrganization.put("id", "org1");

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);

            when(fileRepository.save(any(File.class))).thenThrow(new RuntimeException("Unexpected error"));
            Exception exception = assertThrows(Exception.class, () -> {
                fileServiceImpl.uploadFile(fileUploadRequest);
            });
            assertEquals("Unexpected error", exception.getMessage());
        }
    }

    @Test
    void testListofFileByEntityId_Success() throws Exception {
        String entityId = "testEntityId";
        String organizationId = "org1";
        int page = 1;
        int size = 10;
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = new HashMap<>();
            mockedOrganization.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);

            File file1 = new File();
            file1.setId("file1");
            file1.setName("Test File 1");
            file1.setEntityId(entityId);
            file1.setOrganizationId(organizationId);

            File file2 = new File();
            file2.setId("file2");
            file2.setName("Test File 2");
            file2.setEntityId(entityId);
            file2.setOrganizationId(organizationId);

            List<File> fileList = Arrays.asList(file1, file2);

            AggregationResults<File> aggregationResults = mock(AggregationResults.class);
            when(aggregationResults.getMappedResults()).thenReturn(fileList);


            when(mongoTemplate.aggregate(any(Aggregation.class), eq(File.class), eq(File.class)))
                    .thenReturn(aggregationResults);

            Query query = new Query();
            query.addCriteria(Criteria.where("entityId").is(entityId));
            query.addCriteria(Criteria.where("organizationId").is(organizationId));
            when(mongoTemplate.count(any(Query.class), eq(File.class))).thenReturn(2L);
            FileResponse response = fileServiceImpl.listofFileByEntityId(entityId, page, size);
            assertNotNull(response);
            assertEquals(2, response.getFiles().size());
            assertEquals(2L, response.getMetadata().get("totalSize"));
            assertEquals("file1", response.getFiles().get(0).getId());
            assertEquals("Test File 1", response.getFiles().get(0).getName());
        }
    }

    @Test
    void testDownloadFile_Success() throws Exception {

        String fileId = "123";
        String organizationId = "org123";
        String bucketName = "my-bucket";
        String generatedPath = "organizations/org123/employee/123/png/123";
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = new HashMap<>();
            mockedOrganization.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);

            when(fileRepository.findByOrganizationIdAndId(organizationId, fileId)).thenReturn(file);

            when(file.getId()).thenReturn(fileId);
            when(file.getFileFormat()).thenReturn("png");
            when(file.getEntityType()).thenReturn("employee");
            when(file.getEntityId()).thenReturn("123");
            when(file.getFileType()).thenReturn("png");
            when(storage.get(gcsProperties.getBucket().getName(), generatedPath)).thenReturn(blob);

            byte[] fileContent = "file content".getBytes();
            when(blob.getContent()).thenReturn(fileContent);

            ByteArrayResource resource = new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return fileId + ".png";
                }
            };

            FileDownloadResult result = fileServiceImpl.downloadFile(fileId);
            assertNotNull(result);
            assertEquals(fileId + ".png", result.getResource().getFilename());
            assertEquals(file.getCreatedBy(), result.getCreatedBy());
            assertEquals(file.getEntityId(), result.getEntityId());
            assertEquals(file.getOrganizationId(), result.getOrganizationId());
        }
    }

    @Test
    void testDeleteFile_Success() throws Exception {
        String fileId = "123";
        String organizationId = "org123";

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = new HashMap<>();
            mockedOrganization.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);

            File fileToDelete = new File();
            fileToDelete.setId(fileId);
            fileToDelete.setEntityId("123");
            fileToDelete.setEntityType("employee");
            fileToDelete.setFileType("png");
            when(fileRepository.findByOrganizationIdAndId(organizationId, fileId)).thenReturn(fileToDelete);
            when(gcsProperties.getBucket()).thenReturn(mock(GCSProperties.Bucket.class));
            when(storage.get(gcsProperties.getBucket().getName(), "organizations/org123/employee/123/png/123")).thenReturn(blob);
            File deletedFile = fileServiceImpl.deleteFile(fileId);
            assertNotNull(deletedFile);
            assertEquals(fileId, deletedFile.getId());
            verify(fileRepository).delete(fileToDelete);
            verify(blob).delete();
        }
    }

    @Test
    void testDeleteFile_FileNotFound() throws Exception {
        String fileId = "123";
        String organizationId = "org123";
        when(fileRepository.findByOrganizationIdAndId(organizationId, fileId)).thenReturn(null);  // Simulating file not found


        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = new HashMap<>();
            mockedOrganization.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);

            FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
                fileServiceImpl.deleteFile(fileId);
            });

            assertEquals(Constants.NO_FILE_FOUND_WITH_GIVEN_ID, exception.getMessage());
        }
    }




    @Test
    void getFileById_Success() throws FileNotFoundException {
        String organizationId = "org123";

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = new HashMap<>();
            mockedOrganization.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);
            String fileId = "123";
            File file = new File();
            file.setId(fileId);
            file.setEntityId("123");
            file.setEntityType("employee");
            file.setFileType("png");
            when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
            File result = fileServiceImpl.getFileById(fileId);
            assertNotNull(result);
            assertEquals(fileId, result.getId());
            assertEquals(file.getEntityId(), result.getEntityId());
            assertEquals(file.getEntityType(), result.getEntityType());
            assertEquals(file.getFileType(), result.getFileType());
        }
    }

    @Test
    void getFileById_FileNotFoundException() {

        String fileId = "123";
        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> {
            fileServiceImpl.getFileById(fileId);
        });
    }

}







