package com.beeja.api.accounts.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseValuesDTO {
    private Set<String> expenseCategories;
    private Set<String> expenseTypes;
    private Set<String> expenseModesOfPayment;
}
