package com.beeja.api.projectmanagement.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadResultMetaData {
  private String fileName;
  private String createdBy;
  private String entityId;
  private String organizationId;
}
