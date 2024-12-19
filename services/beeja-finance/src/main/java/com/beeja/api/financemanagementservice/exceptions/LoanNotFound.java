package com.beeja.api.financemanagementservice.exceptions;

public class LoanNotFound extends RuntimeException {
  public LoanNotFound(String message) {
    super(message);
  }
}
