package com.beeja.api.accounts.response;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseValuesDTO {
  private Set<String> expenseCategories;
  private Set<String> expenseTypes;
  private Set<String> expenseModesOfPayment;
}
