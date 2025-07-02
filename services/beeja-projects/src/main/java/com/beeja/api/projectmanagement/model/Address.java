package com.beeja.api.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
  private String street;
  private String city;
  private String state;
  private String postalCode;
  private String country;
}
