package com.beeja.api.accounts.exceptions;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
