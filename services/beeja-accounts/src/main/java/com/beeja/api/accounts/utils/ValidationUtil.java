package com.beeja.api.accounts.utils;

import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidationUtil {

  /**
   * Processes validation errors from BindingResult and formats them into a JSON-friendly structure.
   *
   * @param bindingResult the BindingResult containing validation errors
   * @return a map containing validation error details
   */
  public static Map<String, Object> formatValidationErrors(BindingResult bindingResult) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("message", "Validation failed for the request.");
    errorResponse.put(
        "errors",
        bindingResult.getFieldErrors().stream()
            .map(
                error -> {
                  Map<String, String> fieldError = new HashMap<>();
                  fieldError.put("field", error.getField());
                  fieldError.put("error", error.getDefaultMessage());
                  return fieldError;
                })
            .collect(Collectors.toList()));
    return errorResponse;
  }

  /**
   * Checks if there are validation errors and returns a formatted response if errors exist.
   *
   * @param bindingResult the BindingResult containing validation errors
   * @return a map if errors are present, otherwise null
   */
  public static Map<String, Object> handleValidation(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return formatValidationErrors(bindingResult);
    }
    return null;
  }
}
