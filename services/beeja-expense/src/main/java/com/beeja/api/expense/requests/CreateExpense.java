package com.beeja.api.expense.requests;

import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateExpense {
  private String category;
  private String type;
  private float amount;
  private String currencyCode;
  private String modeOfPayment;
  private String merchant;
  private boolean isClaimed;
  private String paymentMadeBy;
  private String department;
  private String description;
  private List<MultipartFile> files;
  private String expenseDate;
  private String requestedDate;
  private String paymentSettled;
}
