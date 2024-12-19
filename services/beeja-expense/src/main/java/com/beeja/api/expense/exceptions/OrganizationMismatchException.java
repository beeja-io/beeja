package com.beeja.api.expense.exceptions;

public class OrganizationMismatchException extends RuntimeException {
  public OrganizationMismatchException(String message) {
    super(message);
  }
}
