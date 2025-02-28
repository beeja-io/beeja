package com.beeja.api.filemanagement.controller;

import com.beeja.api.filemanagement.exceptions.FileNotFoundException;
import com.beeja.api.filemanagement.exceptions.FileTypeMismatchException;
import com.beeja.api.filemanagement.exceptions.FileAccessException;
import com.beeja.api.filemanagement.exceptions.MongoFileUploadException;
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
  public ResponseEntity<?> uploadFile(FileUploadRequest fileInput) {
    if (fileInput.getFile() == null || fileInput.getFile().getOriginalFilename() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Constants.FILE_MISSING_IN_REQUEST_ERROR);
    }

    try {
      File file = fileService.uploadFile(fileInput);
      return ResponseEntity.ok(file);
    } catch (MongoFileUploadException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Constants.MONGO_UPLOAD_FAILED);
    } catch (FileAccessException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Constants.ERROR_SAVING_FILE);
    } catch (FileTypeMismatchException e) {
      throw new FileTypeMismatchException(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Constants.SERVICE_DOWN_ERROR + e.getMessage());
    }
  }

  @GetMapping("/download/{fileId}")
  public ResponseEntity<?> downloadFile(@PathVariable String fileId) {
    try {
      FileDownloadResult result = fileService.downloadFile(fileId);
      ByteArrayResource resource = result.getResource();
      HttpHeaders headers = new HttpHeaders();
      headers.add(
          HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\"" + resource.getFilename() + "\"");
      // Adding custom headers for metadata
      headers.add("createdBy", result.getCreatedBy());
      headers.add("organizationId", result.getOrganizationId());
      headers.add("entityId", result.getEntityId());

      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .headers(headers)
          .body(resource);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  // Delete file
  @DeleteMapping("/{fileId}")
  public ResponseEntity<?> deleteFile(@PathVariable String fileId) {
    try {
      File deletedFile = fileService.deleteFile(fileId);
      return ResponseEntity.ok(deletedFile);
    } catch (FileNotFoundException | FileAccessException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  @PutMapping("/{fileId}")
  public ResponseEntity<?> updateFile(
      @PathVariable String fileId, FileUploadRequest fileUploadRequest) {
    try {
      File updatedFile = fileService.updateFile(fileId, fileUploadRequest);
      return ResponseEntity.ok(updatedFile);
    } catch (FileAccessException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  @GetMapping("/find/{fileId}")
  public ResponseEntity<?> getFileById(@PathVariable String fileId) throws java.io.FileNotFoundException {
    return ResponseEntity.ok(fileService.getFileById(fileId));
  }

  /**
   * Handles the upload or update of a file based on existing data. This will fetch data based on
   * File Type, Entity Id, and Organisation Id
   */
  @PostMapping("/dynamic")
  public ResponseEntity<?> uploadOrUpdateFile(FileUploadRequest fileInput) {
    if (fileInput.getFile() == null || fileInput.getFile().getOriginalFilename() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Constants.FILE_MISSING_IN_REQUEST_ERROR);
    }
    try {
      File file = fileService.uploadOrUpdateFile(fileInput);
      return ResponseEntity.ok(file);
    } catch (MongoFileUploadException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Constants.MONGO_UPLOAD_FAILED);
    } catch (FileAccessException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Constants.ERROR_SAVING_FILE);
    } catch (FileTypeMismatchException e) {
      throw new FileTypeMismatchException(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Constants.SERVICE_DOWN_ERROR + e.getMessage());
    }
  }
}
