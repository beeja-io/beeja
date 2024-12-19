package com.beeja.api.financemanagementservice.exceptions;

import com.beeja.api.financemanagementservice.enums.ErrorCode;
import com.beeja.api.financemanagementservice.enums.ErrorType;
import com.beeja.api.financemanagementservice.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.beeja.api.financemanagementservice.Utils.Constants.BEEJA;
import static com.beeja.api.financemanagementservice.Utils.Constants.DOC_URL_RESOURCE_NOT_FOUND;

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
            DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
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
            DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
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
            DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(DuplicateProductIdException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateProductIdException(
      DuplicateProductIdException e, WebRequest request) {
    String[] errorMessage = convertStringToArray(e.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.valueOf(errorMessage[0]),
            ErrorCode.valueOf(errorMessage[1]),
            errorMessage[2],
            DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e, WebRequest request) {
    log.error(e.getMessage());
    String[] errorMessage = convertStringToArray(e.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.valueOf(errorMessage[0]),
            ErrorCode.valueOf(errorMessage[1]),
            errorMessage[2],
            DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  private String[] convertStringToArray(String commaSeparatedString) {
    return commaSeparatedString.split(",");
  }
}
