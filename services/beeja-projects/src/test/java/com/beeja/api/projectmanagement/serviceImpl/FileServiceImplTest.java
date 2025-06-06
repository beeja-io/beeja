package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.FileClient;
import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.exceptions.FeignClientException;
import com.beeja.api.projectmanagement.exceptions.UnAuthorisedException;
import com.beeja.api.projectmanagement.model.dto.File;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.responses.FileResponse;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private FileClient fileClient;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private FileServiceImpl fileService;

    private File mockFile;
    private FileUploadRequest mockFileUploadRequest;
    private FileResponse mockFileResponse;

    @BeforeEach
    void setUp() {
        UserContext.setLoggedInUser(null, null, null, null, null, null);

        mockFile = new File();
        mockFile.setId("file123");
        mockFile.setName("testfile");
        mockFile.setFileFormat("txt");
        mockFile.setEntityType(Constants.ENTITY_TYPE_CLIENT);
        mockFile.setEntityId("entity456");
        mockFile.setCreatedBy("employee123");

        mockFileUploadRequest = new FileUploadRequest();
        mockFileUploadRequest.setFile(new MockMultipartFile("file", "test.txt", "text/plain", "some content".getBytes()));
        mockFileUploadRequest.setName("uploaded_file");
        mockFileUploadRequest.setDescription("Test description");
        mockFileUploadRequest.setFileType(Constants.FILE_TYPE_PROJECT);
        mockFileUploadRequest.setEntityId("newEntityId");
        mockFileUploadRequest.setEntityType(Constants.ENTITY_TYPE_CLIENT);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("totalElements", 1L);
        metadata.put("totalPages", 1);
        metadata.put("currentPage", 1);
        mockFileResponse = new FileResponse(metadata, Collections.singletonList(mockFile));
    }

    @Test
    void listOfFileByEntityId_Success_WithReadAllDocumentsPermission() {
        UserContext.setLoggedInUser(
                "test@example.com", "Test User", "emp1", new HashMap<>(),
                new HashSet<>(Collections.singletonList(PermissionConstants.READ_ALL_DOCUMENTS)), "token"
        );
        when(fileClient.getAllFilesByEntityId(anyString(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok(mockFileResponse));

        FileResponse result = fileService.listOfFileByEntityId("entity456", 1, 10);

        assertNotNull(result);
        assertEquals(mockFileResponse, result);
        verify(fileClient).getAllFilesByEntityId("entity456", 1, 10);
    }

    @Test
    void listOfFileByEntityId_FeignClientException() {
        UserContext.setLoggedInUser(
                "test@example.com", "Test User", "emp1", new HashMap<>(),
                new HashSet<>(Collections.singletonList(PermissionConstants.READ_ALL_DOCUMENTS)), "token"
        );
        when(fileClient.getAllFilesByEntityId(anyString(), anyInt(), anyInt())).thenThrow(new RuntimeException("API error"));

        FeignClientException thrown = assertThrows(FeignClientException.class,
                () -> fileService.listOfFileByEntityId("entity456", 1, 10));

        assertEquals(Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE, thrown.getMessage());
        verify(fileClient).getAllFilesByEntityId("entity456", 1, 10);
    }

    @Test
    void listOfFileByEntityId_UnAuthorisedException() {
        UserContext.setLoggedInUser(
                "test@example.com", "Test User", "emp1", new HashMap<>(),
                new HashSet<>(Collections.singletonList("SOME_OTHER_PERMISSION")), "token"
        );

        UnAuthorisedException thrown = assertThrows(UnAuthorisedException.class,
                () -> fileService.listOfFileByEntityId("entity456", 1, 10));

        assertEquals(Constants.UNAUTHORISED_TO_READ_OTHERS_DOCUMENTS, thrown.getMessage());
        verify(fileClient, never()).getAllFilesByEntityId(anyString(), anyInt(), anyInt());
    }

    @Test
    void uploadFile_Success() throws Exception {
        LinkedHashMap<String, Object> mockFileMap = new LinkedHashMap<>();
        mockFileMap.put("id", "file123");
        mockFileMap.put("name", "testfile.txt");
        mockFileMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
        mockFileMap.put("entityId", "newEntityId");

        when(fileClient.uploadFile(any(FileUploadRequest.class))).thenReturn(ResponseEntity.ok(mockFileMap));

        File result = fileService.uploadFile(mockFileUploadRequest);

        assertNotNull(result);
        assertEquals("file123", result.getId());
        assertEquals("testfile.txt", result.getName());
        verify(fileClient).uploadFile(mockFileUploadRequest);
    }


    @Test
    void uploadFile_FeignClientException() {
        when(fileClient.uploadFile(any(FileUploadRequest.class))).thenThrow(new RuntimeException("Upload service down"));

        FeignClientException thrown = assertThrows(FeignClientException.class,
                () -> fileService.uploadFile(mockFileUploadRequest));

        assertEquals(Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE, thrown.getMessage());
        verify(fileClient).uploadFile(mockFileUploadRequest);
    }

    @Test
    void deleteFile_Success_DeleteAllDocumentPermission() throws Exception {
        UserContext.setLoggedInUser(
                "test@example.com", "Test User", "differentEmployee", new HashMap<>(),
                new HashSet<>(Collections.singletonList(PermissionConstants.DELETE_ALL_DOCUMENT)), "token"
        );

        LinkedHashMap<String, Object> fileToFetchMap = new LinkedHashMap<>();
        fileToFetchMap.put("id", "file123");
        fileToFetchMap.put("name", "testfile.txt");
        fileToFetchMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
        fileToFetchMap.put("createdBy", "employee123");

        when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileToFetchMap));
        when(fileClient.deleteFile(anyString())).thenReturn(ResponseEntity.ok(fileToFetchMap));

        File result = fileService.deleteFile("file123");

        assertNotNull(result);
        assertEquals("file123", result.getId());
        verify(fileClient).getFileById("file123");
        verify(fileClient).deleteFile("file123");
    }

    @Test
    void deleteFile_Success_OwnDocument() throws Exception {
        UserContext.setLoggedInUser(
                "test@example.com", "Test User", "employee123", new HashMap<>(),
                new HashSet<>(Collections.singletonList(PermissionConstants.DELETE_DOCUMENT)), "token"
        );

        LinkedHashMap<String, Object> fileToFetchMap = new LinkedHashMap<>();
        fileToFetchMap.put("id", "file123");
        fileToFetchMap.put("name", "testfile.txt");
        fileToFetchMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
        fileToFetchMap.put("createdBy", "employee123");

        when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileToFetchMap));
        when(fileClient.deleteFile(anyString())).thenReturn(ResponseEntity.ok(fileToFetchMap));

        File result = fileService.deleteFile("file123");

        assertNotNull(result);
        assertEquals("file123", result.getId());
        verify(fileClient).getFileById("file123");
        verify(fileClient).deleteFile("file123");
    }

    @Test
    void deleteFile_Unauthorized_NotOwnDocumentAndNoDeleteAllPermission() {
        UserContext.setLoggedInUser(
                "test@example.com", "Test User", "differentEmployee", new HashMap<>(),
                new HashSet<>(Collections.singletonList(PermissionConstants.DELETE_DOCUMENT)), "token"
        );

        LinkedHashMap<String, Object> fileToFetchMap = new LinkedHashMap<>();
        fileToFetchMap.put("id", "file123");
        fileToFetchMap.put("name", "testfile.txt");
        fileToFetchMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
        fileToFetchMap.put("createdBy", "employee123");

        when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileToFetchMap));

        UnAuthorisedException thrown = assertThrows(UnAuthorisedException.class,
                () -> fileService.deleteFile("file123"));

        assertEquals(Constants.UNAUTHORISED_ACCESS, thrown.getMessage());
        verify(fileClient).getFileById("file123");
    }

    @Test
    void deleteFile_Unauthorized_NonClientEntityType() {
        UserContext.setLoggedInUser(
                "test@example.com", "Test User", "employee123", new HashMap<>(),
                new HashSet<>(Collections.singletonList(PermissionConstants.DELETE_DOCUMENT)), "token"
        );

        LinkedHashMap<String, Object> fileToFetchMap = new LinkedHashMap<>();
        fileToFetchMap.put("id", "file123");
        fileToFetchMap.put("name", "testfile.txt");
        fileToFetchMap.put("entityType", "OTHER_TYPE");
        fileToFetchMap.put("createdBy", "employee123");

        when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileToFetchMap));

        UnAuthorisedException thrown = assertThrows(UnAuthorisedException.class,
                () -> fileService.deleteFile("file123"));

        assertEquals(Constants.UNAUTHORISED_ACCESS, thrown.getMessage());
        verify(fileClient).getFileById("file123");
    }

    @Test
    void deleteFile_GetFileById_FeignClientException() {
        when(fileClient.getFileById(anyString())).thenThrow(new RuntimeException("Get file failed"));

        FeignClientException thrown = assertThrows(FeignClientException.class,
                () -> fileService.deleteFile("file123"));

        assertEquals(Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE, thrown.getMessage());
        verify(fileClient).getFileById("file123");

    }

    @Test
    void deleteFile_DeleteFile_FeignClientException() throws Exception {
        UserContext.setLoggedInUser(
                "test@example.com", "Test User", "employee123", new HashMap<>(),
                new HashSet<>(Collections.singletonList(PermissionConstants.DELETE_DOCUMENT)), "token"
        );

        LinkedHashMap<String, Object> fileToFetchMap = new LinkedHashMap<>();
        fileToFetchMap.put("id", "file123");
        fileToFetchMap.put("name", "testfile.txt");
        fileToFetchMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
        fileToFetchMap.put("createdBy", "employee123");

        when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileToFetchMap));
        when(fileClient.deleteFile(anyString())).thenThrow(new RuntimeException("Delete file failed"));

        FeignClientException thrown = assertThrows(FeignClientException.class,
                () -> fileService.deleteFile("file123"));

        assertEquals(Constants.ERROR_IN_DELETING_FILE_FROM_FILE_SERVICE, thrown.getMessage());
        verify(fileClient).getFileById("file123");
        verify(fileClient).deleteFile("file123");
    }

    @Test
    void deleteFile_ObjectMapperThrowsException() throws Exception {
        UserContext.setLoggedInUser(
                "test@example.com", "Test User", "employee123", new HashMap<>(),
                new HashSet<>(Collections.singletonList(PermissionConstants.DELETE_DOCUMENT)), "token"
        );

        LinkedHashMap<String, Object> fileToFetchMap = new LinkedHashMap<>();
        fileToFetchMap.put("id", "file123");
        fileToFetchMap.put("name", "testfile.txt");
        fileToFetchMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
        fileToFetchMap.put("createdBy", "employee123");

        LinkedHashMap<String, Object> deletedFileResponse = new LinkedHashMap<>();
        deletedFileResponse.put("id", "file123");
        deletedFileResponse.put("name", "testfile.txt");
        deletedFileResponse.put("entityType", Constants.ENTITY_TYPE_CLIENT);
        deletedFileResponse.put("createdBy", "employee123");

        when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileToFetchMap));
        when(fileClient.deleteFile(anyString())).thenReturn(ResponseEntity.ok(deletedFileResponse));

        File mockFile = new File();
        mockFile.setId("file123");
        mockFile.setName("testfile.txt");
        mockFile.setEntityType(Constants.ENTITY_TYPE_CLIENT);
        mockFile.setCreatedBy("employee123");

        when(objectMapper.convertValue(eq(fileToFetchMap), eq(File.class))).thenReturn(mockFile);
        when(objectMapper.convertValue(eq(deletedFileResponse), eq(File.class)))
                .thenThrow(new IllegalArgumentException("Mapping failed"));

        Exception thrown = assertThrows(Exception.class,
                () -> fileService.deleteFile("file123"));

        assertEquals(Constants.SOMETHING_WENT_WRONG, thrown.getMessage());

        verify(fileClient).getFileById("file123");
        verify(fileClient).deleteFile("file123");
        verify(objectMapper, times(2)).convertValue(any(), eq(File.class));
    }
}