package com.beeja.api.expense.exceptions;

import static com.beeja.api.expense.utils.Constants.BEEJA;

import com.beeja.api.expense.config.properties.FileSizeConfig;
import com.beeja.api.expense.enums.ErrorCode;
import com.beeja.api.expense.enums.ErrorType;
import com.beeja.api.expense.response.ErrorResponse;
import com.beeja.api.expense.utils.Constants;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class ExceptionAdvice {

  @Autowired FileSizeConfig fileSizeConfig;

  @ExceptionHandler(CustomAccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<?> handleCustomAccessDeniedException(
      CustomAccessDeniedException ex, WebRequest request) {
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

  @ExceptionHandler(FeignClientException.class)
  public ResponseEntity<?> handleFeignClientException(FeignClientException ex, WebRequest request) {
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

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<?> handleMaxSizeException(
      MaxUploadSizeExceededException ex, WebRequest request) {
    String message = Constants.FILE_SIZE_EXCEEDED + fileSizeConfig.getMaxFileSize();
    ErrorResponse errorResponse =
        new ErrorResponse(
            ErrorType.FILE_SIZE_EXCEED,
            ErrorCode.MAX_FILE_SIZE_EXCEEDED,
            message,
            Constants.DOC_URL_RESOURCE_NOT_FOUND,
            request.getDescription(false),
            BEEJA + "-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
  }

  @ExceptionHandler(UnAuthorisedException.class)
  public ResponseEntity<?> handleUnAuthorisedException(
      UnAuthorisedException ex, WebRequest request) {
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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
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
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ExpenseAlreadySettledException.class)
  public ResponseEntity<String> handleExpenseAlreadySettledException(
      ExpenseAlreadySettledException ex, WebRequest request) {
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
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
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

  @ExceptionHandler(ExpenseNotFound.class)
  public ResponseEntity<ErrorResponse> handleExpenseNotFoundException(
      ExpenseNotFound e, WebRequest request) {
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

  @ExceptionHandler(OrganizationMismatchException.class)
  public ResponseEntity<ErrorResponse> handleOrganizationMismatchException(
      OrganizationMismatchException e, WebRequest request) {
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
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(handleInternalServerException.class)
  public ResponseEntity<ErrorResponse> handlehandleInternalServerException(
      handleInternalServerException e, WebRequest request) {
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

  public String[] convertStringToArray(String commaSeparatedString) {
    return commaSeparatedString.split(",");
  }
}
