
package com.beeja.api.performance_management.exceptions;

import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.response.ErrorResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException e, WebRequest request) {
        String[] errorMessage = convertStringToArray(e.getMessage());
        ErrorResponse errorResponse =
                new ErrorResponse(
                        ErrorType.valueOf(errorMessage[0]),
                        ErrorCode.valueOf(errorMessage[1]),
                        errorMessage[2],
                        request.getDescription(false),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        String[] errorMessage = convertStringToArray(ex.getMessage());
        ErrorResponse errorResponse =
                new ErrorResponse(
                        ErrorType.valueOf(errorMessage[0]),
                        ErrorCode.valueOf(errorMessage[1]),
                        errorMessage[2],
                        request.getDescription(false),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleCustomAccessDeniedException(
            CustomAccessDeniedException e, WebRequest request) {
        String[] errorMessage = convertStringToArray(e.getMessage());
        ErrorResponse errorResponse =
                new ErrorResponse(
                        ErrorType.valueOf(errorMessage[0]),
                        ErrorCode.valueOf(errorMessage[1]),
                        errorMessage[2],
                        request.getDescription(false),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductIdException(
            DuplicateDataException e, WebRequest request) {
        String[] errorMessage = convertStringToArray(e.getMessage());
        ErrorResponse errorResponse =
                new ErrorResponse(
                        ErrorType.valueOf(errorMessage[0]),
                        ErrorCode.valueOf(errorMessage[1]),
                        errorMessage[2],
                        request.getDescription(false),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationException(
            InvalidOperationException e, WebRequest request) {

        String[] errorMessage = convertStringToArray(e.getMessage());
        ErrorType type = ErrorType.valueOf(errorMessage[0]);
        ErrorCode code = ErrorCode.valueOf(errorMessage[1]);
        String message = errorMessage.length > 2 ? errorMessage[2] : "Unexpected error";

        ErrorResponse errorResponse = new ErrorResponse(
                type,
                code,
                message,
                request.getDescription(false),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        if (type == ErrorType.SUCCESS) {
            return ResponseEntity.ok(errorResponse);
        }

        HttpStatus status = HttpStatus.BAD_REQUEST;

        switch (type) {
            case RESOURCE_NOT_FOUND_ERROR -> status = HttpStatus.NOT_FOUND;
            case VALIDATION_ERROR -> status = HttpStatus.BAD_REQUEST;
            case INTERNAL_SERVER_ERROR, DATA_ACCESS_ERROR -> status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(FeignClientException.class)
    public ResponseEntity<ErrorResponse> handleFeignClientException(
            FeignClientException e, WebRequest request) {
        String[] errorMessage = convertStringToArray(e.getMessage());
        ErrorResponse errorResponse =
                new ErrorResponse(
                        ErrorType.valueOf(errorMessage[0]),
                        ErrorCode.valueOf(errorMessage[1]),
                        errorMessage[2],
                        request.getDescription(false),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        log.error("Feign client call failed: {}", errorMessage[2]);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error Occurred, please contact IT Support");
    }

    private String[] convertStringToArray(String commaSeparatedString) {
        return commaSeparatedString.split(",");
    }
}
