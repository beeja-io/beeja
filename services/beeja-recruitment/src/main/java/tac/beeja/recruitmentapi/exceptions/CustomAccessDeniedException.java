package tac.beeja.recruitmentapi.exceptions;

public class CustomAccessDeniedException extends RuntimeException {

  public CustomAccessDeniedException(String message) {
    super(message);
  }
}
