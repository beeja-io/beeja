package com.beeja.api.financemanagementservice.modals;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.enums.InstalmentType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "health_insurance")
public class HealthInsurance {
  @Id private String id;

  @NotBlank(message = Constants.EMPID_MUST_NOT_NULL)
  private String employeeId;

  @NotBlank(message = Constants.ORG_ID_NOT_NULL)
  private String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

  private String grossPremium;
  private InstalmentType instalmentType;
  private Double instalmentAmount;
  private Integer instalmentFrequency;

  @Field("created_at")
  @CreatedDate
  private Date createdAt;

  @Field("modified_at")
  @LastModifiedDate
  private Date modifiedAt;

  private String createdBy;

  private String modifiedBy;
}
