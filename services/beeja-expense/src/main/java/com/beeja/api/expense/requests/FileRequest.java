package com.beeja.api.expense.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileRequest {
  private MultipartFile file;
  private String entityType = "expense";
}
