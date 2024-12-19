package com.beeja.api.employeemanagement.exceptions;

public class FeignClientException extends RuntimeException {
  public FeignClientException(String message) {
    super(message);
  }
}
