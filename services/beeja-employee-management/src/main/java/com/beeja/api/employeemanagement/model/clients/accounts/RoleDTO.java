package com.beeja.api.employeemanagement.model.clients.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
  private String id;
  private String name;
  private String description;
  private Set<String> permissions;
  private String organizationId;
}
