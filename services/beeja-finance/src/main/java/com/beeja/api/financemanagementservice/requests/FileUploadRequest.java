package com.beeja.api.financemanagementservice.requests;

import com.beeja.api.financemanagementservice.Utils.Constants;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadRequest {
  private MultipartFile file;
  private String name;
  private String fileType;
  private String entityId;

  @Pattern(
      regexp = "^(employee|project|organization|client)$",
      message = Constants.INVALID_ENTITY_TYPE)
  private String entityType;

  private String description;
}
