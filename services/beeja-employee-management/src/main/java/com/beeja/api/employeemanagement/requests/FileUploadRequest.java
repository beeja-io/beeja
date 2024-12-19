package com.beeja.api.employeemanagement.requests;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadRequest {
  private MultipartFile file;
  private String name;
  private String fileType;
  private String entityId;

  @Pattern(regexp = "^(employee|project|organization|client)$", message = "Invalid entity type")
  private String entityType;

  private String description;
}
