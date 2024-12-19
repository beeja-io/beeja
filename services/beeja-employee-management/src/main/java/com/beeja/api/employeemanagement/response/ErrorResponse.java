package com.beeja.api.employeemanagement.response;

import com.beeja.api.employeemanagement.enums.ErrorCode;
import com.beeja.api.employeemanagement.enums.ErrorType;
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
