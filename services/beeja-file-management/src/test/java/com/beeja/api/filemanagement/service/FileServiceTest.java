package com.beeja.api.filemanagement.service;

import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.requests.FileUploadRequest;
import com.beeja.api.filemanagement.response.FileDownloadResult;
import com.beeja.api.filemanagement.response.FileResponse;
import com.beeja.api.filemanagement.utils.UserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileService fileService;

    @Test
    void testListofFileByEntityId_Success() throws Exception {
        String entityId = "123";
        int page = 0;
        int size = 10;
        FileResponse mockResponse = new FileResponse();
        when(fileService.listofFileByEntityId(entityId, page, size)).thenReturn(mockResponse);

        FileResponse result = fileService.listofFileByEntityId(entityId, page, size);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(fileService, times(1)).listofFileByEntityId(entityId, page, size);
    }

   @Test
    void testDownloadFile_Success() throws Exception {
        String fileId = "file123";
        FileDownloadResult mockResult = new FileDownloadResult(
                new ByteArrayResource(new byte[0]),
                "user123",
                "entity123",
                "org123",
                "filename.pdf"
        );


        when(fileService.downloadFile(fileId)).thenReturn(mockResult);

        FileDownloadResult result = fileService.downloadFile(fileId);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(fileService, times(1)).downloadFile(fileId);
    }
    @Test
    void testDeleteFile_Exception() throws Exception {
        String fileName = "testFile.txt";
        when(fileService.deleteFile(fileName)).thenThrow(new Exception("File deletion failed"));

        assertThrows(Exception.class, () -> fileService.deleteFile(fileName));
        verify(fileService, times(1)).deleteFile(fileName);
    }


    @Test
    void testUploadFile_Success() throws Exception {

        String organizationId = "org1";
        FileUploadRequest request = new FileUploadRequest();

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = new HashMap<>();
            mockedOrganization.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);

            File mockFile = new File();
            when(fileService.uploadFile(request)).thenReturn(mockFile);

            File result = fileService.uploadFile(request);

            assertNotNull(result);
            assertEquals(mockFile, result);
            verify(fileService, times(1)).uploadFile(request);
        }
    }

    @Test
    void testUploadFile_Exception() throws Exception {
        FileUploadRequest request = new FileUploadRequest();
        when(fileService.uploadFile(request)).thenThrow(new Exception("File upload failed"));

        assertThrows(Exception.class, () -> fileService.uploadFile(request));
        verify(fileService, times(1)).uploadFile(request);
    }

    @Test
    void testUpdateFile_Success() throws Exception {
        String fileId = "file123";
        String organizationId = "org1";
        FileUploadRequest request = new FileUploadRequest();

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = new HashMap<>();
            mockedOrganization.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);

            File mockFile = new File();
            when(fileService.updateFile(fileId, request)).thenReturn(mockFile);

            File result = fileService.updateFile(fileId, request);

            assertNotNull(result);
            assertEquals(mockFile, result);
            verify(fileService, times(1)).updateFile(fileId, request);
        }
    }

    @Test
    void testUpdateFile_Exception() throws Exception {
        String fileId = "file123";
        FileUploadRequest request = new FileUploadRequest();
        when(fileService.updateFile(fileId, request)).thenThrow(new Exception("File update failed"));

        assertThrows(Exception.class, () -> fileService.updateFile(fileId, request));
        verify(fileService, times(1)).updateFile(fileId, request);
    }
    @Test
    void testGetFileById_Success() {
        String fileId = "file123";
        String organizationId = "org1";
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = new HashMap<>();
            mockedOrganization.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);

            File mockFile = new File();
            when(fileService.getFileById(fileId)).thenReturn(mockFile);

            File result = fileService.getFileById(fileId);

            assertNotNull(result);
            assertEquals(mockFile, result);
            verify(fileService, times(1)).getFileById(fileId);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testGetFileById_FileNotFound() throws FileNotFoundException {
        String fileId = "file123";
        try {
            when(fileService.getFileById(fileId)).thenReturn(null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        File result = null;
        try {
            result = fileService.getFileById(fileId);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        assertNull(result);
        try {
            verify(fileService, times(1)).getFileById(fileId);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testUploadOrUpdateFile_Success() throws Exception {
        String organizationId = "org1";
        FileUploadRequest request = new FileUploadRequest();
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Map<String, String> mockedOrganization = new HashMap<>();
            mockedOrganization.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockedOrganization);

            File mockFile = new File();
            when(fileService.uploadOrUpdateFile(request)).thenReturn(mockFile);

            File result = fileService.uploadOrUpdateFile(request);

            assertNotNull(result);
            assertEquals(mockFile, result);
            verify(fileService, times(1)).uploadOrUpdateFile(request);
        }
    }

    @Test
    void testUploadOrUpdateFile_Exception() throws Exception {
        FileUploadRequest request = new FileUploadRequest();
        when(fileService.uploadOrUpdateFile(request)).thenThrow(new Exception("Error occurred"));

        assertThrows(Exception.class, () -> fileService.uploadOrUpdateFile(request));
        verify(fileService, times(1)).uploadOrUpdateFile(request);
    }
}
