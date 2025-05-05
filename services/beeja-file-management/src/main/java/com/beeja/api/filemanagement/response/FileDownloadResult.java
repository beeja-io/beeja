package com.beeja.api.filemanagement.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;

@AllArgsConstructor
@Getter
public class FileDownloadResult {
  private final ByteArrayResource resource;
  private final String createdBy;
  private final String entityId;
  private final String organizationId;
  private final String fileName;
}
