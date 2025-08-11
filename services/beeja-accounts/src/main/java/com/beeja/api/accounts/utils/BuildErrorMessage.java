package com.beeja.api.accounts.utils;

import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;

public class BuildErrorMessage {
  public static String buildErrorMessage(ErrorType errorType, ErrorCode errorCode, String message) {
    return String.format("%s,%s,%s", errorType, errorCode, message); 
  }
}
