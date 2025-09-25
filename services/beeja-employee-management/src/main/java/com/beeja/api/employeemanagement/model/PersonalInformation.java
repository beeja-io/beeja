package com.beeja.api.employeemanagement.model;

import java.util.Date;
import lombok.Data;

@Data
public class PersonalInformation {
  private String nationality;
  private Date dateOfBirth;
  private String gender;
  private String maritalStatus;
  private NomineeDetails nomineeDetails;
  private String personalTaxId;
}
