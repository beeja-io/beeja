package com.beeja.api.filemanagement.serviceImpl;

import com.beeja.api.filemanagement.config.properties.DefaultStorageProperties;
import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.utils.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import com.beeja.api.filemanagement.utils.UserContext; // Import UserContext
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.beeja.api.filemanagement.utils.helpers.FileExtensionHelpers.FilePathGenerator.generateFilePath;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultFileStorageServiceTest {

    @InjectMocks
    private DefaultFileStorageService defaultFileStorageService;

    @Mock
    private DefaultStorageProperties storageDirectory;

    private Path tempStoragePath;
    private MockedStatic<UserContext> mockedUserContext;

    @BeforeEach
    void setUp() throws IOException {
        tempStoragePath = Files.createTempDirectory("test-storage");
        lenient().when(storageDirectory.getPath()).thenReturn(tempStoragePath.toString());

        mockedUserContext = mockStatic(UserContext.class);

        Map<String, Object> mockOrg = new HashMap<>();
        mockOrg.put("id", "mockOrgId");

        mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrg);
        mockedUserContext.when(UserContext::getLoggedInEmployeeId).thenReturn("mockEmpId");
        mockedUserContext.when(UserContext::getLoggedInUserName).thenReturn("Mock User");
    }

    @AfterEach
    void tearDown() {
        if (mockedUserContext != null) {
            mockedUserContext.close();
        }
    }
    @Test
    void testUploadFile_Success() throws IOException {
        // Setup UserContext before creating File
        Map<String, Object> mockOrgContext = new HashMap<>();
        mockOrgContext.put("id", "org123");
        UserContext.setLoggedInUserOrganization(mockOrgContext);

        MultipartFile multipartFile = new MockMultipartFile("test.txt", "test.txt", "text/plain", "test content".getBytes());
        File savedFile = new File();
        savedFile.setId("file123");
        savedFile.setEntityId("entity123");
        savedFile.setOrganizationId("org123");

        defaultFileStorageService.uploadFile(multipartFile, savedFile);

        String filePath = generateFilePath(savedFile) + ".txt";
        Path destinationPath = Paths.get(tempStoragePath.toString(), filePath);
        assertTrue(Files.exists(destinationPath));
        assertEquals("test content", new String(Files.readAllBytes(destinationPath)));

        // Clear UserContext after the test
        UserContext.setLoggedInUserOrganization(null);
    }

    @Test
    void testUploadFile_EmptyFile() {
        MultipartFile multipartFile = new MockMultipartFile("test.txt", new byte[0]);
        File savedFile = new File();

        assertThrows(IOException.class, () -> defaultFileStorageService.uploadFile(multipartFile, savedFile));
    }

    @Test
    void testDownloadFile_Success() throws IOException {
        // Setup UserContext before creating File
        Map<String, Object> mockOrgContext = new HashMap<>();
        mockOrgContext.put("id", "org123");
        UserContext.setLoggedInUserOrganization(mockOrgContext);

        File file = new File();
        file.setId("file123");
        file.setEntityId("entity123");
        file.setOrganizationId("org123");
        file.setFileFormat("txt");

        String filePath = generateFilePath(file) + ".txt";
        Path destinationPath = Paths.get(tempStoragePath.toString(), filePath);
        Files.createDirectories(destinationPath.getParent());
        Files.write(destinationPath, "test content".getBytes());

        byte[] downloadedContent = defaultFileStorageService.downloadFile(file);
        assertEquals("test content", new String(downloadedContent));

        // Clear UserContext after the test
        UserContext.setLoggedInUserOrganization(null);
    }

    @Test
    void testDownloadFile_FileNotFound() {
        // Setup UserContext before creating File
        Map<String, Object> mockOrgContext = new HashMap<>();
        mockOrgContext.put("id", "org123");
        UserContext.setLoggedInUserOrganization(mockOrgContext);

        File file = new File();
        file.setId("file123");
        file.setEntityId("entity123");
        file.setOrganizationId("org123");
        file.setFileFormat("txt");

        assertThrows(FileNotFoundException.class, () -> defaultFileStorageService.downloadFile(file));

        // Clear UserContext after the test
        UserContext.setLoggedInUserOrganization(null);
    }

    @Test
    void testDeleteFile_Success() throws IOException {
        // Setup UserContext before creating File
        Map<String, Object> mockOrgContext = new HashMap<>();
        mockOrgContext.put("id", "org123");
        UserContext.setLoggedInUserOrganization(mockOrgContext);

        File file = new File();
        file.setId("file123");
        file.setEntityId("entity123");
        file.setOrganizationId("org123");
        file.setFileFormat("txt");

        String filePath = generateFilePath(file) + ".txt";
        Path destinationPath = Paths.get(tempStoragePath.toString(), filePath);
        Files.createDirectories(destinationPath.getParent());
        Files.write(destinationPath, "test content".getBytes());

        defaultFileStorageService.deleteFile(file);
        assertFalse(Files.exists(destinationPath));

        // Clear UserContext after the test
        UserContext.setLoggedInUserOrganization(null);
    }

    @Test
    void testDeleteFile_FileNotFound() {
        // Setup UserContext before creating File
        Map<String, Object> mockOrgContext = new HashMap<>();
        mockOrgContext.put("id", "org123");
        UserContext.setLoggedInUserOrganization(mockOrgContext);

        File file = new File();
        file.setId("file123");
        file.setEntityId("entity123");
        file.setOrganizationId("org123");
        file.setFileFormat("txt");

        assertThrows(IOException.class, () -> defaultFileStorageService.deleteFile(file)); // Changed to IOException

        // Clear UserContext after the test
        UserContext.setLoggedInUserOrganization(null);
    }

    @Test
    void testUpdateFile_Success() throws IOException {
        // Setup UserContext before creating File
        Map<String, Object> mockOrgContext = new HashMap<>();
        mockOrgContext.put("id", "org123");
        UserContext.setLoggedInUserOrganization(mockOrgContext);

        File file = new File();
        file.setId("file123");
        file.setEntityId("entity123");
        file.setOrganizationId("org123");
        file.setFileFormat("txt");

        String filePath = generateFilePath(file) + ".txt";
        Path destinationPath = Paths.get(tempStoragePath.toString(), filePath);
        Files.createDirectories(destinationPath.getParent());
        Files.write(destinationPath, "old content".getBytes());

        MultipartFile newFile = new MockMultipartFile("updated.txt", "updated.txt", "text/plain", "new content".getBytes());
        defaultFileStorageService.updateFile(file, newFile);

        assertTrue(Files.exists(destinationPath));
        assertEquals("new content", new String(Files.readAllBytes(destinationPath)));

        // Clear UserContext after the test
        UserContext.setLoggedInUserOrganization(null);
    }

    @Test
    void testUpdateFile_DeleteFails() throws IOException {
        // Setup UserContext before creating File
        Map<String, Object> mockOrgContext = new HashMap<>();
        mockOrgContext.put("id", "org123");
        UserContext.setLoggedInUserOrganization(mockOrgContext);

        File file = new File();
        file.setId("file123");
        file.setEntityId("entity123");
        file.setOrganizationId("org123");
        file.setFileFormat("txt");

        MultipartFile newFile = new MockMultipartFile("updated.txt", "updated.txt", "text/plain", "new content".getBytes());

        assertThrows(IOException.class, () -> defaultFileStorageService.updateFile(file, newFile)); // Changed to IOException

        // Clear UserContext after the test
        UserContext.setLoggedInUserOrganization(null);
    }
}