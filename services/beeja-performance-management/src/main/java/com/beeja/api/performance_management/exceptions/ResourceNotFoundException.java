package com.beeja.api.performance_management.exceptions;

import com.beeja.api.performance_management.response.ErrorResponse;

public class ResourceNotFoundException extends RuntimeException {
  private ErrorResponse errorResponse;
    public ResourceNotFoundException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
