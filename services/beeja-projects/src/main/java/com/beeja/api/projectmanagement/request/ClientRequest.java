package com.beeja.api.projectmanagement.request;

import com.beeja.api.projectmanagement.enums.ClientType;
import com.beeja.api.projectmanagement.enums.Industry;
import com.beeja.api.projectmanagement.model.Address;
import com.beeja.api.projectmanagement.model.TaxDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ClientRequest {

  @NotNull(message = "Client name cannot be null")
  private String clientName;

  @NotNull(message = "Client type cannot be null")
  private ClientType clientType;

  @Email(message = "Invalid email format")
  private String email;

  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid contact number")
  private String contact;

  @Valid
  @NotNull(message = "Tax details cannot be null")
  private TaxDetails taxDetails;

  @Valid
  @NotNull(message = "Primary address cannot be null")
  private Address primaryAddress;

  private Address billingAddress;
  private boolean usePrimaryAsBillingAddress = true;

  private Industry industry;
  private String description;
  private MultipartFile logo;
}
