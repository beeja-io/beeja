package com.beeja.api.projectmanagement.request;

import com.beeja.api.projectmanagement.utils.Constants;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadRequest {
  private MultipartFile file;
  private String fileType= Constants.FILE_TYPE_PROJECT;
  private String entityId;
  private String entityType= Constants.ENTITY_TYPE_CLIENT;
}
