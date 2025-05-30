package com.beeja.api.performance_management.exceptions;

public class CustomAccessDeniedException extends RuntimeException {

  public CustomAccessDeniedException(String message) {
    super(message);
  }
}
