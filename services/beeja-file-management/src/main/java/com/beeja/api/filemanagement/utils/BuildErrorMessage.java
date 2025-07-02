package com.beeja.api.filemanagement.utils;

import com.beeja.api.filemanagement.enums.ErrorCode;
import com.beeja.api.filemanagement.enums.ErrorType;

public class BuildErrorMessage {
  public static String buildErrorMessage(ErrorType errorType, ErrorCode errorCode, String message) {
    return String.format("%s,%s,%s", errorType, errorCode, message);
  }
}
