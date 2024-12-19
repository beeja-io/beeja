package com.beeja.api.expense.controllers;

import com.beeja.api.expense.annotations.HasPermission;
import com.beeja.api.expense.service.ReceiptService;
import com.beeja.api.expense.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/receipts")
public class ReceiptController {

  @Autowired ReceiptService receiptService;

  @GetMapping("/{fileId}")
  @HasPermission(Constants.READ_EXPENSE)
  public ResponseEntity<?> downloadFile(@PathVariable String fileId) throws Exception {
    ByteArrayResource resource = receiptService.downloadFile(fileId);
    HttpHeaders headers = new HttpHeaders();
    headers.add(
        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .headers(headers)
        .body(resource);
  }
}
