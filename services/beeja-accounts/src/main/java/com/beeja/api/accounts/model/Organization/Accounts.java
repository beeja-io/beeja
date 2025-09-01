package com.beeja.api.accounts.model.Organization;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Accounts {
  private String pfNumber;
  private String tanNumber;

  @Pattern(
      regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}",
      message = "PAN number must be in the format XXXXX0000X")
  private String panNumber;

  private String esiNumber;
  private String linNumber;
  private String gstNumber;

  @Pattern(

          regexp = "^[A-Za-z0-9-]{10,15}$",
          message = "Tax number must be 10–15 characters, alphanumeric, and may include hyphen (-)"

        
  )
  private String taxNumber;
}
