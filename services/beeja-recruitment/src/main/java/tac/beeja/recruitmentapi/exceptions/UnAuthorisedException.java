package tac.beeja.recruitmentapi.exceptions;

public class UnAuthorisedException extends RuntimeException {
  public UnAuthorisedException(String message) {
    super(message);
  }
}
