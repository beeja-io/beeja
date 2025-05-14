package com.beeja.api.expense.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseValues {
    private Set<String> expenseCategories;
    private Set<String> expenseTypes;
    private Set<String> expenseModesOfPayment;
}
