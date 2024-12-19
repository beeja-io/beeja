package com.beeja.api.filemanagement.exceptions;

public class GCSFileAccessException extends RuntimeException {
  public GCSFileAccessException(String message) {
    super(message);
  }
}
