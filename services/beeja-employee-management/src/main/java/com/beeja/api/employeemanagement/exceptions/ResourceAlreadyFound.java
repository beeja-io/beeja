package com.beeja.api.employeemanagement.exceptions;

public class ResourceAlreadyFound extends RuntimeException {
  public ResourceAlreadyFound(String message) {
    super(message);
  }
}
