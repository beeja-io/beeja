package com.beeja.api.accounts.requests;

import java.util.Set;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
  private Set<String> roles;
}
