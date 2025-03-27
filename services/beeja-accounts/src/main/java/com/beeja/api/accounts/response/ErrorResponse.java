package com.beeja.api.accounts.response;

import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
  private ErrorType type;
  private ErrorCode code;
  private String message;
  private String docUrl;
  private String path;
  private String referenceId;
  private String timestamp;

  public ErrorResponse(ErrorType errorType, ErrorCode errorCode, String message) {
    this.type = errorType;
    this.code =errorCode;
    this.message=message;

  }
}
