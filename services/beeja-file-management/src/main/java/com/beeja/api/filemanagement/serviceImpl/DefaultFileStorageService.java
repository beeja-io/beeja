package com.beeja.api.filemanagement.serviceImpl;

import com.beeja.api.filemanagement.config.properties.DefaultStorageProperties;
import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.service.FileStorageService;
import com.beeja.api.filemanagement.utils.BuildErrorMessage;
import com.beeja.api.filemanagement.utils.Constants;
import com.beeja.api.filemanagement.enums.ErrorType;
import com.beeja.api.filemanagement.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.beeja.api.filemanagement.utils.helpers.FileExtensionHelpers.FilePathGenerator.generateFilePath;

import java.io.FileNotFoundException;

@Slf4j
@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "local")
public class DefaultFileStorageService implements FileStorageService {

    @Autowired
    private DefaultStorageProperties storageDirectory;

    public DefaultFileStorageService(){
        log.info("DefaultFileStorageService bean created");
    }

    @Override
    public void uploadFile(MultipartFile file, File savedFile) throws IOException {
        if (file == null || file.isEmpty()) {
            log.error(Constants.EMPTY_FILE_NOT_ALLOWED + file.getOriginalFilename());
            throw new IOException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.INVALID_REQUEST,
                            ErrorCode.EMPTY_FILE,
                            Constants.EMPTY_FILE_NOT_ALLOWED));
        }

        Path storagePath = Paths.get(storageDirectory.getPath());
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generate final file path with extension
        String filePath = generateFilePath(savedFile) + fileExtension;
        Path destinationPath = Paths.get(storagePath.toString(), filePath);

        Path parentDir = destinationPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        try {
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error(Constants.ERROR_SAVING_FILE, e.getMessage());
            throw new IOException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.SERVICE_ERROR,
                            ErrorCode.SERVICE_DOWN,
                            Constants.ERROR_SAVING_FILE));
        }
    }



    @Override
    public byte[] downloadFile(File file) throws IOException {
        if (file == null) {
            throw new FileNotFoundException(Constants.NO_FILE_FOUND_WITH_GIVEN_ID);
        }

        Path storagePath = Paths.get(storageDirectory.getPath());
        String filePath = generateFilePath(file);
        String fullFilePath = filePath + "." + file.getFileFormat();

        Path path = storagePath.resolve(fullFilePath);

        if (!Files.exists(path)) {
            log.error(Constants.FILE_NOT_FOUND_AT_PATH + path);
            throw new FileNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.RESOURCE_NOT_FOUND_ERROR,
                            ErrorCode.FILE_NOT_FOUND,
                    Constants.FILE_NOT_FOUND_AT_PATH + path));
        }

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error(Constants.ERROR_READING_FILE + path, e.getMessage());
            throw new IOException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.SERVICE_ERROR,
                            ErrorCode.SERVICE_DOWN,
                            Constants.ERROR_READING_FILE + path));
        }
    }


    @Override
    public void deleteFile(File file) throws IOException {
        if (file == null) {
            log.error(Constants.NO_FILE_FOUND_WITH_GIVEN_ID + file.getId());
            throw new FileNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.RESOURCE_NOT_FOUND_ERROR,
                            ErrorCode.FILE_NOT_FOUND,
                            Constants.NO_FILE_FOUND_WITH_GIVEN_ID+ file.getId()));
        }

        Path storagePath = Paths.get(storageDirectory.getPath());
        String filePath = generateFilePath(file);
        String fullFilePath = filePath + "." + file.getFileFormat();

        Path path = storagePath.resolve(fullFilePath);

        try {
            boolean deleted = Files.deleteIfExists(path);
            if (!deleted) {
                log.error(Constants.FILE_NOT_FOUND_AT_PATH + path);
                throw new FileNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.FILE_NOT_FOUND,
                                Constants.FILE_NOT_FOUND_AT_PATH + path));
            }
        } catch (IOException e) {
            log.error(Constants.ERROR_DELETING_FILE + path, e.getMessage());
            throw new IOException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.SERVICE_ERROR,
                            ErrorCode.RESOURCE_DELETING_ERROR,
                    Constants.ERROR_DELETING_FILE + path));
        }
    }

    @Override
    public void updateFile(File file, MultipartFile newFile) throws IOException {
        deleteFile(file);
        uploadFile(newFile, file);
    }
}
