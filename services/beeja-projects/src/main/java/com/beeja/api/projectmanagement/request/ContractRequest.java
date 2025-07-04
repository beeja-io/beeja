package com.beeja.api.projectmanagement.request;

import com.beeja.api.projectmanagement.enums.ContractBillingType;
import com.beeja.api.projectmanagement.enums.ContractType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractRequest {
  private String projectId;
  private String clientId;
  private String contractTitle;
  private String description;
  private Double contractValue;
  private Date startDate;
  private Date endDate;
  private String signedBy;
  private ContractType contractType;
  private ContractBillingType billingType;
  List<String> projectManagers;
  List<String> projectResources;
}
