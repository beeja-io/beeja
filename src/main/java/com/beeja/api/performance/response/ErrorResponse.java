package com.beeja.api.performance.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.beeja.api.performance.enums.ErrorCode;
import com.beeja.api.performance.enums.ErrorType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private ErrorType type;
    private ErrorCode code;
    private String message;
    private String docUrl;
    private String path;
    private String referenceId;
    private String timestamp;
}
