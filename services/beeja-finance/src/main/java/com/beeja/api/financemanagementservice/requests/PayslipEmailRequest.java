package com.beeja.api.financemanagementservice.requests;

import lombok.Data;

@Data
public class PayslipEmailRequest {
  private String templateCode;
  private String initiator;
  private String toMail;
  private String employeeId;
  private String employeeName;
  private String notificationCode = "PAYSLIP_SUCCESS";
  private String organizationName;
  private String payPeriod;
  private String year;
  private String month;
}
