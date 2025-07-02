package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.dto.File;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.responses.FileResponse;
import org.springframework.core.io.ByteArrayResource;

public interface FileService {
  ByteArrayResource downloadFile(String fileId) throws Exception;

  FileResponse listOfFileByEntityId(String entityId, int page, int size) throws Exception;

  File uploadFile(FileUploadRequest fileUploadRequest) throws Exception;

  File deleteFile(String fileId) throws Exception;

  File updateFile(String fileId, FileUploadRequest fileUploadRequest) throws Exception;
}
