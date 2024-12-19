package com.beeja.api.accounts.exceptions;

public class CustomAccessDenied extends RuntimeException {
  public CustomAccessDenied(String message) {
    super(message);
  }
}
