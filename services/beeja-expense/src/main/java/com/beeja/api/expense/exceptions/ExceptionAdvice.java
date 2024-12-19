package com.beeja.api.expense.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class ExceptionAdvice {
  @ExceptionHandler(CustomAccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<String> handleCustomAccessDeniedException(CustomAccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: " + ex.getMessage());
  }

  @ExceptionHandler(FeignClientException.class)
  public ResponseEntity<String> handleFeignClientException(FeignClientException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("An unexpected error occurred: " + ex.getMessage());
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
        .body("File size exceeds the maximum allowed limit! (Max is 10MB)");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> handleInternalServerException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred: " + ex.getMessage());
  }

  @ExceptionHandler(ExpenseAlreadySettledException.class)
  public ResponseEntity<String> handleExpenseAlreadySettledException(
      ExpenseAlreadySettledException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }
}
