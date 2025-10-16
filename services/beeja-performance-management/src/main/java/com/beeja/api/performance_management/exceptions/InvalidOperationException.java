package com.beeja.api.performance_management.exceptions;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String errorType, String errorCode, String message) {
        super(String.format("%s,%s,%s",
                errorType != null ? errorType : "",
                errorCode != null ? errorCode : "",
                message != null ? message : ""));
    }

    public InvalidOperationException(String errorType) {
        super(errorType != null ? errorType : "UnknownError");
    }
}
