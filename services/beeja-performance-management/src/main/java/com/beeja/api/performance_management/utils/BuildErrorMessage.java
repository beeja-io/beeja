package com.beeja.api.performance_management.utils;


import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.response.ErrorResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class BuildErrorMessage {


    /**
     * Builds a consistent ErrorResponse object for exceptions.
     *
     * @param type    ErrorType enum (e.g., VALIDATION_ERROR, RESOURCE_NOT_FOUND_ERROR)
     * @param code    ErrorCode enum (e.g., NUll_VALUE, RESOURCE_NOT_FOUND)
     * @param message Custom descriptive error message
     * @return ErrorResponse object
     */
    public static ErrorResponse buildErrorMessage(ErrorType type, ErrorCode code, String message) {
        return new ErrorResponse(
                type,
                code,
                message,
                Constants.DOC_URL_RESOURCE_NOT_FOUND,
                "",
                generateReferenceId(),
                getCurrentTimestamp()
        );
    }

    private static String generateReferenceId() {
        return "BEEJA-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase();
    }

    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
