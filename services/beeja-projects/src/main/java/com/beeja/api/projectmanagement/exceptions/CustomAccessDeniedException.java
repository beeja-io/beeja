package com.beeja.api.projectmanagement.exceptions;

public class CustomAccessDeniedException extends RuntimeException {

  public CustomAccessDeniedException(String message) {
    super(message);
  }
}
