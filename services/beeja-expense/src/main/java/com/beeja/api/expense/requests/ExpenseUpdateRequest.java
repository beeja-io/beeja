package com.beeja.api.expense.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ExpenseUpdateRequest {
  private String category;
  private String type;
  private float amount;
  private String currencyCode;
  private boolean claimed;
  private List<String> deleteFileId;
  private List<MultipartFile> newFiles;
  private String department;
  private String modeOfPayment;
  private String paymentMadeBy;

  private String merchant;
  private String description;
  private String expenseDate;
  private String requestedDate;
  private String paymentSettled;
}
