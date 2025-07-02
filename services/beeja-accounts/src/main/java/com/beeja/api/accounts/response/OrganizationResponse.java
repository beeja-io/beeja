package com.beeja.api.accounts.response;

import com.beeja.api.accounts.model.Organization.Accounts;
import com.beeja.api.accounts.model.Organization.Address;
import com.beeja.api.accounts.model.Organization.BankDetails;
import com.beeja.api.accounts.model.Organization.LoanLimit;
import com.beeja.api.accounts.model.Organization.Preferences;
import lombok.Data;

@Data
public class OrganizationResponse {
  private String id;

  private String name;
  private String email;

  private String subscriptionId;
  private String emailDomain;

  private String contactMail;

  private String website;

  private Preferences preferences;
  private Address address;
  private String filingAddress;

  private Accounts accounts;

  private String logoFileId;

  private LoanLimit loanLimit;

  private BankDetails bankDetails;
}
