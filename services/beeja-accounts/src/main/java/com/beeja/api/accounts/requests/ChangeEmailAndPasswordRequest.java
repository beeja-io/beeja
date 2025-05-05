package com.beeja.api.accounts.requests;

import lombok.Data;

@Data
public class ChangeEmailAndPasswordRequest {
  private String employeeId;
  private String newEmail;
  private String currentPassword;
  private String newPassword;
  private String confirmPassword;
}
