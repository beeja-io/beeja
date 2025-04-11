package com.beeja.api.expense.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDefaultValues {
  private String type;
  private String category;
  private String modeOfPayment;
}
