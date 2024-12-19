package com.beeja.api.expense.exceptions;

public class ExpenseAlreadySettledException extends RuntimeException {
  public ExpenseAlreadySettledException(String message) {

    super(message);
  }
}
