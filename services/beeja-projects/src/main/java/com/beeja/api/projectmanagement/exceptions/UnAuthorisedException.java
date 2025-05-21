package com.beeja.api.projectmanagement.exceptions;

public class UnAuthorisedException extends RuntimeException {

  public UnAuthorisedException(String message) {
    super(message);
  }
}
