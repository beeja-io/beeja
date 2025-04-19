package com.beeja.api.expense.enums;

public enum ErrorType {
    RESOURCE_NOT_FOUND,
    API_ERROR,
    AUTHENTICATION_ERROR,
    AUTHORIZATION_ERROR,
    CONFLICT_ERROR,
    VALIDATION_ERROR,
    RESOURCE_EXISTS_ERROR,
    MEMORY_ERROR,
    INVALID_REQUEST,
    GENERAL_ERROR,
    DB_ERROR,
    BAD_REQUEST, FEIGN_CLIENT_ERROR, INTERNAL_SERVER_ERROR
}
