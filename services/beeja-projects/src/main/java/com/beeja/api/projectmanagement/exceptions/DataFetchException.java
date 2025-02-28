package com.beeja.api.projectmanagement.exceptions;

import com.beeja.api.projectmanagement.responses.ErrorResponse;

public class DataFetchException extends RuntimeException{
    private final ErrorResponse errorResponse;

    public DataFetchException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
