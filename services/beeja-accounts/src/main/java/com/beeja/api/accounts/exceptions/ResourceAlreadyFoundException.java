package com.beeja.api.accounts.exceptions;

public class ResourceAlreadyFoundException extends RuntimeException {
  public ResourceAlreadyFoundException(String message) {
    super(message);
  }
}
