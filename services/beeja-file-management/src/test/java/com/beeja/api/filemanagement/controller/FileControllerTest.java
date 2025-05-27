package com.beeja.api.filemanagement.controller;

import com.beeja.api.filemanagement.exceptions.FileAccessException;
import com.beeja.api.filemanagement.exceptions.FileNotFoundException;
import com.beeja.api.filemanagement.exceptions.FileTypeMismatchException;
import com.beeja.api.filemanagement.exceptions.MongoFileUploadException;
import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.requests.FileUploadRequest;
import com.beeja.api.filemanagement.response.FileDownloadResult;
import com.beeja.api.filemanagement.response.FileResponse;
import com.beeja.api.filemanagement.service.FileService;
import com.beeja.api.filemanagement.utils.Constants;
import com.beeja.api.filemanagement.utils.UserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {

  @Mock
  private FileService fileService;

  @InjectMocks
  private FileController fileController;

  @Test
  void testGetAllFilesByEntityId_Success() throws Exception {
    String entityId = "123";
    int page = 0;
    int size = 10;
    FileResponse mockResponse = new FileResponse();

    when(fileService.listofFileByEntityId(entityId, page, size)).thenReturn(mockResponse);

    ResponseEntity<FileResponse> response = fileController.getAllFilesByEntityId(entityId, page, size);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockResponse, response.getBody());
    verify(fileService, times(1)).listofFileByEntityId(entityId, page, size);
  }

  @Test
  void testUploadFile_FileMissing() throws Exception {
    FileUploadRequest fileInput = new FileUploadRequest();

    ResponseEntity<?> response = fileController.uploadFile(fileInput);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(Constants.FILE_MISSING_IN_REQUEST_ERROR, response.getBody());
  }

  @Test
  void testUploadFile_Exception() throws Exception {
    FileUploadRequest fileInput = new FileUploadRequest();
    MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
    fileInput.setFile(file);
    when(fileService.uploadFile(fileInput)).thenThrow(new MongoFileUploadException("MongoDB error"));

    MongoFileUploadException thrown = assertThrows(
            MongoFileUploadException.class,
            () -> fileController.uploadFile(fileInput),
            "Expected uploadFile() to throw MongoFileUploadException, but it didn't"
    );
    assertEquals("MongoDB error", thrown.getMessage());
    verify(fileService, times(1)).uploadFile(fileInput);
  }


  @Test
  void testDeleteFile_FileNotFound() throws Exception {
    String fileId = "file123";
    when(fileService.deleteFile(fileId)).thenThrow(new FileNotFoundException("File not found"));
    FileNotFoundException thrown = assertThrows(
            FileNotFoundException.class,
            () -> fileController.deleteFile(fileId),
            "Expected deleteFile() to throw FileNotFoundException, but it didn't"
    );

    assertEquals("File not found", thrown.getMessage());
    verify(fileService, times(1)).deleteFile(fileId);
  }

  @Test
  void testUpdateFile_Success() throws Exception {
    String fileId = "file123";
    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    File mockFile = Mockito.mock(File.class);
    try (MockedStatic<UserContext> userContextMock = Mockito.mockStatic(UserContext.class)) {
      Map<String, Object> orgMap = new HashMap<>();
      orgMap.put("id", "testOrgId");
      userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);

      when(fileService.updateFile(fileId, fileUploadRequest)).thenReturn(mockFile);

      ResponseEntity<?> response = fileController.updateFile(fileId, fileUploadRequest);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(mockFile, response.getBody());
      verify(fileService, times(1)).updateFile(fileId, fileUploadRequest);
    }
  }

  @Test
  void testUpdateFile_Exception() throws Exception {
    String fileId = "file123";
    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    when(fileService.updateFile(fileId, fileUploadRequest)).thenThrow(new FileAccessException("File access error"));

    FileAccessException thrown = assertThrows(
            FileAccessException.class,
            () -> fileController.updateFile(fileId, fileUploadRequest),
            "Expected updateFile() to throw FileAccessException, but it didn't"
    );

    assertEquals("File access error", thrown.getMessage());
    verify(fileService, times(1)).updateFile(fileId, fileUploadRequest);

  }

  @Test
  void testGetFileById_Success() throws FileNotFoundException, java.io.FileNotFoundException {
    String fileId = "file123";
    File mockFile = Mockito.mock(File.class);
    try (MockedStatic<UserContext> userContextMock = Mockito.mockStatic(UserContext.class)) {
      Map<String, Object> orgMap = new HashMap<>();
      orgMap.put("id", "testOrgId");
      userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);
      when(fileService.getFileById(fileId)).thenReturn(mockFile);

      ResponseEntity<?> response = fileController.getFileById(fileId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(mockFile, response.getBody());
      verify(fileService, times(1)).getFileById(fileId);
    }
  }

  @Test
  void testGetFileById_FileNotFound() throws FileNotFoundException, java.io.FileNotFoundException {
    String fileId = "file123";
    when(fileService.getFileById(fileId)).thenThrow(new FileNotFoundException("File not found"));

    assertThrows(FileNotFoundException.class, () -> fileController.getFileById(fileId));
    verify(fileService, times(1)).getFileById(fileId);
  }

  @Test
  void testGetFileById_FileAccessException() throws FileNotFoundException, java.io.FileNotFoundException {
    String fileId = "file123";
    when(fileService.getFileById(fileId)).thenThrow(new FileAccessException("File access error"));

    assertThrows(FileAccessException.class, () -> fileController.getFileById(fileId));
    verify(fileService, times(1)).getFileById(fileId);
  }

  @Test
  void testUploadOrUpdateFile_ThrowsMongoFileUploadException() throws Exception {
    FileUploadRequest fileInput = new FileUploadRequest();
    MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "test content".getBytes()
    );
    fileInput.setFile(file);

    String expectedErrorMessage = "MongoDB error: Connection failed";

    when(fileService.uploadOrUpdateFile(fileInput)).thenThrow(new MongoFileUploadException(expectedErrorMessage));

    MongoFileUploadException thrown = assertThrows(
            MongoFileUploadException.class,
            () -> fileController.uploadOrUpdateFile(fileInput)
    );

    assertEquals(expectedErrorMessage, thrown.getMessage());
    verify(fileService, times(1)).uploadOrUpdateFile(fileInput);
  }

  @Test
  void testUploadFile_FileTypeMismatchException() throws Exception {
    FileUploadRequest fileInput = new FileUploadRequest();
    MockMultipartFile file = new MockMultipartFile("file", "badfile.exe", "application/octet-stream", "binary".getBytes());
    fileInput.setFile(file);

    when(fileService.uploadFile(fileInput)).thenThrow(new FileTypeMismatchException("Invalid file type"));

    assertThrows(FileTypeMismatchException.class, () -> fileController.uploadFile(fileInput));
    verify(fileService, times(1)).uploadFile(fileInput);
  }

  @Test
  void testUploadOrUpdateFile_FileMissing() throws Exception {
    FileUploadRequest fileInput = new FileUploadRequest();

    ResponseEntity<?> response = fileController.uploadOrUpdateFile(fileInput);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(Constants.FILE_MISSING_IN_REQUEST_ERROR, response.getBody());
  }



  @Test
  void testUploadOrUpdateFile_ThrowsGeneralException() throws Exception {
    FileUploadRequest fileInput = new FileUploadRequest();
    MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
    fileInput.setFile(file);

    String expectedErrorMessage = "General error";
    when(fileService.uploadOrUpdateFile(fileInput)).thenThrow(new Exception(expectedErrorMessage));

    Exception thrown = assertThrows(
            Exception.class, // Expecting a generic Exception
            () -> fileController.uploadOrUpdateFile(fileInput)
    );

    assertEquals(expectedErrorMessage, thrown.getMessage());

    verify(fileService, times(1)).uploadOrUpdateFile(fileInput);
  }

  @Test
  void testDownloadFile_Success() throws Exception {
    String fileId = "testFileId";
    ByteArrayResource mockResource = new ByteArrayResource("test data".getBytes()) {
      @Override
      public String getFilename() {
        return "testFile.txt";
      }
    };


    FileDownloadResult mockResult = new FileDownloadResult(
            mockResource,
            "testUser",
            "testEntity",
            "testOrg",
            "testFile.txt"
    );

    when(fileService.downloadFile(fileId)).thenReturn(mockResult);

    ResponseEntity<?> response = fileController.downloadFile(fileId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
    assertEquals("attachment; filename=\"testFile.txt\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
    assertEquals("testUser", response.getHeaders().getFirst("createdBy"));
    assertEquals("testOrg", response.getHeaders().getFirst("organizationId"));
    assertEquals("testEntity", response.getHeaders().getFirst("entityId"));
    assertEquals("testFile.txt", response.getHeaders().getFirst("fileName"));
    assertEquals(mockResource, ((ByteArrayResource) response.getBody()));

    verify(fileService, times(1)).downloadFile(fileId);
  }


  @Test
  void testDownloadFile_ThrowsException() throws Exception {
    String fileId = "testFileId";
    String expectedErrorMessage = "Download failed";

    when(fileService.downloadFile(fileId)).thenThrow(new Exception(expectedErrorMessage));

    Exception thrown = assertThrows(
            Exception.class,
            () -> fileController.downloadFile(fileId)
    );

    assertEquals(expectedErrorMessage, thrown.getMessage());

    verify(fileService, times(1)).downloadFile(fileId);
  }

  @Test
  void testUploadFile_FileMissing_FileIsNull() throws Exception {
    FileUploadRequest fileInput = new FileUploadRequest();
    fileInput.setFile(null);

    ResponseEntity<?> response = fileController.uploadFile(fileInput);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(Constants.FILE_MISSING_IN_REQUEST_ERROR, response.getBody());
  }

  @Test
  void testUploadOrUpdateFile_FileMissing_FileIsNull() throws Exception {
    FileUploadRequest fileInput = new FileUploadRequest();
    fileInput.setFile(null);

    ResponseEntity<?> response = fileController.uploadOrUpdateFile(fileInput);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(Constants.FILE_MISSING_IN_REQUEST_ERROR, response.getBody());
  }

}
