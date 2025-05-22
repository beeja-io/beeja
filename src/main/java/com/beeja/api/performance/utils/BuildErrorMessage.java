package com.beeja.api.performance.utils;

import com.beeja.api.performance.enums.ErrorCode;
import com.beeja.api.performance.enums.ErrorType;

public class BuildErrorMessage {
    public static String buildErrorMessage(ErrorType errorType, ErrorCode errorCode, String message) {
        return String.format("%s,%s,%s", errorType, errorCode, message);
    }
}
