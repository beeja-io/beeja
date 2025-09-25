package com.beeja.api.accounts.requests;

import com.beeja.api.accounts.model.Organization.Accounts;
import com.beeja.api.accounts.model.Organization.Address;
import com.beeja.api.accounts.model.Organization.BankDetails;
import com.beeja.api.accounts.model.Organization.Preferences;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationProfileUpdate {
  private String name;
  private String email;
  private String subscriptionId;
  private String contactMail;
  private String website;
  private Address address;
  private Accounts accounts;
  private BankDetails bankDetails;
  private Preferences preferences;
}
