package com.beeja.api.accounts.requests;

import com.beeja.api.accounts.model.UserPreferences;
import lombok.Data;

@Data
public class UpdateUserRequest {
  private String employeeId;
  private String firstName;
  private String lastName;
  private UserPreferences userPreferences;
  private String email;
}
