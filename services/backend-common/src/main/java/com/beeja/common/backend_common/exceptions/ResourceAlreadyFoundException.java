package com.beeja.common.backend_common.exceptions;

public class ResourceAlreadyFoundException extends RuntimeException {
    public ResourceAlreadyFoundException(String message) {
        super(message);
    }
}
