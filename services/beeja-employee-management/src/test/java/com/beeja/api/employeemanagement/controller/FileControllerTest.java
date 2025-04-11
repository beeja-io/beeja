package com.beeja.api.employeemanagement.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beeja.api.employeemanagement.client.AccountClient;
import com.beeja.api.employeemanagement.config.filters.JwtProperties;
import com.beeja.api.employeemanagement.model.Employee;
import com.beeja.api.employeemanagement.model.File;
import com.beeja.api.employeemanagement.requests.FileUploadRequest;
import com.beeja.api.employeemanagement.response.FileResponse;
import com.beeja.api.employeemanagement.service.EmployeeService;
import com.beeja.api.employeemanagement.service.FileService;
import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(FileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FileControllerTest {

  @Autowired MockMvc mockMvc;
  @MockBean JwtProperties jwtProperties;

  @MockBean AccountClient accountClient;
  @InjectMocks FileController fileController;

  @MockBean FileService fileService;

  @MockBean EmployeeService employeeService;

  @Test
  void testGetAllFilesOfEntityId_Success() throws Exception {
    String entityId = "12345";
    int page = 1;
    int size = 10;

    File file1 = new File();
    file1.setId("1");
    file1.setName("file1.txt");
    file1.setFileType("text");
    file1.setFileFormat("txt");
    file1.setFileSize("1024");
    file1.setEntityId(entityId);
    file1.setEntityType("employee");
    file1.setDescription("First file description");
    file1.setOrganizationId("org1");
    file1.setCreatedBy("user1");
    file1.setCreatedByName("User One");
    file1.setModifiedBy("user1");
    file1.setCreatedAt(new Date());
    file1.setModifiedAt(new Date());

    File file2 = new File();
    file2.setId("2");
    file2.setName("file2.pdf");
    file2.setFileType("document");
    file2.setFileFormat("pdf");
    file2.setFileSize("2048");
    file2.setEntityId(entityId);
    file2.setEntityType("employee");
    file2.setDescription("Second file description");
    file2.setOrganizationId("org1");
    file2.setCreatedBy("user2");
    file2.setCreatedByName("User Two");
    file2.setModifiedBy("user2");
    file2.setCreatedAt(new Date());
    file2.setModifiedAt(new Date());
    FileResponse fileResponse = new FileResponse();
    fileResponse.setFiles(Arrays.asList(file1, file2));
    fileResponse.setMetadata(null);

    when(fileService.listOfFileByEntityId(entityId, page, size)).thenReturn(fileResponse);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/v1/files/{entityId}", entityId)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.files").exists())
        .andExpect(jsonPath("$.files").isArray())
        .andExpect(jsonPath("$.files[0].name").value("file1.txt"))
        .andExpect(jsonPath("$.files[1].name").value("file2.pdf"))
        .andExpect(jsonPath("$.files[0].fileType").value("text"))
        .andExpect(jsonPath("$.files[1].fileFormat").value("pdf"))
        .andExpect(jsonPath("$.files[0].description").value("First file description"))
        .andExpect(jsonPath("$.files[1].description").value("Second file description"));
  }

  @Test
  void testUploadFile_Success() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "testfile.txt", "text/plain", "This is a test file".getBytes());

    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    fileUploadRequest.setFile(file);
    fileUploadRequest.setName("testfile.txt");
    fileUploadRequest.setFileType("text");
    fileUploadRequest.setEntityId("12345");
    fileUploadRequest.setEntityType("employee");
    fileUploadRequest.setDescription("Test file description");
    File mockFile = new File();
    mockFile.setId("1");
    mockFile.setName("testfile.txt");
    mockFile.setFileType("text");
    mockFile.setFileFormat("txt");
    mockFile.setFileSize("1024");
    mockFile.setEntityId("12345");
    mockFile.setEntityType("employee");
    mockFile.setDescription("Test file description");

    when(fileService.uploadFile(fileUploadRequest)).thenReturn(mockFile);

    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/v1/files")
                .file(file)
                .param("name", "testfile.txt")
                .param("fileType", "text")
                .param("entityId", "12345")
                .param("entityType", "employee")
                .param("description", "Test file description")
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("testfile.txt"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.fileType").value("text"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.entityId").value("12345"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.entityType").value("employee"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test file description"));
  }

  @Test
  void testDownloadFile_Success() throws Exception {
    String fileId = "12345";
    String fileName = "testfile.txt";
    byte[] fileContent = "This is the content of the file.".getBytes();
    ByteArrayResource resource =
        new ByteArrayResource(fileContent) {
          @Override
          public String getFilename() {
            return fileName;
          }
        };

    when(fileService.downloadFile(fileId)).thenReturn(resource);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/v1/files/download/{fileId}", fileId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.header()
                .string(
                    HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\""))
        .andExpect(MockMvcResultMatchers.content().bytes(fileContent))
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM));
  }

  @Test
  void testUploadOrUpdateProfilePic_Success() throws Exception {
    MockMultipartFile mockFile =
        new MockMultipartFile(
            "file", "profile-pic.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
    String entityId = "12345";

    Employee mockEmployee = new Employee();
    mockEmployee.setId("123");
    mockEmployee.setBeejaAccountId("beeja123");
    mockEmployee.setEmployeeId("EMP123");
    mockEmployee.setEmploymentType("Full-Time");
    mockEmployee.setOrganizationId("org123");
    mockEmployee.setProfilePictureId("pic123");

    when(employeeService.uploadOrUpdateProfilePic(mockFile, entityId)).thenReturn(mockEmployee);

    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/v1/files/profile-pic")
                .file(mockFile)
                .param("entityId", entityId)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("123"))
        .andExpect(jsonPath("$.beejaAccountId").value("beeja123"))
        .andExpect(jsonPath("$.employeeId").value("EMP123"))
        .andExpect(jsonPath("$.employmentType").value("Full-Time"))
        .andExpect(jsonPath("$.organizationId").value("org123"))
        .andExpect(jsonPath("$.profilePictureId").value("pic123"));

    verify(employeeService, times(1)).uploadOrUpdateProfilePic(mockFile, entityId);
  }

  @Test
  void testDeleteFileOfEmployee_Success() throws Exception {

    String fileId = "file123";
    File mockFile = new File();
    mockFile.setId(fileId);
    mockFile.setName("Test File");
    mockFile.setFileType("DOCUMENT");
    mockFile.setFileFormat("pdf");
    mockFile.setFileSize("1MB");
    mockFile.setEntityId("entity123");
    mockFile.setEntityType("Employee");
    mockFile.setDescription("Test description");
    mockFile.setOrganizationId("org123");
    mockFile.setCreatedBy("user123");
    mockFile.setCreatedByName("Test User");
    mockFile.setModifiedBy("user456");
    mockFile.setCreatedAt(new Date());
    mockFile.setModifiedAt(new Date());

    when(fileService.deleteFile(fileId)).thenReturn(mockFile);
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/v1/files/{fileId}", fileId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(fileId))
        .andExpect(jsonPath("$.name").value("Test File"))
        .andExpect(jsonPath("$.fileType").value("DOCUMENT"))
        .andExpect(jsonPath("$.fileFormat").value("pdf"))
        .andExpect(jsonPath("$.fileSize").value("1MB"))
        .andExpect(jsonPath("$.entityId").value("entity123"))
        .andExpect(jsonPath("$.entityType").value("Employee"))
        .andExpect(jsonPath("$.description").value("Test description"))
        .andExpect(jsonPath("$.organizationId").value("org123"))
        .andExpect(jsonPath("$.createdBy").value("user123"))
        .andExpect(jsonPath("$.createdByName").value("Test User"))
        .andExpect(jsonPath("$.modifiedBy").value("user456"));

    verify(fileService, times(1)).deleteFile(fileId);
  }

  @Test
  void testUpdateFileByFileId_Success() throws Exception {
    String fileId = "file123";
    FileUploadRequest mockRequest = new FileUploadRequest();
    mockRequest.setName("Updated File");
    mockRequest.setFileType("DOCUMENT");
    mockRequest.setEntityId("entity123");
    mockRequest.setEntityType("employee");
    mockRequest.setDescription("Updated description");

    MockMultipartFile mockFile =
        new MockMultipartFile(
            "file",
            "updated-file.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Updated file content".getBytes());
    File mockFileResponse = new File();
    mockFileResponse.setId(fileId);
    mockFileResponse.setName("Updated File");
    mockFileResponse.setFileType("DOCUMENT");
    mockFileResponse.setFileFormat("pdf");
    mockFileResponse.setEntityId("entity123");
    mockFileResponse.setEntityType("employee");
    mockFileResponse.setDescription("Updated description");
    mockFileResponse.setOrganizationId("org123");
    mockFileResponse.setCreatedBy("user123");
    mockFileResponse.setCreatedByName("Test User");
    mockFileResponse.setModifiedBy("user456");
    mockFileResponse.setCreatedAt(new Date());
    mockFileResponse.setModifiedAt(new Date());

    when(fileService.updateFile(eq(fileId), any(FileUploadRequest.class)))
        .thenReturn(mockFileResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/v1/files/{fileId}", fileId)
                .param("name", "Updated File")
                .param("fileType", "DOCUMENT")
                .param("entityId", "entity123")
                .param("entityType", "employee")
                .param("description", "Updated description"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(fileId))
        .andExpect(jsonPath("$.name").value("Updated File"))
        .andExpect(jsonPath("$.fileType").value("DOCUMENT"))
        .andExpect(jsonPath("$.fileFormat").value("pdf"))
        .andExpect(jsonPath("$.entityId").value("entity123"))
        .andExpect(jsonPath("$.entityType").value("employee"))
        .andExpect(jsonPath("$.description").value("Updated description"))
        .andExpect(jsonPath("$.organizationId").value("org123"))
        .andExpect(jsonPath("$.createdBy").value("user123"))
        .andExpect(jsonPath("$.createdByName").value("Test User"))
        .andExpect(jsonPath("$.modifiedBy").value("user456"));

    verify(fileService, times(1)).updateFile(eq(fileId), any(FileUploadRequest.class));
  }
}
