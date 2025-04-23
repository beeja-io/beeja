package com.beeja.api.employeemanagement.model.clients.accounts;

import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrganizationDTO {
  private String id;
  private String name;
  private String email;
  private String subscriptionId;
  private String location;
  private String emailDomain;
  private String contactMail;
  private String website;
  private HashMap<String, Object> preferences;
  private HashMap<String, Object> address;
  private String filingAddress;
  private HashMap<String, Object> accounts;
  private HashMap<String, Object> loanLimit;
  private String logoFileId;
}
