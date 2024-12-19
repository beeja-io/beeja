package com.beeja.api.accounts.requests;

import lombok.Data;

@Data
public class NewOrganizationEmailRequest {
  private String templateCode;
  private String toMail;
  private String employeeId;
  private String employeeName;
  private String verificationToken;
  private String notificationCode = "ORGANIZATION_ONBOARD";
  private String organizationId;
  private String organizationName;
}
