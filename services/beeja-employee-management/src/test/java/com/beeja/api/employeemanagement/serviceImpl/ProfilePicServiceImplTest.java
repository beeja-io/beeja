package com.beeja.api.employeemanagement.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.beeja.api.employeemanagement.model.File;
import com.beeja.api.employeemanagement.requests.FileUploadRequest;
import com.beeja.api.employeemanagement.service.FileService;
import com.beeja.api.employeemanagement.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProfilePicServiceImplTest {

  @InjectMocks private ProfilePicServiceImpl profilePicService;

  @MockBean private FileService fileService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSaveProfilePicture() throws Exception {
    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    fileUploadRequest.setFileType(null);
    fileUploadRequest.setEntityType(null);
    fileUploadRequest.setEntityId(null);

    File expectedFile = new File();
    expectedFile.setId("12345");

    when(fileService.uploadFile(any(FileUploadRequest.class))).thenReturn(expectedFile);

    File result = profilePicService.saveProfilePicture(fileUploadRequest);

    assertNotNull(result);
    assertEquals("12345", result.getId());
    verify(fileService, times(1)).uploadFile(any(FileUploadRequest.class));
  }

  @Test
  void testSaveProfilePicture_Exception() throws Exception {
    FileUploadRequest fileUploadRequest = new FileUploadRequest();

    when(fileService.uploadFile(any(FileUploadRequest.class)))
        .thenThrow(new RuntimeException("File upload error"));

    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              profilePicService.saveProfilePicture(fileUploadRequest);
            });

    Assertions.assertEquals(
        Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE, exception.getMessage());
    verify(fileService, times(1)).uploadFile(fileUploadRequest);
  }

  @Test
  void testUpdateProfilePicture() throws Exception {
    String fileId = "file123";
    FileUploadRequest fileUploadRequest = new FileUploadRequest();

    File expectedFile = new File();
    when(fileService.updateFile(eq(fileId), any(FileUploadRequest.class))).thenReturn(expectedFile);

    File result = profilePicService.updateProfilePicture(fileId, fileUploadRequest);

    assertNotNull(result);
    assertEquals(expectedFile, result);
    verify(fileService, times(1)).updateFile(fileId, fileUploadRequest);
  }

  @Test
  void testUpdateProfilePicture_Exception() throws Exception {
    String fileId = "file123";
    FileUploadRequest fileUploadRequest = new FileUploadRequest();

    when(fileService.updateFile(eq(fileId), any(FileUploadRequest.class)))
        .thenThrow(new RuntimeException("Update error"));

    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              profilePicService.updateProfilePicture(fileId, fileUploadRequest);
            });

    assertEquals(Constants.ERROR_IN_UPDATING_FILE_IN_FILE_SERVICE, exception.getMessage());
    verify(fileService, times(1)).updateFile(fileId, fileUploadRequest);
  }

  @Test
  void testGetProfilePicById() throws Exception {
    String fileId = "file123";
    ByteArrayResource expectedResource = new ByteArrayResource("imageData".getBytes());

    when(fileService.downloadFile(eq(fileId))).thenReturn(expectedResource);

    ByteArrayResource result = profilePicService.getProfilePicById(fileId);

    assertNotNull(result);
    assertEquals(expectedResource, result);
    verify(fileService, times(1)).downloadFile(fileId);
  }

  @Test
  void testGetProfilePicById_Exception() throws Exception {
    String fileId = "file123";

    when(fileService.downloadFile(eq(fileId))).thenThrow(new RuntimeException("Download error"));

    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              profilePicService.getProfilePicById(fileId);
            });

    assertEquals(Constants.ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE, exception.getMessage());
    verify(fileService, times(1)).downloadFile(fileId);
  }
}
