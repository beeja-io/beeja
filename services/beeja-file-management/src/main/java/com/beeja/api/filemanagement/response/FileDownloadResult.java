package com.beeja.api.filemanagement.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;

@AllArgsConstructor
@Getter
public class FileDownloadResult {
  private  ByteArrayResource resource;
  private  String createdBy;
  private  String entityId;
  private  String organizationId;
  private  String fileName;


  public FileDownloadResult(ByteArrayResource resource, String user1, String entity1, String org1) {
  }



}
