package com.beeja.api.accounts.model.Organization;

import com.beeja.api.accounts.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "organizations")
public class Organization {
  @Id private String id;

  @NotBlank(message = Constants.VALIDATION_ORGANIZATION_NAME_IS_REQUIRED)
  private String name;

  @NotBlank(message = Constants.VALIDATION_ORGANIZATION_EMAIL_IS_REQUIRED)
  @Email(message = Constants.VALIDATION_ORGANIZATION_EMAIL_IS_INVALID_FORMAT)
  private String email;

  private String subscriptionId;
  private String emailDomain;

  @NotBlank(message = Constants.VALIDATION_ORGANIZATION_OWNER_CONTACT_EMAIL_IS_REQUIRED)
  @Email(message = Constants.VALIDATION_ORGANIZATION_OWNER_CONTACT_EMAIL_IS_INVALID_FORMAT)
  private String contactMail;

  @URL(message = Constants.VALIDATION_ORGANIZATION_WEBSITE_INVALID_FORMAT)
  @NotBlank(message = Constants.VALIDATION_ORGANIZATION_WEBSITE_REQUIRED)
  private String website;

  private Preferences preferences = new Preferences();
  private Address address;
  private String filingAddress;

  @JsonIgnore private Accounts accounts;

  private String logoFileId;

  private LoanLimit loanLimit = new LoanLimit();

  public Organization(String org123) {
  }

  public String get(String id) {
    return id;
  }
}
