package com.beeja.api.filemanagement.serviceImpl;


import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.service.FileStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DockerFileStorage implements FileStorage {

    @Value( "${beeja.fileUploadLocation}" )
    private String fileUploadLocation;
    private  Path location  = null;

    private static final String pathSeparator = java.io.File.separator;
    @PostConstruct
    void setFileUploadLocation(){
        location = Paths.get(fileUploadLocation);
    }


    @Override
    public void uploadFile(MultipartFile uploadedFile, File file) throws IOException {

            Files.copy(uploadedFile.getInputStream(), this.location.resolve(file.getId()));

    }

    public byte[] downloadFile(File file) throws IOException {
        Path filepath = this.location.resolve(file.getId());
        return Files.readAllBytes(filepath);
    }

    @Override
    public void deleteFile(File createdFile) throws IOException {
        Files.deleteIfExists(this.location.resolve(createdFile.getId()));
    }
}
