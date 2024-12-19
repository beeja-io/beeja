package com.beeja.api.employeemanagement.exceptions;

public class UnAuthorisedException extends RuntimeException {

  public UnAuthorisedException(String message) {
    super(message);
  }
}
