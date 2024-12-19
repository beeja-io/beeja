package com.beeja.api.expense.exceptions;

public class CustomAccessDeniedException extends RuntimeException {

  public CustomAccessDeniedException(String message) {
    super(message);
  }
}
