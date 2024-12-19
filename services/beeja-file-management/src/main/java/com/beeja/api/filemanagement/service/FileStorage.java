package com.beeja.api.filemanagement.service;


import com.beeja.api.filemanagement.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorage {
    void uploadFile(MultipartFile file, File createdFile) throws IOException;

    byte[] downloadFile(File createdFile) throws IOException;

    void deleteFile(File createdFile) throws IOException;

}
