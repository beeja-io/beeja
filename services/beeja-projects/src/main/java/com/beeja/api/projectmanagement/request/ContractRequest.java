package com.beeja.api.projectmanagement.request;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
