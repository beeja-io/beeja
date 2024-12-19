package com.beeja.api.employeemanagement.requests;

import com.beeja.api.employeemanagement.model.BankDetails;
import com.beeja.api.employeemanagement.model.KYCDetails;
import lombok.Data;

@Data
public class UpdateKYCRequest {

  private KYCDetails kycDetails;
  private BankDetails bankDetails;
}
