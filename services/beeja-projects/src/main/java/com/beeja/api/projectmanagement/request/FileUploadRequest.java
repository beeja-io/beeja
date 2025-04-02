package com.beeja.api.projectmanagement.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadRequest {
  private MultipartFile file;
  private String fileType= "project";
  private String entityId;
  private String entityType= "client";
}
