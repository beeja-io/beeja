package com.beeja.api.accounts.requests;

import lombok.Data;

@Data
public class NewUserEmailRequest {
  private String templateCode;
  private String initiator;
  private String toMail;
  private String employeeId;
  private String employeeName;
  private String password;
  private String notificationCode = "NEW_EMP";
  private String organizationId;
  private String organizationName;
}
