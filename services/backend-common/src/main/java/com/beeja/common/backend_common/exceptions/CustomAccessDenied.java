package com.beeja.common.backend_common.exceptions;

public class CustomAccessDenied extends RuntimeException {
    public CustomAccessDenied(String message) {
        super(message);
    }
}
