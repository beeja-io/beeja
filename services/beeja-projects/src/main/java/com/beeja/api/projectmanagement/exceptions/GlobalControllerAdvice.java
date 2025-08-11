package com.beeja.api.projectmanagement.exceptions;

import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationException(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceAlreadyFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceAlreadyFoundException(
      ResourceAlreadyFoundException e, HttpServletRequest request) {
    String path = request.getRequestURI();
    ErrorResponse errorResponse =
        BuildErrorMessage.buildErrorMessage(
            ErrorType.CONFLICT, ErrorCode.RESOURCE_ALREADY_EXISTS, e.getMessage());
    errorResponse.setPath(path);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {
    String path = request.getRequestURI();
    ErrorResponse errorResponse =
        BuildErrorMessage.buildErrorMessage(
            ErrorType.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage());

    errorResponse.setPath(path);
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      ValidationException ex, HttpServletRequest request) {
    String path = request.getRequestURI();
    ErrorResponse errorResponse =
        BuildErrorMessage.buildErrorMessage(
            ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_ARGUMENT, ex.getMessage());
    errorResponse.setPath(path);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DatabaseException.class)
  public ResponseEntity<ErrorResponse> handleDatabaseException(
      DatabaseException ex, HttpServletRequest request) {
    String path = request.getRequestURI();
    ErrorResponse errorResponse =
        BuildErrorMessage.buildErrorMessage(
            ErrorType.DATABASE_ERROR, ErrorCode.MONGO_SAVE_FAILED, ex.getMessage());
    errorResponse.setPath(path);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFound(
      NoResourceFoundException ex, HttpServletRequest request) {

    String path = request.getRequestURI();

    ErrorResponse errorResponse =
        BuildErrorMessage.buildErrorMessage(
            ErrorType.API_ERROR,
            ErrorCode.INVALID_API_REQUEST,
            Constants.RESOURCE_NOT_FOUND + ex.getResourcePath());
    errorResponse.setPath(path);

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
    String path = request.getRequestURI();

    ErrorResponse errorResponse =
        BuildErrorMessage.buildErrorMessage(
            ErrorType.INTERNAL_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
    errorResponse.setPath(path);

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(CustomAccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleCustomAccessDeniedException(
      CustomAccessDeniedException ex, HttpServletRequest request) {
    String path = request.getRequestURI();

    ErrorResponse errorResponse =
        BuildErrorMessage.buildErrorMessage(
            ErrorType.ACCESS_DENIED, ErrorCode.PERM_403, Constants.NO_PERMISSION);
    errorResponse.setPath(path);
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(UnAuthorisedException.class)
  public ResponseEntity<ErrorResponse> handleUnAuthorisedException(
      UnAuthorisedException ex, HttpServletRequest request) {
    String path = request.getRequestURI();
    ErrorResponse errorResponse =
        BuildErrorMessage.buildErrorMessage(
            ErrorType.AUTHORIZATION_ERROR, ErrorCode.PERMISSION_MISSING, ex.getMessage());
    errorResponse.setPath(path);
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }
}
