package com.beeja.api.expense.exceptions;

public class handleInternalServerException extends RuntimeException {
  public handleInternalServerException(String message) {
    super(message);
  }
}
