package tac.beeja.recruitmentapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

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

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("An unexpected error occurred: " + ex.getMessage());
  }

  @ExceptionHandler(InterviewerException.class)
  public ResponseEntity<String> handleInterviewerException(InterviewerException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("An unexpected error occurred: " + ex.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("An unexpected error occurred: " + ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> handleException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred: " + ex.getMessage());
  }
}
