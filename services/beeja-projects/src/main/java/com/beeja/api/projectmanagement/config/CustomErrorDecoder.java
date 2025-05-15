package com.beeja.api.projectmanagement.config;

import com.beeja.api.projectmanagement.exceptions.FeignClientException;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.stereotype.Component;

/**
 * Custom implementation of Feign's {@link ErrorDecoder} to handle error responses
 * from Feign clients by extracting and propagating the response body.
 * <p>
 * Converts the HTTP error response into a {@link FeignClientException}
 * containing the response body as its message for better error visibility.
 */
@Component
public class CustomErrorDecoder implements ErrorDecoder {

  /**
   * Decodes an HTTP error response from a Feign client call and returns an appropriate exception.
   * <p>
   * If the response body is available, it reads and includes the body content
   * in the {@link FeignClientException} message.
   *
   * @param methodKey the Feign method key identifying the method invoked
   * @param response the HTTP response returned by the remote service
   * @return an instance of {@link FeignClientException} containing the response body,
   *         or a generic {@link Exception} if an I/O error occurs while reading the body
   */
  @Override
  public Exception decode(String methodKey, Response response) {
    StringBuilder responseBody = new StringBuilder();
    try {
      BufferedReader reader =
              new BufferedReader(new InputStreamReader(response.body().asInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        responseBody.append(line);
      }
    } catch (IOException e) {
      return new Exception(e.getMessage());
    }
    return new FeignClientException(responseBody.toString());
  }
}
