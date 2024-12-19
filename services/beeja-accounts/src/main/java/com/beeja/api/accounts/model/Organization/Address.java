package com.beeja.api.accounts.model.Organization;

import lombok.Data;

@Data
public class Address {
  private String addressOne;
  private String addressTwo;
  private String country;
  private String state;
  private String city;
  private String pinCode;
}
