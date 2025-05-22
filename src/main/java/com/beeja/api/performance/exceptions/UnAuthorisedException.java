package com.beeja.api.performance.exceptions;

public class UnAuthorisedException extends RuntimeException {
  public UnAuthorisedException(String message) {
    super(message);
  }
}
