package com.beeja.api.filemanagement.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beeja.api.filemanagement.exceptions.MongoFileUploadException;
import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.repository.FileRepository;
import com.beeja.api.filemanagement.requests.FileUploadRequest;
import com.beeja.api.filemanagement.response.FileDownloadResult;
import com.beeja.api.filemanagement.response.FileResponse;
import com.beeja.api.filemanagement.service.FileService;
import com.beeja.api.filemanagement.utils.Constants;
import com.beeja.api.filemanagement.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class FileControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private FileService fileService;
  @InjectMocks private FileController fileController;

  @Mock private FileRepository fileRepository;

  private static final String fileId = "file123";

  private static final String entityId = "TAC123";

  @BeforeEach
  void setUp() {
    // Mock the static methods of UserContext
    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      Map<String, String> mockedOrganization = new HashMap<>();
      mockedOrganization.put("id", "org1");

      // Mock the static calls
      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockedOrganization);
      mockedUserContext.when(UserContext::getLoggedInEmployeeId).thenReturn("user1");
      mockedUserContext.when(UserContext::getLoggedInUserName).thenReturn("User One");
    }
  }

  @Test
  public void testGetAllFilesByEntityId() throws Exception {
    // Arrange
    String entityId = "12345";
    int page = 1;
    int size = 10;

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("totalPages", 5);
    metadata.put("totalFiles", 50);

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      Map<String, String> mockedOrganization = new HashMap<>();
      mockedOrganization.put("id", "org1");
      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockedOrganization);

      List<File> files =
          Arrays.asList(
              new File(
                  "1",
                  "file1.pdf",
                  "application/pdf",
                  null,
                  "1024",
                  entityId,
                  "project",
                  null,
                  "org1",
                  "user1",
                  "User One",
                  null,
                  new Date(),
                  new Date()),
              new File(
                  "2",
                  "file2.png",
                  "image/png",
                  null,
                  "2048",
                  entityId,
                  "employee",
                  null,
                  "org1",
                  "user1",
                  "User One",
                  null,
                  new Date(),
                  new Date()));

      FileResponse fileResponse = new FileResponse(metadata, files);

      when(fileService.listofFileByEntityId(entityId, page, size)).thenReturn(fileResponse);
      mockMvc
          .perform(
              get("/v1/files/{entityId}", entityId)
                  .param("page", String.valueOf(page))
                  .param("size", String.valueOf(size))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.metadata.totalPages").value(5))
          .andExpect(jsonPath("$.metadata.totalFiles").value(50))
          .andExpect(jsonPath("$.files[0].name").value("file1.pdf"))
          .andExpect(jsonPath("$.files[0].fileType").value("application/pdf"))
          .andExpect(jsonPath("$.files[0].fileSize").value("1024"))
          .andExpect(jsonPath("$.files[1].name").value("file2.png"))
          .andExpect(jsonPath("$.files[1].fileType").value("image/png"))
          .andExpect(jsonPath("$.files[1].fileSize").value("2048"))
          .andDo(print());

      verify(fileService, times(1)).listofFileByEntityId(entityId, page, size);
    }
  }

  @Test
  public void testUploadFile_UnexpectedException() throws Exception {
    MockMultipartFile mockFile =
        new MockMultipartFile("file", "test.txt", "text/plain", "Sample content".getBytes());

    FileUploadRequest fileInput = new FileUploadRequest();
    fileInput.setFile(mockFile);
    fileInput.setName("Test File");
    fileInput.setEntityId("12345");
    fileInput.setEntityType("employee");
    fileInput.setDescription("Test description");

    when(fileService.uploadFile(any(FileUploadRequest.class)))
        .thenThrow(new RuntimeException("Unexpected error"));

    mockMvc
        .perform(
            multipart("/v1/files")
                .file(mockFile)
                .param("name", "Test File")
                .param("entityId", "12345")
                .param("entityType", "employee")
                .param("description", "Test description"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string(containsString(Constants.SERVICE_DOWN_ERROR)))
        .andDo(print());

    verify(fileService, times(1)).uploadFile(any(FileUploadRequest.class));
  }

  @Test
  public void testUploadFile_MongoFileUploadException() throws Exception {
    MockMultipartFile mockFile =
        new MockMultipartFile("file", "test.txt", "text/plain", "Sample content".getBytes());

    FileUploadRequest fileInput = new FileUploadRequest();
    fileInput.setFile(mockFile);
    fileInput.setName("Test File");
    fileInput.setEntityId("12345");
    fileInput.setEntityType("employee");
    fileInput.setDescription("Test description");

    when(fileService.uploadFile(any(FileUploadRequest.class)))
        .thenThrow(new MongoFileUploadException("Upload failed"));

    mockMvc
        .perform(
            multipart("/v1/files")
                .file(mockFile)
                .param("name", "Test File")
                .param("entityId", "12345")
                .param("entityType", "employee")
                .param("description", "Test description"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Constants.MONGO_UPLOAD_FAILED))
        .andDo(print());

    verify(fileService, times(1)).uploadFile(any(FileUploadRequest.class));
  }

  @Test
  public void testDownloadFile_SuccessfulDownload() throws Exception {
    String fileId = "12345";
    byte[] fileContent = "Sample file content".getBytes();
    ByteArrayResource resource =
        new ByteArrayResource(fileContent) {
          @Override
          public String getFilename() {
            return "sample.txt";
          }
        };

    FileDownloadResult mockResult = new FileDownloadResult(resource, "user1",  "entity1","org1","filename.pdf");

    when(fileService.downloadFile(fileId)).thenReturn(mockResult);

    mockMvc
        .perform(get("/v1/files/download/{fileId}", fileId))
        .andExpect(status().isOk())
        .andExpect(
            header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sample.txt\""))
        .andExpect(header().string("createdBy", "user1"))
        .andExpect(header().string("organizationId", "org1"))
        .andExpect(header().string("entityId", "entity1"))
        .andExpect(content().bytes(fileContent))
        .andDo(print());

    verify(fileService, times(1)).downloadFile(fileId);
  }

  @Test
  public void testDeleteFile_SuccessfulDeletion() throws Exception {

    String fileId = "12345";

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      Map<String, String> mockedOrganization = new HashMap<>();
      mockedOrganization.put("id", "org1");
      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockedOrganization);
      mockedUserContext.when(UserContext::getLoggedInEmployeeId).thenReturn("user1");
      mockedUserContext.when(UserContext::getLoggedInUserName).thenReturn("User One");

      File deletedFile =
          new File(
              fileId,
              "sample.txt",
              "text/plain",
              null,
              "1024",
              "entityId",
              "project",
              null,
              "org1",
              "user1",
              "User One",
              null,
              new Date(),
              new Date());

      when(fileService.deleteFile(fileId)).thenReturn(deletedFile);

      mockMvc
          .perform(delete("/v1/files/{fileId}", fileId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("12345"))
          .andExpect(jsonPath("$.name").value("sample.txt"))
          .andExpect(jsonPath("$.fileType").value("text/plain"))
          .andExpect(jsonPath("$.fileSize").value("1024"))
          .andExpect(jsonPath("$.entityId").value("entityId"))
          .andExpect(jsonPath("$.organizationId").value("org1"))
          .andDo(print());
      verify(fileService, times(1)).deleteFile(fileId);
    }
  }

  @Test
  public void testUpdateFile_SuccessfulUpdate() throws Exception {
    String fileId = "12345";
    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      Map<String, String> mockedOrganization = new HashMap<>();
      mockedOrganization.put("id", "org1");
      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockedOrganization);
      mockedUserContext.when(UserContext::getLoggedInEmployeeId).thenReturn("user1");
      mockedUserContext.when(UserContext::getLoggedInUserName).thenReturn("User One");
      FileUploadRequest fileUploadRequest = new FileUploadRequest();
      fileUploadRequest.setName("sample.txt");
      fileUploadRequest.setFileType("text/plain");
      fileUploadRequest.setEntityId("entityId");
      fileUploadRequest.setEntityType("project");
      fileUploadRequest.setDescription("Sample file description");
      File updatedFile =
          new File(
              fileId,
              "sample.txt",
              "text/plain",
              null,
              "1024",
              "entityId",
              "project",
              "Sample file description",
              "org1",
              "user1",
              "User One",
              null,
              new Date(),
              new Date());

      when(fileService.updateFile(fileId, fileUploadRequest)).thenReturn(updatedFile);

      mockMvc
          .perform(
              put("/v1/files/{fileId}", fileId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(fileUploadRequest)))
          .andExpect(status().isOk())
          .andDo(print());
      verify(fileService, times(1)).updateFile(any(), any());
    }
  }

  @Test
  public void testUploadOrUpdateFile_SuccessfulUpload() throws Exception {
    String fileName = "sample.txt";

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      Map<String, String> mockedOrganization = new HashMap<>();
      mockedOrganization.put("id", "org1");

      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockedOrganization);
      mockedUserContext.when(UserContext::getLoggedInEmployeeId).thenReturn("user1");
      mockedUserContext.when(UserContext::getLoggedInUserName).thenReturn("User One");
      MockMultipartFile mockFile =
          new MockMultipartFile("file", fileName, "text/plain", "Sample file content".getBytes());

      FileUploadRequest fileUploadRequest = new FileUploadRequest();
      fileUploadRequest.setName(fileName);
      fileUploadRequest.setFileType("text/plain");
      fileUploadRequest.setEntityId("entityId");
      fileUploadRequest.setEntityType("project");
      fileUploadRequest.setDescription("Sample description");
      fileUploadRequest.setFile(mockFile);

      File fileResponse =
          new File(
              "12345",
              fileName,
              "text/plain",
              null,
              "1024",
              "entityId",
              "project",
              "Sample description",
              "org1",
              "user1",
              "User One",
              null,
              new Date(),
              new Date());

      when(fileService.uploadOrUpdateFile(any(FileUploadRequest.class))).thenReturn(fileResponse);
      mockMvc
          .perform(
              multipart("/v1/files/dynamic")
                  .file(mockFile)
                  .param("name", fileName)
                  .param("fileType", "text/plain")
                  .param("entityId", "entityId")
                  .param("entityType", "project")
                  .param("description", "Sample description")
                  .contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("12345"))
          .andExpect(jsonPath("$.name").value(fileName))
          .andExpect(jsonPath("$.fileType").value("text/plain"))
          .andExpect(jsonPath("$.fileSize").value("1024"))
          .andExpect(jsonPath("$.entityId").value("entityId"))
          .andExpect(jsonPath("$.entityType").value("project"))
          .andExpect(jsonPath("$.description").value("Sample description"))
          .andDo(print());
    }
  }

  @Test
  public void testUploadUpdateFile_SuccessfulUpload() throws Exception {
    String fileName = "sample.txt";

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      Map<String, String> mockedOrganization = new HashMap<>();
      mockedOrganization.put("id", "org1");
      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockedOrganization);
      mockedUserContext.when(UserContext::getLoggedInEmployeeId).thenReturn("user1");
      mockedUserContext.when(UserContext::getLoggedInUserName).thenReturn("User One");
      MockMultipartFile mockFile =
          new MockMultipartFile("file", fileName, "text/plain", "Sample file content".getBytes());
      FileUploadRequest fileUploadRequest = new FileUploadRequest();
      fileUploadRequest.setName(fileName);
      fileUploadRequest.setFileType("text/plain");
      fileUploadRequest.setEntityId("entityId");
      fileUploadRequest.setEntityType("project");
      fileUploadRequest.setDescription("Sample description");
      fileUploadRequest.setFile(mockFile);

      File fileResponse =
          new File(
              "12345",
              fileName,
              "text/plain",
              null,
              "1024",
              "entityId",
              "project",
              "Sample description",
              "org1",
              "user1",
              "User One",
              null,
              new Date(),
              new Date());

      when(fileService.uploadOrUpdateFile(any(FileUploadRequest.class))).thenReturn(fileResponse);
      mockMvc
          .perform(
              multipart("/v1/files/dynamic")
                  .file(mockFile)
                  .param("name", fileName)
                  .param("fileType", "text/plain")
                  .param("entityId", "entityId")
                  .param("entityType", "project")
                  .param("description", "Sample description")
                  .contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("12345"))
          .andExpect(jsonPath("$.name").value(fileName))
          .andExpect(jsonPath("$.fileType").value("text/plain"))
          .andExpect(jsonPath("$.fileSize").value("1024"))
          .andExpect(jsonPath("$.entityId").value("entityId"))
          .andExpect(jsonPath("$.entityType").value("project"))
          .andExpect(jsonPath("$.description").value("Sample description"))
          .andDo(print());
    }
  }
}
