package com.beeja.api.employeemanagement.exceptions;

import com.beeja.api.employeemanagement.enums.ErrorCode;
import com.beeja.api.employeemanagement.enums.ErrorType;
import com.beeja.api.employeemanagement.response.ErrorResponse;
import com.beeja.api.employeemanagement.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.UUID;

@ControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(GlobalExceptionHandler.class)
  public ResponseEntity<ErrorResponse> handleGlobalExceptionHandler(
      GlobalExceptionHandler e, WebRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.API_ERROR,
            ErrorCode.SOMETHING_WENT_WRONG,
            Constants.SOMETHING_WENT_WRONG,
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            Constants.BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleMaxSizeException(
      MaxUploadSizeExceededException ex, WebRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.MEMORY_ERROR,
            ErrorCode.FILE_SIZE_LIMIT_EXCEEDED,
            Constants.FILE_LIMIT_ERROR,
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            Constants.BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
  }

  @ExceptionHandler(UnAuthorisedException.class)
  public ResponseEntity<ErrorResponse> handleUnAuthorisedException(
      UnAuthorisedException ex, WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.valueOf(errorMessage[0]),
            ErrorCode.valueOf(errorMessage[1]),
            errorMessage[2],
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            Constants.BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(FeignClientException.class)
  public ResponseEntity<ErrorResponse> feignClientBadRequest(
      FeignClientException ex, WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.valueOf(errorMessage[0]),
            ErrorCode.valueOf(errorMessage[1]),
            errorMessage[2],
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            Constants.BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ResourceNotFound.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(
      ResourceNotFound ex, WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.valueOf(errorMessage[0]),
            ErrorCode.valueOf(errorMessage[1]),
            errorMessage[2],
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            Constants.BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
    String[] errorMessage = convertStringToArray(ex.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.valueOf(errorMessage[0]),
            ErrorCode.valueOf(errorMessage[1]),
            errorMessage[2],
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            Constants.BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchElementException(
      NoSuchElementException ex, WebRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.RESOURCE_NOT_FOUND_ERROR,
            ErrorCode.RESOURCE_NOT_FOUND,
            Constants.EMPLOYEE_NOT_FOUND,
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            "ReferenceId-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  public String[] convertStringToArray(String commaSeparatedString) {
    return commaSeparatedString.split(",");
  }
}
