package com.beeja.api.financemanagementservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class PDFResponse {
  String entityId;
  MultipartFile pdfFile;
}
