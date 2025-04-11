package com.beeja.api.expense.config;

import com.beeja.api.expense.exceptions.FeignClientException;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
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
