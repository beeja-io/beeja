package com.beeja.api.expense.service;

import com.beeja.api.expense.client.FileClient;
import com.beeja.api.expense.exceptions.FeignClientException;
import com.beeja.api.expense.response.FileDownloadResultMetaData;
import com.beeja.api.expense.response.FileResponse;
import com.beeja.api.expense.serviceImpl.ReceiptServiceImpl;
import com.beeja.api.expense.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReceiptServiceImplTest {

  @Mock private FileClient fileClient;

  @InjectMocks private ReceiptServiceImpl receiptService;

  private String fileId;
  private FileResponse fileResponse;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    fileId = "12345";
    fileResponse = new FileResponse();
    fileResponse.setEntityType("expense");
  }

  @Test
  void testGetFileById_Success() {
    String fileId = "sampleFileId";
    LinkedHashMap<String, Object> responseBody = new LinkedHashMap<>();
    responseBody.put("entityType", "expense");
    ResponseEntity<?> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    doReturn(mockResponse).when(fileClient).getFileById(fileId);
    ResponseEntity<?> response = fileClient.getFileById(fileId);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(responseBody, response.getBody());
    verify(fileClient, times(1)).getFileById(fileId);
  }

  @Test
  void testDownloadFile_UnauthorisedAccess() throws Exception {
    LinkedHashMap<String, Object> body = new LinkedHashMap<>();
    body.put("entityType", "otherEntity");
    final String expectedErrorMessage = "FEIGN_CLIENT_ERROR,FILE_SERVICE_COMMUNICATION_FAILED,Failed to fetch file information from file service.";
    ResponseEntity<?> mockGetFileResponse = new ResponseEntity<>(body, HttpStatus.OK);
    doReturn(mockGetFileResponse).when(fileClient).getFileById(fileId);
    FeignClientException exception =
        assertThrows(
            FeignClientException.class,
            () -> {
              receiptService.downloadFile(fileId);
            });
    assertEquals(expectedErrorMessage, exception.getMessage());
    verify(fileClient, times(1)).getFileById(fileId);
    verify(fileClient, never()).downloadFile(fileId);
  }

  @Test
  void testDownloadFile_FeignClientException() throws Exception {
    when(fileClient.getFileById(fileId)).thenThrow(new RuntimeException("FEIGN_CLIENT_ERROR,FILE_SERVICE_COMMUNICATION_FAILED,Failed to fetch file information from file service."));
    FeignClientException exception =
        assertThrows(
            FeignClientException.class,
            () -> {
              receiptService.downloadFile(fileId);
            });
    assertEquals("FEIGN_CLIENT_ERROR,FILE_SERVICE_COMMUNICATION_FAILED,Failed to fetch file information from file service.", exception.getMessage());
    verify(fileClient, times(1)).getFileById(fileId);
    verify(fileClient, never()).downloadFile(fileId);
  }

  @Test
  public void testGetMetaData_Success()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test_file.pdf\"");
    headers.add("createdby", "user123");
    ResponseEntity<byte[]> mockResponse = ResponseEntity.ok().headers(headers).body(new byte[0]);
    Method getMetaDataMethod =
        ReceiptServiceImpl.class.getDeclaredMethod("getMetaData", ResponseEntity.class);
    getMetaDataMethod.setAccessible(true);
    FileDownloadResultMetaData result =
        (FileDownloadResultMetaData) getMetaDataMethod.invoke(null, mockResponse);
    assertNotNull(result, "Result should not be null.");
    assertEquals("test_file.pdf", result.getFileName(), "File name should match.");
    assertEquals("user123", result.getCreatedBy(), "Created by should match.");
  }

  @Test
  void testDownloadFile_Success() throws Exception {
    LinkedHashMap<String, Object> responseBody = new LinkedHashMap<>();
    responseBody.put("entityType", "expense");
    ResponseEntity<?> mockGetFileResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    ResponseEntity<byte[]> mockDownloadResponse =
        ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test_file.pdf\"")
            .body("Test file content".getBytes());
    doReturn(mockGetFileResponse).when(fileClient).getFileById(fileId);
    doReturn(mockDownloadResponse).when(fileClient).downloadFile(fileId);
    ByteArrayResource resource = receiptService.downloadFile(fileId);
    assertNotNull(resource, "Resource should not be null.");
    assertEquals(
        "test_file.pdf",
        resource.getFilename(),
        "Filename should match the Content-Disposition header.");
    assertEquals(
        "Test file content", new String(resource.getByteArray()), "File content should match.");
    verify(fileClient, times(1)).getFileById(fileId);
    verify(fileClient, times(1)).downloadFile(fileId);
  }
}
