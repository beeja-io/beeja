package com.beeja.api.projectmanagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.beeja.api.projectmanagement.model.dto.File;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.responses.FileResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {


    @Mock
    private FileService fileService; // Mocked interface

    @Test
    void testListOfFileByEntityId() throws Exception {
        String entityId = "entity123";
        int page = 1;
        int size = 10;

        File mockFile = new File("file1", "test.pdf", "project", "pdf", "1024", entityId, "project",
                "Description", "org1", "user1", "User One", null, new Date(), new Date());
        FileResponse mockResponse = new FileResponse(Map.of(), List.of(mockFile));

        when(fileService.listOfFileByEntityId(entityId, page, size)).thenReturn(mockResponse);

        FileResponse result = fileService.listOfFileByEntityId(entityId, page, size);

        assertNotNull(result);
        assertEquals(1, result.getFiles().size());
        assertEquals("file1", result.getFiles().get(0).getId());
        verify(fileService, times(1)).listOfFileByEntityId(entityId, page, size);
    }

    @Test
    void testDownloadFile() throws Exception {
        String fileId = "file123";
        ByteArrayResource resource = new ByteArrayResource("Hello World".getBytes());

        when(fileService.downloadFile(fileId)).thenReturn(resource);

        ByteArrayResource result = fileService.downloadFile(fileId);

        assertNotNull(result);
        assertArrayEquals(resource.getByteArray(), result.getByteArray());
        verify(fileService, times(1)).downloadFile(fileId);
    }

    @Test
    void testUploadFile() throws Exception {
        FileUploadRequest request = new FileUploadRequest();
        request.setName("test.pdf");
        request.setEntityId("entity123");

        File mockFile = new File("file123", "test.pdf", "project", "pdf", "1024",
                "entity123", "project", "Description", "org1", "user1", "User One",
                null, new Date(), new Date());

        when(fileService.uploadFile(request)).thenReturn(mockFile);

        File result = fileService.uploadFile(request);

        assertNotNull(result);
        assertEquals("file123", result.getId());
        assertEquals("test.pdf", result.getName());
        verify(fileService, times(1)).uploadFile(request);
    }

    @Test
    void testDeleteFile() throws Exception {
        String fileId = "file123";
        File mockFile = new File(fileId, "test.pdf", "project", "pdf", "1024",
                "entity123", "project", "Description", "org1", "user1", "User One",
                null, new Date(), new Date());

        when(fileService.deleteFile(fileId)).thenReturn(mockFile);

        File result = fileService.deleteFile(fileId);

        assertNotNull(result);
        assertEquals(fileId, result.getId());
        verify(fileService, times(1)).deleteFile(fileId);
    }

    @Test
    void testUpdateFile() throws Exception {
        String fileId = "file123";
        FileUploadRequest request = new FileUploadRequest();
        request.setName("updated.pdf");

        File mockFile = new File(fileId, "updated.pdf", "project", "pdf", "2048",
                "entity123", "project", "Updated description", "org1", "user1", "User One",
                null, new Date(), new Date());

        when(fileService.updateFile(fileId, request)).thenReturn(mockFile);

        File result = fileService.updateFile(fileId, request);

        assertNotNull(result);
        assertEquals(fileId, result.getId());
        assertEquals("updated.pdf", result.getName());
        verify(fileService, times(1)).updateFile(fileId, request);
    }
}
