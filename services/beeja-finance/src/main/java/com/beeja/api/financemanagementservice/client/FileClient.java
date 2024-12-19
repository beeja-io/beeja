package com.beeja.api.financemanagementservice.client;

import com.beeja.api.financemanagementservice.requests.FileUploadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "file-service", url = "${client-urls.fileService}")
public interface FileClient {
  @PostMapping(value = "/v1/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<?> uploadFile(
      FileUploadRequest fileUploadRequest,
      @RequestHeader("Authorization") String authorizationHeader);
}
