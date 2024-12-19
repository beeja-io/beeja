package com.beeja.api.accounts.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class AddRoleRequest {

  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotNull(message = "Permissions cannot be null")
  @Size(min = 1, message = "At least one permission is required")
  private Set<String> permissions;
}
