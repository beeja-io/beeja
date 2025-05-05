package com.beeja.api.projectmanagement.utils;

import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.responses.ErrorResponse;

public class BuildErrorMessage {
    public static ErrorResponse buildErrorMessage(
            ErrorType type,
            ErrorCode code,
            String message
           ){
        return new ErrorResponse(
                type,
                code,
                message,
                null
        );

    }
}
