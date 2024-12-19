package com.beeja.api.financemanagementservice.exceptions;

public class UnAuthorisedException extends RuntimeException {
  public UnAuthorisedException(String message) {
    super(message);
  }
}
