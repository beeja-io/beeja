package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.annotations.HasPermission;
import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.model.dto.File;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.responses.FileResponse;
import com.beeja.api.projectmanagement.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/files")
public class FileController {

  @Autowired FileService fileService;

  @GetMapping("download/{fileId}")
  @HasPermission({PermissionConstants.READ_DOCUMENT, PermissionConstants.GET_CONTRACT})
  public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileId)
      throws Exception {
    ByteArrayResource byteArrayResource = fileService.downloadFile(fileId);

    HttpHeaders headers = new HttpHeaders();
    headers.add(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + byteArrayResource.getFilename() + "\"");
    headers.add("Access-Control-Expose-Headers", "Content-Disposition");

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .headers(headers)
        .body(byteArrayResource);
  }

  @GetMapping("/{entityId}")
  @HasPermission({PermissionConstants.READ_DOCUMENT, PermissionConstants.READ_ALL_DOCUMENTS})
  public ResponseEntity<FileResponse> getAllFilesOfEntityId(
      @PathVariable String entityId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size)
      throws Exception {
    return ResponseEntity.ok(fileService.listOfFileByEntityId(entityId, page, size));
  }

  @PostMapping
  @HasPermission({PermissionConstants.CREATE_DOCUMENT, PermissionConstants.CREATE_ALL_DOCUMENT})
  public ResponseEntity<File> uploadFile(FileUploadRequest fileUploadRequest) throws Exception {
    return ResponseEntity.ok(fileService.uploadFile(fileUploadRequest));
  }

  @DeleteMapping("/{fileId}")
  @HasPermission({PermissionConstants.DELETE_DOCUMENT, PermissionConstants.DELETE_ALL_DOCUMENT})
  public ResponseEntity<File> deleteFile(@PathVariable String fileId) throws Exception {
    return ResponseEntity.ok(fileService.deleteFile(fileId));
  }

  @PutMapping("/{fileId}")
  @HasPermission({PermissionConstants.UPDATE_DOCUMENT, PermissionConstants.UPDATE_ALL_DOCUMENT})
  public ResponseEntity<File> updateFileByFileId(
      @PathVariable String fileId, FileUploadRequest fileUploadRequest) throws Exception {
    return ResponseEntity.ok(fileService.updateFile(fileId, fileUploadRequest));
  }
}
