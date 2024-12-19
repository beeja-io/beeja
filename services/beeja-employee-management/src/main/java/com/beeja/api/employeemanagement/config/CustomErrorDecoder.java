package com.beeja.api.employeemanagement.config;

import com.beeja.api.employeemanagement.exceptions.FeignClientException;
import com.beeja.api.employeemanagement.exceptions.GlobalExceptionHandler;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
      return new GlobalExceptionHandler(e.getMessage());
    }
    return new FeignClientException(responseBody.toString());
  }
}
