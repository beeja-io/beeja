package com.beeja.api.projectmanagement.request;

import com.beeja.api.projectmanagement.utils.Constants;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
  private MultipartFile file;
  private String name;
  private String description;
  private String fileType= Constants.FILE_TYPE_PROJECT;
  private String entityId= Constants.ENTITY_TYPE_CLIENT;
  private String entityType= Constants.ENTITY_TYPE_CLIENT;
}
