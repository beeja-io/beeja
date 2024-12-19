package com.beeja.api.expense.service;

import org.springframework.core.io.ByteArrayResource;

public interface ReceiptService {
  ByteArrayResource downloadFile(String fileId) throws Exception;
}
