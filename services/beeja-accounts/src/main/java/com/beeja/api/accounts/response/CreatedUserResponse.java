package com.beeja.api.accounts.response;

import com.beeja.api.accounts.model.User;
import lombok.Data;

@Data
public class CreatedUserResponse {
  private User user;
  private String password;
}
