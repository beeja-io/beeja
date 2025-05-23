package com.beeja.api.accounts.model.Organization;

import lombok.Data;

@Data
public class LoanLimit {
  private int monitorLoan = 30000;
  private boolean isMonitorLoanEnabled = true;
  private int personalLoan = 100000;
  private boolean isPersonalLoanEnabled = true;
  private int salaryMultiplier = 1;
  private boolean isSalaryMultiplierEnabled = false;


}
