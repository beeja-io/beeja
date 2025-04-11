package com.beeja.api.filemanagement.exceptions;

import com.beeja.api.filemanagement.response.ErrorResponse;
import java.nio.file.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(value = GlobalExceptionHandler.class)
  public ResponseEntity<ErrorResponse> handleGlobalExceptionHandler(GlobalExceptionHandler e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("400 GLOBAL", e.getMessage()));
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("500 INTERNAL", e.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body("Unauthorized access: " + ex.getMessage());
  }

  @ExceptionHandler(UnAuthorisedException.class)
  public ResponseEntity<ErrorResponse> handleUnAuthorisedException(UnAuthorisedException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse("401 UNAUTHORIZED", e.getMessage()));
  }

  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFileNotFoundException(FileNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("404 NOT FOUND", e.getMessage()));
  }

  @ExceptionHandler(FileTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleFileTypeMismatchException(
      FileTypeMismatchException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("403 FORBIDDEN", e.getMessage()));
  }
}
