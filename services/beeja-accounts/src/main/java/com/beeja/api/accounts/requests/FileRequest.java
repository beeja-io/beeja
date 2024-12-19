package com.beeja.api.accounts.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileRequest {
  private MultipartFile file;
  private String name;
  private String fileType;
  private String entityId;
  private String entityType;
  private String description;
}
