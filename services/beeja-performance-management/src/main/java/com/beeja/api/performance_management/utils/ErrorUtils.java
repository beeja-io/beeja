package com.beeja.api.performance_management.utils;

import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;

public class ErrorUtils {
    public static String formatError(ErrorType type, ErrorCode code, String message) {
        return type.name() + "," + code.name() + "," + message;
        }
}
