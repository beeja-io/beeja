package com.beeja.api.employeemanagement.exceptions;

public class ResourceNotFound extends RuntimeException {
  public ResourceNotFound(String message) {
    super(message);
  }
}
