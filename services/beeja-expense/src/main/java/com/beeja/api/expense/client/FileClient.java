package com.beeja.api.expense.client;

import com.beeja.api.expense.requests.FileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "file-service", url = "${client-urls.fileService}")
public interface FileClient {
  @PostMapping(value = "/v1/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<?> uploadFile(FileRequest fileRequest);

  @DeleteMapping("/v1/files/{fileId}")
  ResponseEntity<?> deleteFile(@PathVariable String fileId) throws Exception;

  @GetMapping("/v1/files/download/{fileId}")
  ResponseEntity<byte[]> downloadFile(@PathVariable String fileId);

  @GetMapping("v1/files/find/{fileId}")
  ResponseEntity<?> getFileById(@PathVariable String fileId);
}
