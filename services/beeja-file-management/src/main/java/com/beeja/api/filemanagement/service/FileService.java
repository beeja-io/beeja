package com.beeja.api.filemanagement.service;

import com.beeja.api.filemanagement.response.FileDownloadResult;
import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.requests.FileUploadRequest;
import com.beeja.api.filemanagement.response.FileResponse;

public interface FileService {
  FileResponse listofFileByEntityId(String entityId, int page, int size) throws Exception;

  FileDownloadResult downloadFile(String fileId) throws Exception;

  File deleteFile(String fileName) throws Exception;

  File uploadFile(FileUploadRequest fileUpload) throws Exception;

  File updateFile(String fileId, FileUploadRequest fileUploadRequest) throws Exception;

  File getFileById(String fileId);

  File uploadOrUpdateFile(FileUploadRequest fileUploadRequest) throws Exception;
}
