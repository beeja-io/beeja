package com.beeja.api.filemanagement.serviceImpl;

import com.beeja.api.filemanagement.service.FileStorage;
import com.beeja.api.filemanagement.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class CloudFileStorage implements FileStorage {


    @Override
    public void uploadFile(MultipartFile file, File createdFile) throws IOException {

    }

    @Override
    public byte[] downloadFile(File createdFile) throws IOException {
        return new byte[0];
    }

    @Override
    public void deleteFile(File createdFile) throws IOException {

    }
}
