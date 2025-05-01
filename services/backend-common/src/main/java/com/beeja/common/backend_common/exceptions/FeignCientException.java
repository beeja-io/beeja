package com.beeja.common.backend_common.exceptions;

public class FeignCientException extends RuntimeException {
    public FeignCientException(String message) {
        super(message);
    }
}
