
package com.beeja.api.performance_management.exceptions;

import java.util.HashMap;
import java.util.Map;

public class InvalidOperationException extends RuntimeException {

    private final String errorType;
    private final String errorCode;
    private final String errorMessage;

    public InvalidOperationException(String errorType, String errorCode, String message) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public InvalidOperationException(String errorType) {
        super(errorType);
        this.errorType = errorType;
        this.errorCode = null;
        this.errorMessage = null;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, Object> getErrorDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("type", errorType);
        details.put("code", errorCode);
        details.put("message", errorMessage);
        return details;
    }
}
