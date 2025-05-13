package com.beeja.api.employeemanagement.controller;

import com.beeja.api.employeemanagement.annotations.HasPermission;
import com.beeja.api.employeemanagement.constants.PermissionConstants;
import com.beeja.api.employeemanagement.model.Employee;
import com.beeja.api.employeemanagement.model.File;
import com.beeja.api.employeemanagement.requests.FileUploadRequest;
import com.beeja.api.employeemanagement.response.FileResponse;
import com.beeja.api.employeemanagement.service.EmployeeService;
import com.beeja.api.employeemanagement.service.FileService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/files")
public class FileController {

  @Autowired FileService fileService;

  @Autowired EmployeeService employeeService;

  @GetMapping("/{entityId}")
  @HasPermission({
    PermissionConstants.READ_EMPLOYEE_DOCUMENT,
    PermissionConstants.READ_ALL_EMPLOYEE_DOCUMENT
  })
  public ResponseEntity<FileResponse> getAllFilesOfEntityId(
      @PathVariable String entityId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size)
      throws Exception {
    return ResponseEntity.ok(fileService.listOfFileByEntityId(entityId, page, size));
  }

  @PostMapping
  @HasPermission({
    PermissionConstants.CREATE_EMPLOYEE_DOCUMENT,
    PermissionConstants.CREATE_ALL_EMPLOYEE_DOCUMENT
  })
  public ResponseEntity<File> uploadFile(FileUploadRequest fileUploadRequest) throws Exception {
    return ResponseEntity.ok(fileService.uploadFile(fileUploadRequest));
  }

  @GetMapping("/download/{fileId}")
  @HasPermission({
    PermissionConstants.READ_EMPLOYEE_DOCUMENT,
    PermissionConstants.READ_ALL_EMPLOYEE_DOCUMENT,
    PermissionConstants.UPDATE_PROFILE_PIC_SELF
  })
  public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileId)
      throws Exception {
    ByteArrayResource resource = fileService.downloadFile(fileId);
    HttpHeaders headers = new HttpHeaders();
    headers.add(
        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .headers(headers)
        .body(resource);
  }

  @DeleteMapping("/{fileId}")
  @HasPermission({
    PermissionConstants.DELETE_EMPLOYEE_DOCUMENT,
    PermissionConstants.DELETE_ALL_EMPLOYEE_DOCUMENT
  })
  public ResponseEntity<File> deleteFileOfEmployee(@PathVariable String fileId) throws Exception {
    return ResponseEntity.ok(fileService.deleteFile(fileId));
  }

  @PutMapping("/{fileId}")
  @HasPermission({
    PermissionConstants.UPDATE_EMPLOYEE_DOCUMENT,
    PermissionConstants.UPDATE_ALL_EMPLOYEE_DOCUMENT
  })
  public ResponseEntity<File> updateFileByFileId(
      @PathVariable String fileId, FileUploadRequest fileUploadRequest) throws Exception {
    return ResponseEntity.ok(fileService.updateFile(fileId, fileUploadRequest));
  }

  @PostMapping("/profile-pic")
  @HasPermission({
    PermissionConstants.UPDATE_PROFILE_PIC_SELF,
    PermissionConstants.UPDATE_PROFILE_PIC_ALL
  })
  public ResponseEntity<Employee> uploadOrUpdateProfilePic(
      @RequestParam("file") MultipartFile file, @RequestParam(value = "entityId") String entityId)
      throws Exception {
    Employee response = employeeService.uploadOrUpdateProfilePic(file, entityId);
    return ResponseEntity.ok(response);
  }
}