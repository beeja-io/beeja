package com.beeja.api.expense.controller;

import com.beeja.api.expense.controllers.ReceiptController;
import com.beeja.api.expense.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ReceiptControllerTest {

  @InjectMocks private ReceiptController receiptController;

  @Mock private ReceiptService receiptService;

  private ByteArrayResource byteArrayResource;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    byteArrayResource =
        new ByteArrayResource("Sample content".getBytes()) {
          @Override
          public String getFilename() {
            return "sample-receipt.pdf";
          }
        };
  }

  @Test
  void downloadFile_ShouldReturnFile() throws Exception {
    String fileId = "12345";
    when(receiptService.downloadFile(fileId)).thenReturn(byteArrayResource);
    ResponseEntity<?> responseEntity = receiptController.downloadFile(fileId);
    HttpHeaders expectedHeaders = createHeaders("sample-receipt.pdf");
    ResponseEntity<ByteArrayResource> expectedResponse =
        ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .headers(expectedHeaders)
            .body(byteArrayResource);
    assertEquals(expectedResponse.getStatusCode(), responseEntity.getStatusCode());
    assertEquals(expectedResponse.getHeaders(), responseEntity.getHeaders());
    assertEquals(expectedResponse.getBody(), responseEntity.getBody());
  }

  private HttpHeaders createHeaders(String filename) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
    return headers;
  }
}
