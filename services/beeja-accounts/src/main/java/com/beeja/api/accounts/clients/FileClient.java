package com.beeja.api.accounts.clients;

import com.beeja.api.accounts.requests.FileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "file-service", url = "${client-urls.fileService}")
public interface FileClient {
  @PostMapping(value = "/v1/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<?> uploadFile(FileRequest fileRequest);

  @PutMapping(value = "/v1/files/{fileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<?> updateFile(@PathVariable String fileId, FileRequest fileUploadRequest);

  @GetMapping("/v1/files/download/{fileId}")
  ResponseEntity<byte[]> downloadFile(@PathVariable String fileId);
}
