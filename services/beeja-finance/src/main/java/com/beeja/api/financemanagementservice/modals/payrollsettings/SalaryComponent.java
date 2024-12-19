package com.beeja.api.financemanagementservice.modals.payrollsettings;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryComponent {

  @NotNull private String id;

  @NotNull private String componentType;

  @NotNull private String componentName;

  private String calculationType;

  private Double percentageValue;

  private Double amount;

  private String payType;

  private Boolean considerForEPFContribution;

  private Boolean showInPayslip;

  @NotNull private Boolean isActive;

  @Field("created_at")
  @CreatedDate
  private Date createdAt;

  @Field("modified_at")
  @LastModifiedDate
  private Date modifiedAt;
}
