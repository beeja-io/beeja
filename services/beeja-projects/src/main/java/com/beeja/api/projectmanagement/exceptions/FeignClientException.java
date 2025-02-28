package com.beeja.api.projectmanagement.exceptions;

public class FeignClientException extends RuntimeException {
  public FeignClientException(String message) {
    super(message);
  }
}
