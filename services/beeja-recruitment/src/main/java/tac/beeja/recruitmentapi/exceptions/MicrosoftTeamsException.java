package tac.beeja.recruitmentapi.exceptions;

public class MicrosoftTeamsException extends RuntimeException {

  public MicrosoftTeamsException(String message) {
    super(message);
  }

  public MicrosoftTeamsException(String message, Throwable cause) {
    super(message, cause);
  }
}
