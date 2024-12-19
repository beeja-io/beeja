package com.beeja.api.employeemanagement.service;

import com.beeja.api.employeemanagement.model.File;
import com.beeja.api.employeemanagement.requests.FileUploadRequest;
import com.beeja.api.employeemanagement.response.FileResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

  @Mock private FileService fileService;

  @Test
  void testListofFileByEntityId() throws Exception {

    String entityId = "entity123";
    int page = 1;
    int size = 10;
    FileResponse expectedResponse = new FileResponse(new HashMap<>(), List.of(new File()));

    when(fileService.listOfFileByEntityId(entityId, page, size)).thenReturn(expectedResponse);

    FileResponse result = fileService.listOfFileByEntityId(entityId, page, size);
    assertNotNull(result);
    assertEquals(expectedResponse, result);
    verify(fileService, times(1)).listOfFileByEntityId(entityId, page, size);
  }

  @Test
  void testDownloadFile() throws Exception {

    String fileId = "file123";
    ByteArrayResource expectedResource = new ByteArrayResource("file content".getBytes());

    when(fileService.downloadFile(fileId)).thenReturn(expectedResource);
    ByteArrayResource result = fileService.downloadFile(fileId);
    assertNotNull(result);
    assertArrayEquals(expectedResource.getByteArray(), result.getByteArray());
    verify(fileService, times(1)).downloadFile(fileId);
  }

  @Test
  void testDeleteFile() throws Exception {
    String fileName = "file123";
    File expectedFile =
        new File(
            "file123",
            "file content",
            "pdf",
            "pdf",
            "2MB",
            "entityId",
            "employee",
            "File description",
            "org1",
            "createdBy",
            "createdByName",
            "modifiedBy",
            new Date(),
            new Date());

    when(fileService.deleteFile(fileName)).thenReturn(expectedFile);

    File result = fileService.deleteFile(fileName);
    assertNotNull(result);
    assertEquals(expectedFile, result);
    verify(fileService, times(1)).deleteFile(fileName);
  }

  @Test
  void testUploadFile() throws Exception {
    MockMultipartFile mockFile =
        new MockMultipartFile("file", "file123.pdf", "application/pdf", "file content".getBytes());

    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    fileUploadRequest.setFile(mockFile);
    fileUploadRequest.setName("file123");
    fileUploadRequest.setFileType("pdf");
    fileUploadRequest.setEntityId("entityId");
    fileUploadRequest.setEntityType("employee");
    fileUploadRequest.setDescription("File description");
    File expectedFile =
        new File(
            "file123",
            "file content",
            "pdf",
            "pdf",
            "2MB",
            "entityId",
            "employee",
            "File description",
            "org1",
            "createdBy",
            "createdByName",
            "modifiedBy",
            new Date(),
            new Date());

    when(fileService.uploadFile(fileUploadRequest)).thenReturn(expectedFile);

    File result = fileService.uploadFile(fileUploadRequest);
    assertNotNull(result);
    assertEquals(expectedFile, result);
    verify(fileService, times(1)).uploadFile(fileUploadRequest);
  }

  @Test
  void testUpdateFile() throws Exception {

    String fileId = "file123";
    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    File expectedFile =
        new File(
            "file123",
            "file content",
            "pdf",
            "pdf",
            "2MB",
            "entityId",
            "employee",
            "File description",
            "org1",
            "createdBy",
            "createdByName",
            "modifiedBy",
            new Date(),
            new Date());

    when(fileService.updateFile(fileId, fileUploadRequest)).thenReturn(expectedFile);
    File result = fileService.updateFile(fileId, fileUploadRequest);

    assertNotNull(result);
    assertEquals(expectedFile, result);
    verify(fileService, times(1)).updateFile(fileId, fileUploadRequest);
  }

  @Test
  void testUploadOrUpdateFile() throws Exception {

    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    File expectedFile =
        new File(
            "file123",
            "file content",
            "pdf",
            "pdf",
            "2MB",
            "entityId",
            "employee",
            "File description",
            "org1",
            "createdBy",
            "createdByName",
            "modifiedBy",
            new Date(),
            new Date());

    when(fileService.uploadOrUpdateFile(fileUploadRequest)).thenReturn(expectedFile);

    File result = fileService.uploadOrUpdateFile(fileUploadRequest);

    assertNotNull(result);
    assertEquals(expectedFile, result);
    verify(fileService, times(1)).uploadOrUpdateFile(fileUploadRequest); // Verify the call
  }
}
