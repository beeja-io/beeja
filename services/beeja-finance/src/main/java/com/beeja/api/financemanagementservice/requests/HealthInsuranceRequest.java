package com.beeja.api.financemanagementservice.requests;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.enums.InstalmentType;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class HealthInsuranceRequest {

  private String employeeId;

  @Min(value = 1, message = Constants.AMT_GREATER_THAN_ZERO)
  private String grossPremium;

  private InstalmentType instalmentType;
  private Double instalmentAmount;
}
