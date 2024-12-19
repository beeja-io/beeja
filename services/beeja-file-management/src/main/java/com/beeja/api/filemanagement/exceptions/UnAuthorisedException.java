package com.beeja.api.filemanagement.exceptions;

public class UnAuthorisedException extends RuntimeException {
  public UnAuthorisedException(String message) {
    super(message);
  }
}
