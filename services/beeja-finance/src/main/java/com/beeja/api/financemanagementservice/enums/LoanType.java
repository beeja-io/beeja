package com.beeja.api.financemanagementservice.enums;

import lombok.Getter;

@Getter
public enum LoanType {
  MONITOR_LOAN("Monitor Loan"),
  PERSONAL_LOAN("Personal Loan"),
  ADVANCE_SALARY("Advance Salary");

  private final String displayName;

  LoanType(String displayName) {
    this.displayName = displayName;
  }
}
