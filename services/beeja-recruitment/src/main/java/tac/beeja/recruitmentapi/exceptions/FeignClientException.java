package tac.beeja.recruitmentapi.exceptions;

public class FeignClientException extends RuntimeException {
  public FeignClientException(String message) {
    super(message);
  }
}
