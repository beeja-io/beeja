package com.beeja.api.expense.response;

import com.beeja.api.expense.enums.ErrorCode;
import com.beeja.api.expense.enums.ErrorType;
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
}
