package com.beeja.api.expense.exceptions;

public class UnAuthorisedException extends RuntimeException {
  public UnAuthorisedException(String message) {
    super(message);
  }
}
