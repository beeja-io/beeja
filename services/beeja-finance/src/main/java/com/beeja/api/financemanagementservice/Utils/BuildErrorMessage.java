package com.beeja.api.financemanagementservice.Utils;

import com.beeja.api.financemanagementservice.enums.ErrorCode;
import com.beeja.api.financemanagementservice.enums.ErrorType;

public class BuildErrorMessage {
  public static String buildErrorMessage(ErrorType errorType, ErrorCode errorCode, String message) {
    return String.format("%s,%s,%s", errorType, errorCode, message);
  }
}
