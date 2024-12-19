package com.beeja.api.employeemanagement.model;

import lombok.Data;

@Data
public class BankDetails {

  private long accountNo;
  private String ifscCode;
  private String bankName;
  private String branchName;
}
