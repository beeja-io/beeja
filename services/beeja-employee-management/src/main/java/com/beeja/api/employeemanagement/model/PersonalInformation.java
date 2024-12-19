package com.beeja.api.employeemanagement.model;

import lombok.Data;

import java.util.Date;

@Data
public class PersonalInformation {
  private String nationality;
  private Date dateOfBirth;
  private String gender;
  private String maritalStatus;
  private NomineeDetails nomineeDetails;
}
