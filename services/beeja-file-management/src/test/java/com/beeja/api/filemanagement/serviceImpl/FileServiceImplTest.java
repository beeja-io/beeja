package com.beeja.api.filemanagement.serviceImpl;

import com.beeja.api.filemanagement.config.properties.AllowedContentTypes;
import com.beeja.api.filemanagement.exceptions.FileAccessException;
import com.beeja.api.filemanagement.exceptions.FileTypeMismatchException;
import com.beeja.api.filemanagement.exceptions.MongoFileUploadException;
import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.repository.FileRepository;
import com.beeja.api.filemanagement.requests.FileUploadRequest;
import com.beeja.api.filemanagement.response.FileDownloadResult;
import com.beeja.api.filemanagement.response.FileResponse;
import com.beeja.api.filemanagement.service.FileStorageService;
import com.beeja.api.filemanagement.utils.Constants;
import com.beeja.api.filemanagement.utils.UserContext;
import com.mongodb.MongoWriteException;
import org.bson.BsonDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private AllowedContentTypes allowedContentTypes;

    @Mock
    private MongoTemplate mongoTemplate;

    private Map<String, Object> mockOrgContext;

    @Mock
    private FileStorageService fileStorage; // Corrected field name



    @BeforeEach
    void setUp() {
        mockOrgContext = new HashMap<>();
        mockOrgContext.put("id", "org123");
        UserContext.setLoggedInUserOrganization(mockOrgContext);
    }

    @Test
    void testUploadFile_Success() throws Exception {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(multipartFile);
        request.setName("testFile");
        request.setEntityId("entity123");
        request.setDescription("test description");
        request.setFileType("General");

        when(allowedContentTypes.getAllowedTypes()).thenReturn(new String[]{"text/plain"});
        when(fileRepository.save(any(File.class))).thenReturn(new File());

        File result = fileService.uploadFile(request);

        assertNotNull(result);
        verify(fileStorage, times(1)).uploadFile(eq(multipartFile), any(File.class)); // Corrected verify call
    }

    @Test
    void testUploadFile_InvalidContentType() {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "application/pdf", "test content".getBytes());
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(multipartFile);

        when(allowedContentTypes.getAllowedTypes()).thenReturn(new String[]{"text/plain"});

        assertThrows(FileTypeMismatchException.class, () -> fileService.uploadFile(request));
    }



    @Test
    void testUploadFile_IOException() throws Exception {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(multipartFile);

        when(allowedContentTypes.getAllowedTypes()).thenReturn(new String[]{"text/plain"});
        when(fileRepository.save(any(File.class))).thenReturn(new File());
        doThrow(new IOException()).when(fileStorage).uploadFile(any(MultipartFile.class), any(File.class)); // Corrected line

        assertThrows(FileAccessException.class, () -> fileService.uploadFile(request));
    }

    @Test
    void testUpdateFile_Success() throws Exception {
        File file = new File();
        file.setId("file123");
        MultipartFile multipartFile = new MockMultipartFile("file", "updated.txt", "text/plain", "updated content".getBytes());
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(multipartFile);

        when(fileRepository.findByOrganizationIdAndId("org123", "file123")).thenReturn(file);

        File result = fileService.updateFile("file123", request);

        assertEquals("file123", result.getId());
        verify(fileStorage, times(1)).updateFile(file, multipartFile); // Corrected verify call
    }


    @Test
    void testGetFileById_Success() throws Exception {
        File file = new File();
        file.setId("file123");
        when(fileRepository.findById("file123")).thenReturn(Optional.of(file));

        File result = fileService.getFileById("file123");
        assertEquals("file123", result.getId());
    }

    @Test
    void testGetFileById_NotFound() {
        when(fileRepository.findById("file404")).thenReturn(Optional.empty());
        assertThrows(FileNotFoundException.class, () -> fileService.getFileById("file404"));
    }

    @Test
    void testUploadOrUpdateFile_Upload() throws Exception {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(multipartFile);
        request.setEntityId("entity123");
        request.setFileType("General");

        when(fileRepository.findByEntityIdAndFileTypeAndOrganizationId("entity123", "General", "org123")).thenReturn(null);
        when(allowedContentTypes.getAllowedTypes()).thenReturn(new String[]{"text/plain"});
        when(fileRepository.save(any(File.class))).thenReturn(new File());

        File result = fileService.uploadOrUpdateFile(request);

        assertNotNull(result);
        verify(fileStorage, times(1)).uploadFile(eq(multipartFile), any(File.class)); // Corrected verify call
    }

    @Test
    void testUploadOrUpdateFile_Update() throws Exception {
        File existingFile = new File();
        existingFile.setId("file123");
        MultipartFile multipartFile = new MockMultipartFile("file", "updated.txt", "text/plain", "updated content".getBytes());
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(multipartFile);
        request.setEntityId("entity123");
        request.setFileType("General");

        when(fileRepository.findByEntityIdAndFileTypeAndOrganizationId("entity123", "General", "org123")).thenReturn(existingFile);
        when(fileRepository.findByOrganizationIdAndId("org123", "file123")).thenReturn(existingFile);

        File result = fileService.uploadOrUpdateFile(request);

        assertEquals("file123", result.getId());
        verify(fileStorage, times(1)).updateFile(existingFile, multipartFile); // Corrected verify call
    }



    @Test
    void testListofFileByEntityId_Exception() throws Exception {
        when(mongoTemplate.aggregate(any(Aggregation.class), eq(File.class), eq(File.class))).thenThrow(new RuntimeException("Test Exception"));
        assertThrows(Exception.class, () -> fileService.listofFileByEntityId("entity123", 1, 10));
    }

    @Test
    void testDownloadFile_Success() throws Exception {
        File file = new File();
        file.setId("file123");
        file.setCreatedBy("user123");
        file.setEntityId("entity123");
        file.setOrganizationId("org123");
        file.setName("test.txt");
        byte[] fileContent = "test content".getBytes();

        when(fileRepository.findById("file123")).thenReturn(Optional.of(file));
        when(fileStorage.downloadFile(file)).thenReturn(fileContent); // corrected line

        FileDownloadResult result = fileService.downloadFile("file123");

        assertEquals("user123", result.getCreatedBy());
        assertEquals("entity123", result.getEntityId());
        assertEquals("org123", result.getOrganizationId());
        assertEquals("test.txt", result.getFileName());
        assertArrayEquals(fileContent, result.getResource().getByteArray());
    }


    @Test
    void testDownloadFile_FileNotFound() {
        when(fileRepository.findById("file404")).thenReturn(Optional.empty());
        assertThrows(FileNotFoundException.class, () -> fileService.downloadFile("file404"));
    }

    @Test
    void testDeleteFile_Success() throws Exception {
        File file = new File();
        file.setId("file123");
        when(fileRepository.findById("file123")).thenReturn(Optional.of(file));

        File result = fileService.deleteFile("file123");

        assertEquals("file123", result.getId());
        verify(fileStorage, times(1)).deleteFile(file); // Corrected verify call
        verify(fileRepository, times(1)).delete(file);
    }

    @Test
    void testDeleteFile_FileNotFound() {
        when(fileRepository.findById("file404")).thenReturn(Optional.empty());
        assertThrows(FileNotFoundException.class, () -> fileService.deleteFile("file404"));
    }
}