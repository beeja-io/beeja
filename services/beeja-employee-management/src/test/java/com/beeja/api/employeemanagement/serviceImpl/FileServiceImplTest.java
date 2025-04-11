package com.beeja.api.employeemanagement.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beeja.api.employeemanagement.client.FileClient;
import com.beeja.api.employeemanagement.constants.PermissionConstants;
import com.beeja.api.employeemanagement.controller.FileController;
import com.beeja.api.employeemanagement.model.File;
import com.beeja.api.employeemanagement.requests.FileUploadRequest;
import com.beeja.api.employeemanagement.response.FileResponse;
import com.beeja.api.employeemanagement.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class FileServiceImplTest {
  @InjectMocks private FileServiceImpl fileServiceImpl;

  @Mock private FileClient fileClient;

  @Mock private ObjectMapper objectMapper;

  private MockedStatic<UserContext> userContextMock;

  private static final String FILE_ID = "file123";
  private static final String ENTITY_ID = "entity123";
  private static final String LOGGED_IN_EMPLOYEE_ID = "employee123";
  private static final String ORGANIZATION_ID = "org123";
  private static final int PAGE = 1;
  private static final int SIZE = 10;

  private FileResponse mockResponse;

  private FileUploadRequest fileUploadRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userContextMock = mockStatic(UserContext.class);
    mockResponse =
        new FileResponse(
            Map.of("page", PAGE, "size", SIZE, "total", 2),
            List.of(
                new File(
                    "file1",
                    "File One",
                    "document",
                    "pdf",
                    "2MB",
                    ENTITY_ID,
                    "employee",
                    "desc",
                    ORGANIZATION_ID,
                    "user1",
                    "User One",
                    "user2",
                    new Date(),
                    new Date()),
                new File(
                    "file2",
                    "File Two",
                    "image",
                    "png",
                    "3MB",
                    ENTITY_ID,
                    "employee",
                    "desc",
                    ORGANIZATION_ID,
                    "user1",
                    "User One",
                    "user2",
                    new Date(),
                    new Date())));
  }

  @Test
  void listOfFileByEntityId_WithReadAllPermission_ReturnsFiles() throws Exception {
    Set<String> permissions = Set.of(PermissionConstants.READ_ALL_EMPLOYEE_DOCUMENT);
    userContextMock.when(UserContext::getLoggedInUserPermissions).thenReturn(permissions);

    when(fileClient.getAllFilesByEntityId(ENTITY_ID, PAGE, SIZE))
        .thenReturn(ResponseEntity.ok(mockResponse));

    FileResponse result = fileServiceImpl.listOfFileByEntityId(ENTITY_ID, PAGE, SIZE);
    assertNotNull(result);
    assertEquals(2, result.getFiles().size());
    assertEquals("file1", result.getFiles().get(0).getId());
    assertEquals("file2", result.getFiles().get(1).getId());
  }

  @Test
  void listOfFileByEntityId_AsSelf_ReturnsFiles() throws Exception {
    userContextMock.when(UserContext::getLoggedInUserPermissions).thenReturn(Set.of());
    userContextMock.when(UserContext::getLoggedInEmployeeId).thenReturn(ENTITY_ID);
    when(fileClient.getAllFilesByEntityId(ENTITY_ID, PAGE, SIZE))
        .thenReturn(ResponseEntity.ok(mockResponse));

    FileResponse result = fileServiceImpl.listOfFileByEntityId(ENTITY_ID, PAGE, SIZE);
    assertNotNull(result);
    assertEquals(2, result.getFiles().size());

    verify(fileClient, times(1)).getAllFilesByEntityId(ENTITY_ID, PAGE, SIZE);
  }

  @AfterEach
  void tearDown() {
    if (userContextMock != null) {
      userContextMock.close();
    }
  }

  @Test
  void uploadFile_SuccessfulUpload_ReturnsFile() throws Exception {

    Set<String> permissions =
        Set.of(
            PermissionConstants.CREATE_EMPLOYEE_DOCUMENT,
            PermissionConstants.CREATE_ALL_EMPLOYEE_DOCUMENT);

    userContextMock.when(UserContext::getLoggedInUserPermissions).thenReturn(permissions);
    LinkedHashMap<String, Object> responseBody = new LinkedHashMap<>();
    responseBody.put("id", "fileId");
    responseBody.put("name", "Test File");
    responseBody.put("fileType", "pdf");
    ResponseEntity<Object> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
    when(fileClient.uploadFile(ArgumentMatchers.any(FileUploadRequest.class)))
        .thenReturn(responseEntity);

    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    fileUploadRequest.setFile(mock(MultipartFile.class));
    fileUploadRequest.setName("Test File");
    fileUploadRequest.setFileType("pdf");
    fileUploadRequest.setEntityId("entityId");
    fileUploadRequest.setEntityType("employee");
    fileUploadRequest.setDescription("Test Description");

    File uploadedFile = fileServiceImpl.uploadFile(fileUploadRequest);

    assertNotNull(uploadedFile);
    assertEquals("fileId", uploadedFile.getId());
    assertEquals("Test File", uploadedFile.getName());
    verify(fileClient, times(1)).uploadFile(fileUploadRequest);
  }

  @InjectMocks private FileController fileController;

  @Test
  void updateFile_ShouldReturnUpdatedFile_WhenFileExists() throws Exception {

    String fileId = "test-file-id";

    Set<String> permissions =
        Set.of(
            PermissionConstants.UPDATE_EMPLOYEE_DOCUMENT,
            PermissionConstants.UPDATE_ALL_EMPLOYEE_DOCUMENT);

    userContextMock.when(UserContext::getLoggedInEmployeeId).thenReturn("test-entity-id");
    userContextMock.when(UserContext::getLoggedInUserPermissions).thenReturn(permissions);

    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    fileUploadRequest.setName("updated-file.txt");
    fileUploadRequest.setFileType("text/plain");
    fileUploadRequest.setEntityId("test-entity-id");
    fileUploadRequest.setEntityType("employee");
    fileUploadRequest.setDescription("Updated description");

    MultipartFile mockMultipartFile = mock(MultipartFile.class);
    fileUploadRequest.setFile(mockMultipartFile);
    LinkedHashMap<String, Object> mockFileData = new LinkedHashMap<>();
    mockFileData.put("id", fileId);
    mockFileData.put("name", "original-file.txt");
    mockFileData.put("entityId", "test-entity-id");
    ResponseEntity<Object> mockGetFileResponse = new ResponseEntity<>(mockFileData, HttpStatus.OK);
    when(fileClient.getFileById(fileId)).thenReturn(mockGetFileResponse);

    LinkedHashMap<String, Object> mockUpdatedFileData = new LinkedHashMap<>();
    mockUpdatedFileData.put("id", fileId);
    mockUpdatedFileData.put("name", "updated-file.txt");
    mockUpdatedFileData.put("entityId", "test-entity-id");
    ResponseEntity<Object> mockUpdateFileResponse =
        new ResponseEntity<>(mockUpdatedFileData, HttpStatus.OK);
    when(fileClient.updateFile(eq(fileId), any(FileUploadRequest.class)))
        .thenReturn(mockUpdateFileResponse);

    File updatedFile = fileServiceImpl.updateFile(fileId, fileUploadRequest);
    assertNotNull(updatedFile);
    assertEquals(fileId, updatedFile.getId());
    assertEquals("updated-file.txt", updatedFile.getName());
    assertEquals("test-entity-id", updatedFile.getEntityId());

    verify(fileClient).getFileById(fileId);
    verify(fileClient).updateFile(eq(fileId), any(FileUploadRequest.class));
  }

  @Test
  void deleteFile_ShouldReturnDeletedFile_WhenFileExistsAndUserHasPermission() throws Exception {

    String fileId = "test-file-id";
    String loggedInEmployeeId = "test-employee-id";
    String entityType = "employee";
    Set<String> permissions = Set.of(PermissionConstants.DELETE_ALL_EMPLOYEE_DOCUMENT);

    userContextMock.when(UserContext::getLoggedInEmployeeId).thenReturn(loggedInEmployeeId);
    userContextMock.when(UserContext::getLoggedInUserPermissions).thenReturn(permissions);
    LinkedHashMap<String, Object> fileData = new LinkedHashMap<>();
    fileData.put("id", fileId);
    fileData.put("entityType", entityType);
    fileData.put("createdBy", loggedInEmployeeId);
    ResponseEntity<Object> fileResponse = new ResponseEntity<>(fileData, HttpStatus.OK);
    when(fileClient.getFileById(fileId)).thenReturn(fileResponse);

    ResponseEntity<Object> deleteResponse = new ResponseEntity<>(fileData, HttpStatus.OK);
    when(fileClient.deleteFile(fileId)).thenReturn(deleteResponse);

    File deletedFile = fileServiceImpl.deleteFile(fileId);

    assertNotNull(deletedFile);
    assertEquals(fileId, deletedFile.getId());
    assertEquals(entityType, deletedFile.getEntityType());
    verify(fileClient).getFileById(fileId);
    verify(fileClient).deleteFile(fileId);
  }

  @Test
  void uploadOrUpdateFile_SuccessfulUpload_ReturnsFile() throws Exception {
    Set<String> permissions =
        Set.of(
            PermissionConstants.UPDATE_PROFILE_PIC_SELF,
            PermissionConstants.UPDATE_PROFILE_PIC_ALL);
    userContextMock.when(UserContext::getLoggedInUserPermissions).thenReturn(permissions);

    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    fileUploadRequest.setFile(mock(MultipartFile.class));
    fileUploadRequest.setName("Test File");
    fileUploadRequest.setFileType("pdf");
    fileUploadRequest.setEntityId("entity123");
    fileUploadRequest.setEntityType("employee");
    fileUploadRequest.setDescription("Test Description");

    LinkedHashMap<String, Object> responseBody = new LinkedHashMap<>();
    responseBody.put("id", "fileId");
    responseBody.put("name", "Test File");
    responseBody.put("fileType", "pdf");

    ResponseEntity<Object> mockResponseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
    when(fileClient.uploadOrUpdateFile(any(FileUploadRequest.class)))
        .thenReturn(mockResponseEntity);

    File expectedFile = new File();
    expectedFile.setId("fileId");
    expectedFile.setName("Test File");
    expectedFile.setFileType("pdf");

    lenient().when(objectMapper.convertValue(responseBody, File.class)).thenReturn(expectedFile);
    File uploadedFile = fileServiceImpl.uploadOrUpdateFile(fileUploadRequest);
    assertNotNull(uploadedFile);
    assertEquals("fileId", uploadedFile.getId());
    assertEquals("Test File", uploadedFile.getName());
    assertEquals("pdf", uploadedFile.getFileType());
    verify(fileClient, times(1)).uploadOrUpdateFile(any(FileUploadRequest.class));
  }
}
