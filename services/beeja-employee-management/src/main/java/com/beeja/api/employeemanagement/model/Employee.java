package com.beeja.api.employeemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
@CompoundIndex(
    name = "kyc_unique_aadhaarNumber",
    def = "{'kycDetails.aadhaarNumber': 1}",
    unique = true,
    sparse = true)
@CompoundIndex(
    name = "kyc_unique_panNumber",
    def = "{'kycDetails.panNumber': 1}",
    unique = true,
    sparse = true)
@CompoundIndex(
    name = "kyc_unique_passportNumber",
    def = "{'kycDetails.passportNumber': 1}",
    unique = true,
    sparse = true)
public class Employee {
  @Id private String id;
  private String beejaAccountId;
  private String employeeId;
  @JsonIgnore private int employeeNumber;
  private String employmentType;
  private String organizationId;
  private Address address;
  private PersonalInformation personalInformation;
  private Contact contact;
  private JobDetails jobDetails;
  private String position;
  private PFDetails pfDetails;
  private KYCDetails kycDetails;
  private BankDetails bankDetails;
  private String profilePictureId;
}
