package com.beeja.api.expense.exceptions;

public class ExpenseNotFound extends RuntimeException {
  public ExpenseNotFound(String message) {
    super(message);
  }
}
