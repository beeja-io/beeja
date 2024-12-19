package com.beeja.api.employeemanagement.service;

import com.beeja.api.employeemanagement.model.File;
import com.beeja.api.employeemanagement.requests.FileUploadRequest;
import com.beeja.api.employeemanagement.response.FileResponse;
import org.springframework.core.io.ByteArrayResource;

public interface FileService {
  FileResponse listOfFileByEntityId(String entityId, int page, int size) throws Exception;

  ByteArrayResource downloadFile(String fileId) throws Exception;

  File deleteFile(String fileName) throws Exception;

  File uploadFile(FileUploadRequest fileUpload) throws Exception;

  File updateFile(String fileId, FileUploadRequest fileUploadRequest) throws Exception;

  File uploadOrUpdateFile(FileUploadRequest fileUploadRequest) throws Exception;
}
