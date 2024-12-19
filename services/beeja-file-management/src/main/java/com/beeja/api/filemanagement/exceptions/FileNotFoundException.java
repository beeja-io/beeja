package com.beeja.api.filemanagement.exceptions;

public class FileNotFoundException extends RuntimeException {
  public FileNotFoundException(String message) {
    super(message);
  }
}
