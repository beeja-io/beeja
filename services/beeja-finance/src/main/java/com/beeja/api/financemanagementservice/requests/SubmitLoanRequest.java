package com.beeja.api.financemanagementservice.requests;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.enums.LoanType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class SubmitLoanRequest {

  @NotNull(message = Constants.LOAN_TYPE_CANNOT_BE_NULL)
  private LoanType loanType;

  @Min(value = 1, message = Constants.AMT_GREATER_THAN_ZERO)
  private double amount;

  @Min(value = 1, message = Constants.MONTHLY_EMI_MUST_GREATER_THAN_ZERO)
  private double monthlyEMI;

  private String purpose;

  @Min(value = 1, message = Constants.EMI_TENURE_MUST_GREATER_THAN_ZERO)
  private int emiTenure;

  private Date emiStartDate;
}
