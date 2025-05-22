package com.beeja.api.performance.exceptions;

public class CustomAccessDeniedException extends RuntimeException {

  public CustomAccessDeniedException(String message) {
    super(message);
  }
}
