package com.beeja.api.accounts.exceptions;

public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String message) {
        super(message);
    }
}
