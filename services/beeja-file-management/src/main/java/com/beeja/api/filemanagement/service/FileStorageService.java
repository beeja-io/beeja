package com.beeja.api.filemanagement.service;

import com.beeja.api.filemanagement.model.File;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
  void uploadFile(MultipartFile file, File savedFile) throws IOException;

  byte[] downloadFile(File file) throws IOException;

  void deleteFile(File file) throws IOException;

  void updateFile(File file, MultipartFile newFile) throws IOException;
}
