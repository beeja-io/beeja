package com.beeja.api.accounts.requests;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRoleRequest {
  private Set<String> roles;
}
