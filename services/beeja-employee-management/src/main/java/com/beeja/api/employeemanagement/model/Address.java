package com.beeja.api.employeemanagement.model;

import lombok.Data;

@Data
public class Address {
  private String houseNumber;
  private String landMark;
  private String village;
  private String city;
  private String state;
  private String country;
  private String pinCode;
}
