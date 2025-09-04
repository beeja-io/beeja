package com.beeja.api.projectmanagement.model;

import com.beeja.api.projectmanagement.enums.ClientStatus;
import com.beeja.api.projectmanagement.enums.ClientType;
import com.beeja.api.projectmanagement.enums.Industry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "clientsDB")
@CompoundIndex(name = "client_name_type_index", def = "{'clientName': 1, 'clientType': 1}")
public class Client {
  @Id private String id;

  @Indexed(unique = true)
  @Field("client_name")
  @NotNull(message = "Client name cannot be null")
  private String clientName;

  @Indexed
  @Field("client_type")
  @NotNull(message = "Client type cannot be null")
  private ClientType clientType;

  @Indexed(unique = true)
  @Field("client_id")
  private String clientId;

  private String organizationId;

  @Email(message = "Invalid email format")
  private String email;

  private Industry industry;

  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid contact number")
  private String contact;

  private String description;
  private String logoId;

  @Valid
  @NotNull(message = "Tax Details cannot be null")
  private TaxDetails taxDetails;

  private ClientStatus status = ClientStatus.ACTIVE;

  private Address primaryAddress;
  private Address billingAddress;

  @Field("created_at")
  @CreatedDate
  private Date createdAt;

  @Field("updated_at")
  @LastModifiedDate
  private Date updatedAt;

  private boolean usePrimaryAsBillingAddress = false;

  public Address getBillingAddress() {
    return usePrimaryAsBillingAddress ? this.primaryAddress : this.billingAddress;
  }
}
