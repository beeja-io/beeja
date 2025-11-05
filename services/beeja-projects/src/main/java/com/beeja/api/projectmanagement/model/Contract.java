package com.beeja.api.projectmanagement.model;

import com.beeja.api.projectmanagement.enums.ContractBillingCurrency;
import com.beeja.api.projectmanagement.enums.ContractBillingType;
import com.beeja.api.projectmanagement.enums.ContractType;
import com.beeja.api.projectmanagement.enums.ProjectStatus;

import java.util.ArrayList;
import java.util.Date;
import com.beeja.api.projectmanagement.model.dto.ResourceAllocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

  @Id private String id;

  private String contractId;
  private String projectId;
  private String clientId;
  private String organizationId;

  private String contractTitle;
  private String description;

  private Double contractValue;
  private Date startDate;
  private Date endDate;
  private ProjectStatus status;
  private ContractBillingType billingType;
  private ContractType contractType;
  private String customContractType;
  private ContractBillingCurrency billingCurrency;
  private String signedBy;
  private Boolean isActive = true;
  private List<String> attachmentIds;

  @CreatedDate private Date createdAt;

  @LastModifiedDate private Date updatedAt;
  List<String> projectManagers;

  @Field("projectResources")
  private List<Object> rawProjectResources;

  @Transient
  private List<ResourceAllocation> projectResources;

  public List<ResourceAllocation> normalizeProjectResources(List<Object> rawList) {
    List<ResourceAllocation> result = new ArrayList<>();
    if (rawList != null) {
      for (Object item : rawList) {
        if (item instanceof String empId) {
          result.add(new ResourceAllocation(empId, 100.0));
        } else if (item instanceof Map map) {
          String empId = (String) map.get("employeeId");

          Object allocationRaw = map.get("allocationPercentage");
          Double allocation = null;

          if (allocationRaw instanceof Integer) {
            allocation = ((Integer) allocationRaw).doubleValue();
          } else if (allocationRaw instanceof Double) {
            allocation = (Double) allocationRaw;
          } else {
            allocation = 100.0;
          }

          result.add(new ResourceAllocation(empId, allocation));
        } else if (item instanceof ResourceAllocation ra) {
          result.add(ra);
        }
      }
    }
    return result;
  }
}
