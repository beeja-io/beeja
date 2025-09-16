package com.beeja.api.projectmanagement.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.ByteArrayResource;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

  @Mock private FileClient fileClient;
  @Spy private ObjectMapper objectMapper;
  @InjectMocks private FileServiceImpl fileService;

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
    mockFileUploadRequest.setFile(
            new MockMultipartFile("file", "test.txt", "text/plain", "some content".getBytes()));
    mockFileUploadRequest.setName("uploaded_file");
    mockFileUploadRequest.setDescription("Test description");
    mockFileUploadRequest.setFileType(Constants.FILE_TYPE_PROJECT);
    mockFileUploadRequest.setEntityId("entity456");
    mockFileUploadRequest.setEntityType(Constants.ENTITY_TYPE_CLIENT);

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("totalElements", 1L);
    metadata.put("totalPages", 1);
    metadata.put("currentPage", 1);
    mockFileResponse = new FileResponse(metadata, Collections.singletonList(mockFile));
  }
  @Test
  void listOfFileByEntityId_Success_WithPermission() {
    UserContext.setLoggedInUser(
            "user@test.com",
            "User",
            "emp1",
            new HashMap<>(),
            new HashSet<>(Collections.singleton(PermissionConstants.READ_ALL_DOCUMENTS)),
            "token");

    when(fileClient.getAllFilesByEntityId(anyString(), anyInt(), anyInt()))
            .thenReturn(ResponseEntity.ok(mockFileResponse));

    FileResponse result = fileService.listOfFileByEntityId("entity456", 1, 10);

    assertNotNull(result);
    assertEquals(mockFileResponse, result);
    verify(fileClient).getAllFilesByEntityId("entity456", 1, 10);
  }

  @Test
  void listOfFileByEntityId_UnAuthorised() {
    UserContext.setLoggedInUser(
            "user@test.com", "User", "emp1", new HashMap<>(), new HashSet<>(), "token");

    UnAuthorisedException thrown =
            assertThrows(
                    UnAuthorisedException.class,
                    () -> fileService.listOfFileByEntityId("entity456", 1, 10));

    assertEquals(Constants.UNAUTHORISED_TO_READ_OTHERS_DOCUMENTS, thrown.getMessage());
    verify(fileClient, never()).getAllFilesByEntityId(anyString(), anyInt(), anyInt());
  }

  @Test
  void listOfFileByEntityId_FeignClientException() {
    UserContext.setLoggedInUser(
            "user@test.com",
            "User",
            "emp1",
            new HashMap<>(),
            new HashSet<>(Collections.singleton(PermissionConstants.READ_ALL_DOCUMENTS)),
            "token");

    when(fileClient.getAllFilesByEntityId(anyString(), anyInt(), anyInt()))
            .thenThrow(new RuntimeException("API error"));

    FeignClientException thrown =
            assertThrows(
                    FeignClientException.class,
                    () -> fileService.listOfFileByEntityId("entity456", 1, 10));

    assertEquals(Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE, thrown.getMessage());
  }

  @Test
  void uploadFile_Success() throws Exception {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    map.put("id", "file123");
    map.put("name", "testfile.txt");
    map.put("entityType", Constants.ENTITY_TYPE_CLIENT);
    map.put("entityId", "entity456");

    when(fileClient.uploadFile(any(FileUploadRequest.class))).thenReturn(ResponseEntity.ok(map));

    File result = fileService.uploadFile(mockFileUploadRequest);

    assertNotNull(result);
    assertEquals("file123", result.getId());
    assertEquals("testfile.txt", result.getName());
  }

  @Test
  void uploadFile_FeignClientException() {
    when(fileClient.uploadFile(any(FileUploadRequest.class)))
            .thenThrow(new RuntimeException("Upload failed"));

    FeignClientException thrown =
            assertThrows(FeignClientException.class, () -> fileService.uploadFile(mockFileUploadRequest));

    assertEquals(Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE, thrown.getMessage());
  }

  @Test
  void deleteFile_Success_DeleteAllPermission() throws Exception {
    UserContext.setLoggedInUser(
            "user@test.com",
            "User",
            "empX",
            new HashMap<>(),
            new HashSet<>(Collections.singleton(PermissionConstants.DELETE_ALL_DOCUMENT)),
            "token");

    LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
    fileMap.put("id", "file123");
    fileMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
    fileMap.put("createdBy", "employee123");

    when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileMap));
    when(fileClient.deleteFile(anyString())).thenReturn(ResponseEntity.ok(fileMap));

    File result = fileService.deleteFile("file123");

    assertNotNull(result);
    assertEquals("file123", result.getId());
  }

  @Test
  void deleteFile_Unauthorised_NotOwnerNoPermission() {
    UserContext.setLoggedInUser(
            "user@test.com",
            "User",
            "empX",
            new HashMap<>(),
            new HashSet<>(Collections.singleton(PermissionConstants.DELETE_DOCUMENT)),
            "token");

    LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
    fileMap.put("id", "file123");
    fileMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
    fileMap.put("createdBy", "employee123");

    when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileMap));

    UnAuthorisedException thrown =
            assertThrows(UnAuthorisedException.class, () -> fileService.deleteFile("file123"));

    assertEquals(Constants.UNAUTHORISED_ACCESS, thrown.getMessage());
  }

  @Test
  void deleteFile_GetFileById_FeignClientException() {
    when(fileClient.getFileById(anyString())).thenThrow(new RuntimeException("fail"));

    FeignClientException thrown =
            assertThrows(FeignClientException.class, () -> fileService.deleteFile("file123"));

    assertEquals(Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE, thrown.getMessage());
  }

  @Test
  void updateFile_Success() throws Exception {
    LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
    fileMap.put("id", "file123");
    fileMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);

    LinkedHashMap<String, Object> updatedMap = new LinkedHashMap<>();
    updatedMap.put("id", "file123");
    updatedMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);

    when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileMap));
    when(fileClient.updateFile(anyString(), any(FileUploadRequest.class)))
            .thenReturn(ResponseEntity.ok(updatedMap));

    File result = fileService.updateFile("file123", mockFileUploadRequest);

    assertNotNull(result);
    assertEquals("file123", result.getId());
  }

  @Test
  void updateFile_NonClientEntityType() {
    LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
    fileMap.put("id", "file123");
    fileMap.put("entityType", "OTHER");

    when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileMap));

    assertThrows(RuntimeException.class, () -> fileService.updateFile("file123", mockFileUploadRequest));
  }

  @Test
  void updateFile_NullResponseFromUpdate() {
    LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
    fileMap.put("id", "file123");
    fileMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);

    when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileMap));
    when(fileClient.updateFile(anyString(), any(FileUploadRequest.class))).thenReturn(null);

    FeignClientException thrown =
            assertThrows(FeignClientException.class, () -> fileService.updateFile("file123", mockFileUploadRequest));

    assertEquals(Constants.ERROR_IN_UPDATING_FILE_FROM_FILE_SERVICE, thrown.getMessage());
  }

  @Test
  void updateFile_GetFileByIdReturnsNull() {
    when(fileClient.getFileById(anyString())).thenReturn(null);
    assertThrows(FeignClientException.class, () -> fileService.updateFile("file123", mockFileUploadRequest));
  }

  // ------------------------ downloadFile ------------------------
  @Test
  void downloadFile_Success() throws Exception {
    LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
    fileMap.put("id", "file123");
    fileMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
    fileMap.put("fileFormat", "txt");

    when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileMap));
    when(fileClient.downloadFile(anyString())).thenReturn(ResponseEntity.ok(new byte[]{1,2,3}));

    ByteArrayResource resource = fileService.downloadFile("file123");
    assertNotNull(resource);
    assertArrayEquals(new byte[]{1,2,3}, resource.getByteArray());
  }

  @Test
  void downloadFile_NonClientEntityType() {
    LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
    fileMap.put("id", "file123");
    fileMap.put("entityType", "OTHER");

    when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileMap));

    assertThrows(UnAuthorisedException.class, () -> fileService.downloadFile("file123"));
  }

  @Test
  void downloadFile_FileClientDownloadThrows() throws Exception {
    LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
    fileMap.put("id", "file123");
    fileMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
    fileMap.put("fileFormat", "txt");

    when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileMap));
    when(fileClient.downloadFile(anyString())).thenThrow(new RuntimeException("Download failed"));

    FeignClientException thrown = assertThrows(FeignClientException.class, () -> fileService.downloadFile("file123"));
    assertEquals(Constants.ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE, thrown.getMessage());
  }

  @Test
  void downloadFile_GetFileByIdBodyNull() {
    when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(null));
    assertThrows(FeignClientException.class, () -> fileService.downloadFile("file123"));
  }

  @Test
  void downloadFile_FilenameParsedFromContentDisposition() throws Exception {
    LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
    fileMap.put("id", "file123");
    fileMap.put("entityType", Constants.ENTITY_TYPE_CLIENT);
    fileMap.put("fileFormat", "txt");

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"custom_name.txt\"");

    when(fileClient.getFileById(anyString())).thenReturn(ResponseEntity.ok(fileMap));
    when(fileClient.downloadFile(anyString()))
            .thenReturn(ResponseEntity.ok().headers(headers).body(new byte[]{1, 2, 3}));

    ByteArrayResource resource = fileService.downloadFile("file123");
    assertNotNull(resource);
    assertEquals("custom_name.txt.txt", resource.getFilename());
  }
}
