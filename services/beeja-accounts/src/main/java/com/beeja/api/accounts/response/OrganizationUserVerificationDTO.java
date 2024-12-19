package com.beeja.api.accounts.response;

import lombok.Data;

@Data
public class OrganizationUserVerificationDTO {
  private String email;
  private String firstName;
  private String organizationName;
}
