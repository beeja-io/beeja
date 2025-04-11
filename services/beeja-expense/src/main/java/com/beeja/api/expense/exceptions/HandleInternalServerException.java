package com.beeja.api.expense.exceptions;

public class HandleInternalServerException extends RuntimeException {
  public HandleInternalServerException(String message) {
    super(message);
  }
}
