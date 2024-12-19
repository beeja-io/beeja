package com.beeja.api.accounts.exceptions;

import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import com.beeja.api.accounts.response.ErrorResponse;
import com.beeja.api.accounts.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.beeja.api.accounts.utils.Constants.BEEJA;

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
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(ResourceAlreadyFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceAlreadyFoundException(
      ResourceAlreadyFoundException e, WebRequest request) {
    String[] errorMessage = convertStringToArray(e.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.valueOf(errorMessage[0]),
            ErrorCode.valueOf(errorMessage[1]),
            errorMessage[2],
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
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
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflictException(
      ConflictException ex, WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.valueOf(errorMessage[0]),
            ErrorCode.valueOf(errorMessage[1]),
            errorMessage[2],
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(CustomAccessDenied.class)
  public ResponseEntity<ErrorResponse> handleCustomAccessDenied(
      CustomAccessDenied ex, WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.valueOf(errorMessage[0]),
            ErrorCode.valueOf(errorMessage[1]),
            errorMessage[2],
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
    //    String[] errorMessage = convertStringToArray(ex.getMessage());
    //    ErrorResponse errorResponse =
    //        new ErrorResponse(
    //            ErrorType.valueOf(errorMessage[0]),
    //            ErrorCode.valueOf(errorMessage[1]),
    //            errorMessage[2],
    //            Constants.DOC_URL_RESOURCE_NOT_FOUND,
    //            request.getDescription(false),
    //            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
    //            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public String[] convertStringToArray(String commaSeparatedString) {
    return commaSeparatedString.split(",");
  }
}
