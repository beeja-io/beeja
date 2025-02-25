package com.beeja.api.projectmanagement.exceptions;

import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerAdvice {

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(), Constants.INTERNAL_SERVER_ERROR));
  }
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
  List<String> errors = e.getBindingResult().getFieldErrors().stream()
          .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
          .collect(Collectors.toList());

  return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ErrorResponse("400 BAD_REQUEST", String.join("; ", errors)));
}

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("400 BAD_REQUEST", e.getMessage()));
  }

}
