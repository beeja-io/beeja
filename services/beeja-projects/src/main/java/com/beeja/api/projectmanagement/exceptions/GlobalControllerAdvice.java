package com.beeja.api.projectmanagement.exceptions;

import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
            .findFirst()
            .orElse("Validation error occurred");

    ErrorResponse errorResponse = new ErrorResponse(
            ErrorType.INVALID_REQUEST,
            ErrorCode.VALIDATION_ERROR,
            errorMessage,
            request.getRequestURI()

    );

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(ResourceAlreadyFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceAlreadyFoundException(ResourceAlreadyFoundException e,HttpServletRequest request){
    String path = request.getRequestURI();
    ErrorResponse errorResponse = BuildErrorMessage.buildErrorMessage(
            ErrorType.CONFLICT,
            ErrorCode.RESOURCE_ALREADY_EXISTS,
            e.getMessage()
    );
    errorResponse.setPath(path);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

  }
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(
          ResourceNotFoundException ex,
          HttpServletRequest request) {
    String path = request.getRequestURI();
    ErrorResponse errorResponse = BuildErrorMessage.buildErrorMessage(
            ErrorType.NOT_FOUND,
            ErrorCode.RESOURCE_NOT_FOUND,
            ex.getMessage()

    );
    errorResponse.setPath(path);
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
          ValidationException ex,
          HttpServletRequest request) {
    String path = request.getRequestURI();
    ErrorResponse errorResponse = BuildErrorMessage.buildErrorMessage(
            ErrorType.VALIDATION_ERROR,
            ErrorCode.INVALID_ARGUMENT,
            ex.getMessage()
    );
    errorResponse.setPath(path);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(DatabaseException.class)
  public ResponseEntity<ErrorResponse> handleDatabaseException(
          DatabaseException ex,
          HttpServletRequest request) {
    String path = request.getRequestURI();
    ErrorResponse errorResponse = BuildErrorMessage.buildErrorMessage(
            ErrorType.DATABASE_ERROR,
            ErrorCode.MONGO_SAVE_FAILED,
            ex.getMessage()
    );
    errorResponse.setPath(path);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
