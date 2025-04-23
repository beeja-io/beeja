package com.beeja.api.employeemanagement.model.clients.accounts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private Set<RoleDTO> roles;
  private String employeeId;
  private OrganizationDTO organizations;
  private Map<String, String> userPreferences;
  private String createdBy;
  private String modifiedBy;
  private String createdAt;
  private String modifiedAt;
  private boolean active;
  private String password;
}
