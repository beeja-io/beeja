package com.beeja.api.expense.utils;

import com.beeja.api.expense.enums.ErrorCode;
import com.beeja.api.expense.enums.ErrorType;

public class BuildErrorMessage {
  public static String buildErrorMessage(ErrorType errorType, ErrorCode errorCode, String message) {
    return String.format("%s,%s,%s", errorType, errorCode, message);
  }
}
