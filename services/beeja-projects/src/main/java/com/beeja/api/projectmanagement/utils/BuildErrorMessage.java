package com.beeja.api.projectmanagement.utils;

import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.responses.ErrorResponse;

/** Utility class for building error response messages. */
public class BuildErrorMessage {

  /**
   * Builds an {@link ErrorResponse} with the given parameters.
   *
   * @param type the type of the error (e.g., client, server, validation)
   * @param code the specific error code representing the error
   * @param message a detailed message explaining the error
   * @return a new {@link ErrorResponse} object populated with the provided values
   */
  public static ErrorResponse buildErrorMessage(ErrorType type, ErrorCode code, String message) {
    return new ErrorResponse(type, code, message, null);
  }
}
