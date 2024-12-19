package com.beeja.api.expense.exceptions;

public class FeignClientException extends RuntimeException {
  public FeignClientException(String message) {
    super(message);
  }
}
