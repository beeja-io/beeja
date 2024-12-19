package com.beeja.api.accounts.response;

import lombok.Data;

@Data
public class AddressResponse {
  private String pincode;
  private String state;
  private String country;
  private String district;
}
