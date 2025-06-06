package com.beeja.api.filemanagement.controller;

import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.requests.FileUploadRequest;
import com.beeja.api.filemanagement.response.FileDownloadResult;
import com.beeja.api.filemanagement.response.FileResponse;
import com.beeja.api.filemanagement.service.FileService;
import com.beeja.api.filemanagement.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/files")
public class FileController {

  @Autowired FileService fileService;

  @GetMapping("{entityId}")
  public ResponseEntity<FileResponse> getAllFilesByEntityId(
      @PathVariable String entityId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size)
      throws Exception {
    FileResponse response = fileService.listofFileByEntityId(entityId, page, size);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<?> uploadFile(FileUploadRequest fileInput) throws Exception {
    if (fileInput.getFile() == null || fileInput.getFile().getOriginalFilename() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Constants.FILE_MISSING_IN_REQUEST_ERROR);
    }
    File file = fileService.uploadFile(fileInput);
    return ResponseEntity.ok(file);
  }

  @GetMapping("/download/{fileId}")
  public ResponseEntity<?> downloadFile(@PathVariable String fileId) throws Exception {
    FileDownloadResult result = fileService.downloadFile(fileId);
    ByteArrayResource resource = result.getResource();

    HttpHeaders headers = new HttpHeaders();
    headers.add(
        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
    headers.add("createdBy", result.getCreatedBy());
    headers.add("organizationId", result.getOrganizationId());
    headers.add("entityId", result.getEntityId());
    headers.add("fileName", result.getFileName());

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .headers(headers)
        .body(resource);
  }

  // Delete file
  @DeleteMapping("/{fileId}")
  public ResponseEntity<?> deleteFile(@PathVariable String fileId) throws Exception {
    File deletedFile = fileService.deleteFile(fileId);
    return ResponseEntity.ok(deletedFile);
  }

  @PutMapping("/{fileId}")
  public ResponseEntity<?> updateFile(
      @PathVariable String fileId, FileUploadRequest fileUploadRequest) throws Exception {
    File updatedFile = fileService.updateFile(fileId, fileUploadRequest);
    return ResponseEntity.ok(updatedFile);
  }

  @GetMapping("/find/{fileId}")
  public ResponseEntity<?> getFileById(@PathVariable String fileId)
      throws java.io.FileNotFoundException {
    return ResponseEntity.ok(fileService.getFileById(fileId));
  }

  /**
   * Handles the upload or update of a file based on existing data. This will fetch data based on
   * File Type, Entity Id, and Organisation Id
   */
  @PostMapping("/dynamic")
  public ResponseEntity<?> uploadOrUpdateFile(FileUploadRequest fileInput) throws Exception {
    if (fileInput.getFile() == null || fileInput.getFile().getOriginalFilename() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Constants.FILE_MISSING_IN_REQUEST_ERROR);
    }

    File file = fileService.uploadOrUpdateFile(fileInput);
    return ResponseEntity.ok(file);
  }
}
