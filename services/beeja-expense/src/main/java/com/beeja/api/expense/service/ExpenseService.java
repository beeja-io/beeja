package com.beeja.api.expense.service;

import com.beeja.api.expense.modal.Expense;
import com.beeja.api.expense.requests.CreateExpense;
import com.beeja.api.expense.requests.ExpenseUpdateRequest;
import com.beeja.api.expense.response.ExpenseValues;
import java.util.Date;
import java.util.List;

public interface ExpenseService {

  Expense deleteExpense(String expenseId) throws Exception;

  Expense updateExpense(String expenseId, ExpenseUpdateRequest updatedExpense) throws Exception;

  Expense createExpense(CreateExpense createExpense) throws Exception;

  Expense getExpenseById(String expenseId) throws Exception;

  Expense settleExpense(String expenseId) throws Exception;

  List<Expense> getFilteredExpenses(
      Date startDate,
      Date endDate,
      List<String> department,
      String filterBasedOn,
      List<String> modeOfPayment,
      List<String> expenseType,
      List<String> expenseCategory,
      String organizationId,
      int pageNumber,
      int pageSize,
      String sortBy,
      Boolean settlementStatus,
      boolean ascending)
      throws Exception;

  Double getFilteredTotalAmount(
      Date startDate,
      Date endDate,
      List<String> department,
      String filterBasedOn,
      List<String> modeOfPayment,
      List<String> expenseType,
      List<String> expenseCategory,
      Boolean settlementStatus,
      String organizationId);

  Long getTotalExpensesSize(
      Date startDate,
      Date endDate,
      List<String> department,
      String filterBasedOn,
      List<String> modeOfPayment,
      List<String> expenseType,
      List<String> expenseCategory,
      Boolean settlementStatus,
      String organizationId);

  ExpenseValues getExpenseDefaultValues(String organizationId);
}
