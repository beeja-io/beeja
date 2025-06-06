package com.beeja.api.financemanagementservice.modals;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.enums.Availability;
import com.beeja.api.financemanagementservice.enums.Type;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inventory")
public class Inventory {

  @Id private String id;

  @NotBlank(message = Constants.ORG_ID_NOT_NULL)
  private String organizationId;

  private String deviceNumber;

  @NotNull private String device;

  @NotNull private String provider;

  @NotNull private String model;

  @NotNull private Type type;

  private String os;

  private String specifications;

  private String ram;

  @NotNull private Availability availability;

  @Field("productId")
  @NotNull
  @Indexed(unique = true)
  private String productId;

  @NotNull
  @Min(value = 1, message = Constants.VALUE_MUST_GREATER_THAN_ZERO)
  private Double price;

  @NotNull private Date dateOfPurchase;

  private String comments;

  private String accessoryType;

  @Field("created_at")
  @CreatedDate
  private Date createdAt;

  private String createdBy;

  @Field("modified_at")
  @LastModifiedDate
  private Date modifiedAt;

  private String modifiedBy;
}
