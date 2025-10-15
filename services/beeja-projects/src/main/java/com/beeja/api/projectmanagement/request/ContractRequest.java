package com.beeja.api.projectmanagement.request;

import com.beeja.api.projectmanagement.enums.ContractBillingCurrency;
import com.beeja.api.projectmanagement.enums.ContractBillingType;
import com.beeja.api.projectmanagement.enums.ContractType;
import com.beeja.api.projectmanagement.model.dto.ResourceAllocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

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

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date startDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date endDate;
  private String signedBy;
  private ContractBillingCurrency billingCurrency;
  private ContractType contractType;
  private ContractBillingType billingType;
  private List<String> projectManagers;
  private List<ResourceAllocation> projectResources;
  private List<MultipartFile> attachments;
  private List<String> attachmentIds;
}
