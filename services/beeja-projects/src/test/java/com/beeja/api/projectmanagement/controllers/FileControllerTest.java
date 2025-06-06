package com.beeja.api.projectmanagement.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beeja.api.projectmanagement.model.dto.File;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.responses.FileResponse;
import com.beeja.api.projectmanagement.service.FileService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {

  @Mock private FileService fileService;

  @InjectMocks private FileController fileController;

  private File mockFile;
  private FileUploadRequest mockFileUploadRequest;
  private ByteArrayResource mockByteArrayResource;
  private FileResponse mockFileResponse;

  @BeforeEach
  void setUp() {
    mockFile = new File();
    mockFile.setId("file123");
    mockFile.setName("testfile.txt");
    mockFile.setEntityId("entity456");

    mockFileUploadRequest = new FileUploadRequest();
    mockFileUploadRequest.setFile(
        new MockMultipartFile("file", "original.txt", "text/plain", "test data".getBytes()));
    mockFileUploadRequest.setName("uploaded_file");
    mockFileUploadRequest.setDescription("A test upload");
    mockFileUploadRequest.setFileType("PROJECT");
    mockFileUploadRequest.setEntityId("entity789");
    mockFileUploadRequest.setEntityType("PROJECT");

    mockByteArrayResource =
        new ByteArrayResource("file content".getBytes()) {
          @Override
          public String getFilename() {
            return "downloaded_file.txt";
          }
        };

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("totalElements", 1L);
    metadata.put("totalPages", 1);
    metadata.put("currentPage", 1);
    mockFileResponse = new FileResponse(metadata, Collections.singletonList(mockFile));
  }

  @Test
  void downloadFile_Success() throws Exception {
    when(fileService.downloadFile(anyString())).thenReturn(mockByteArrayResource);

    ResponseEntity<ByteArrayResource> response = fileController.downloadFile("file123");

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
    assertTrue(response.getHeaders().containsKey(HttpHeaders.CONTENT_DISPOSITION));
    assertTrue(
        response
            .getHeaders()
            .getFirst(HttpHeaders.CONTENT_DISPOSITION)
            .contains("filename=\"downloaded_file.txt\""));
    assertTrue(response.getHeaders().containsKey("Access-Control-Expose-Headers"));
    assertEquals(
        "Content-Disposition", response.getHeaders().getFirst("Access-Control-Expose-Headers"));
    assertEquals(mockByteArrayResource, response.getBody());
    verify(fileService).downloadFile("file123");
  }

  @Test
  void downloadFile_ServiceThrowsException() throws Exception {
    when(fileService.downloadFile(anyString())).thenThrow(new RuntimeException("File not found"));

    Exception exception = null;
    try {
      fileController.downloadFile("file123");
    } catch (Exception e) {
      exception = e;
    }

    assertNotNull(exception);
    assertTrue(exception instanceof RuntimeException);
    assertEquals("File not found", exception.getMessage());
    verify(fileService).downloadFile("file123");
  }

  @Test
  void getAllFilesOfEntityId_Success() throws Exception {
    when(fileService.listOfFileByEntityId(anyString(), anyInt(), anyInt()))
        .thenReturn(mockFileResponse);

    ResponseEntity<FileResponse> response =
        fileController.getAllFilesOfEntityId("entity456", 1, 10);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockFileResponse, response.getBody());
    verify(fileService).listOfFileByEntityId("entity456", 1, 10);
  }

  @Test
  void getAllFilesOfEntityId_ServiceThrowsException() throws Exception {
    when(fileService.listOfFileByEntityId(anyString(), anyInt(), anyInt()))
        .thenThrow(new RuntimeException("Database error"));

    Exception exception = null;
    try {
      fileController.getAllFilesOfEntityId("entity456", 1, 10);
    } catch (Exception e) {
      exception = e;
    }

    assertNotNull(exception);
    assertTrue(exception instanceof RuntimeException);
    assertEquals("Database error", exception.getMessage());
    verify(fileService).listOfFileByEntityId("entity456", 1, 10);
  }

  @Test
  void uploadFile_Success() throws Exception {
    when(fileService.uploadFile(any(FileUploadRequest.class))).thenReturn(mockFile);

    ResponseEntity<File> response = fileController.uploadFile(mockFileUploadRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockFile, response.getBody());
    verify(fileService).uploadFile(mockFileUploadRequest);
  }

  @Test
  void uploadFile_ServiceThrowsException() throws Exception {
    when(fileService.uploadFile(any(FileUploadRequest.class)))
        .thenThrow(new RuntimeException("Upload failed"));

    Exception exception = null;
    try {
      fileController.uploadFile(mockFileUploadRequest);
    } catch (Exception e) {
      exception = e;
    }

    assertNotNull(exception);
    assertTrue(exception instanceof RuntimeException);
    assertEquals("Upload failed", exception.getMessage());
    verify(fileService).uploadFile(mockFileUploadRequest);
  }

  @Test
  void deleteFile_Success() throws Exception {
    when(fileService.deleteFile(anyString())).thenReturn(mockFile);

    ResponseEntity<File> response = fileController.deleteFile("file123");

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockFile, response.getBody());
    verify(fileService).deleteFile("file123");
  }

  @Test
  void deleteFile_ServiceThrowsException() throws Exception {
    when(fileService.deleteFile(anyString())).thenThrow(new RuntimeException("Deletion failed"));

    Exception exception = null;
    try {
      fileController.deleteFile("file123");
    } catch (Exception e) {
      exception = e;
    }

    assertNotNull(exception);
    assertTrue(exception instanceof RuntimeException);
    assertEquals("Deletion failed", exception.getMessage());
    verify(fileService).deleteFile("file123");
  }

  @Test
  void updateFileByFileId_Success() throws Exception {
    when(fileService.updateFile(anyString(), any(FileUploadRequest.class))).thenReturn(mockFile);

    ResponseEntity<File> response =
        fileController.updateFileByFileId("file123", mockFileUploadRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockFile, response.getBody());
    verify(fileService).updateFile("file123", mockFileUploadRequest);
  }

  @Test
  void updateFileByFileId_ServiceThrowsException() throws Exception {
    when(fileService.updateFile(anyString(), any(FileUploadRequest.class)))
        .thenThrow(new RuntimeException("Update failed"));

    Exception exception = null;
    try {
      fileController.updateFileByFileId("file123", mockFileUploadRequest);
    } catch (Exception e) {
      exception = e;
    }

    assertNotNull(exception);
    assertTrue(exception instanceof RuntimeException);
    assertEquals("Update failed", exception.getMessage());
    verify(fileService).updateFile("file123", mockFileUploadRequest);
  }
}
