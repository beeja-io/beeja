package com.beeja.api.filemanagement.exceptions;

import static com.beeja.api.filemanagement.utils.Constants.BEEJA;

import com.beeja.api.filemanagement.enums.ErrorCode;
import com.beeja.api.filemanagement.enums.ErrorType;
import com.beeja.api.filemanagement.response.ErrorResponse;
import com.beeja.api.filemanagement.utils.Constants;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(value = GlobalExceptionHandler.class)
  public ResponseEntity<ErrorResponse> handleGlobalExceptionHandler(
      GlobalExceptionHandler e, WebRequest request) {
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
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ErrorResponse> exceptionHandler(Exception e, WebRequest request) {
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
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {
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

  @ExceptionHandler(UnAuthorisedException.class)
  public ResponseEntity<ErrorResponse> handleUnAuthorisedException(
      UnAuthorisedException e, WebRequest request) {
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
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFileNotFoundException(
      FileNotFoundException e, WebRequest request) {
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

  @ExceptionHandler(FileTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleFileTypeMismatchException(
      FileTypeMismatchException e, WebRequest request) {
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
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  public String[] convertStringToArray(String commaSeparatedString) {
    return commaSeparatedString.split(",");
  }
}
