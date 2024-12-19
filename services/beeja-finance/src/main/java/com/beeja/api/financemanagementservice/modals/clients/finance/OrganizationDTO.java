package com.beeja.api.financemanagementservice.modals.clients.finance;

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
}
