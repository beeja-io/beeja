package com.beeja.api.performance_management.exceptions;

import com.beeja.api.performance_management.response.ErrorResponse;

public class FeignClientException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public FeignClientException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
