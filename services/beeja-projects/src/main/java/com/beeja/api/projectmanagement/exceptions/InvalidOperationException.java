package com.beeja.api.projectmanagement.exceptions;

import com.beeja.api.projectmanagement.responses.ErrorResponse;

/**
 * Invalid operation (business rule) exception. Use for duplicates / unauthorized actions.
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(ErrorResponse message) {
        super(String.valueOf(message));
    }
}