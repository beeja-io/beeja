package com.beeja.api.performance.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import com.beeja.api.performance.enums.ErrorCode;
import com.beeja.api.performance.enums.ErrorType;
import com.beeja.api.performance.response.ErrorResponse;
import com.beeja.api.performance.utils.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.beeja.api.performance.utils.Constants.BEEJA;

@ControllerAdvice
public class ExceptionAdvice {
  @ExceptionHandler(CustomAccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<?> handleCustomAccessDeniedException(CustomAccessDeniedException ex,WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
            new ErrorResponse(
                    ErrorType.valueOf(errorMessage[0]),
                    ErrorCode.valueOf(errorMessage[1]),
                    errorMessage[2],
                    Constants.DOC_URL_RESOURCE_NOT_FOUND,
                    request.getDescription(false),
                    BEEJA+ "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(FeignClientException.class)
  public ResponseEntity<?> handleFeignClientException(FeignClientException ex,WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
            new ErrorResponse(
                    ErrorType.valueOf(errorMessage[0]),
                    ErrorCode.valueOf(errorMessage[1]),
                    errorMessage[2],
                    Constants.DOC_URL_RESOURCE_NOT_FOUND,
                    request.getDescription(false),
                    BEEJA+ "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex,WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
            new ErrorResponse(
                    ErrorType.valueOf(errorMessage[0]),
                    ErrorCode.valueOf(errorMessage[1]),
                    errorMessage[2],
                    Constants.DOC_URL_RESOURCE_NOT_FOUND,
                    request.getDescription(false),
                    BEEJA+ "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  @ExceptionHandler(InterviewerException.class)
  public ResponseEntity<?> handleInterviewerException(InterviewerException ex,WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
            new ErrorResponse(
                    ErrorType.valueOf(errorMessage[0]),
                    ErrorCode.valueOf(errorMessage[1]),
                    errorMessage[2],
                    Constants.DOC_URL_RESOURCE_NOT_FOUND,
                    request.getDescription(false),
                    BEEJA+ "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<?> handleBadRequestException(BadRequestException ex,WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
            new ErrorResponse(
                    ErrorType.valueOf(errorMessage[0]),
                    ErrorCode.valueOf(errorMessage[1]),
                    errorMessage[2],
                    Constants.DOC_URL_RESOURCE_NOT_FOUND,
                    request.getDescription(false),
                    BEEJA+ "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<?> handleException(Exception ex, WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
            new ErrorResponse(
                    ErrorType.valueOf(errorMessage[0]),
                    ErrorCode.valueOf(errorMessage[1]),
                    errorMessage[2],
                    Constants.DOC_URL_RESOURCE_NOT_FOUND,
                    request.getDescription(false),
                    BEEJA+ "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

  public String[] convertStringToArray(String commaSeparatedString) {
    return commaSeparatedString.split(",");
  }
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex, WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
            ErrorType.valueOf(errorMessage[0]),
            ErrorCode.valueOf(errorMessage[1]),
            errorMessage[2],
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            Constants.BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }
}
