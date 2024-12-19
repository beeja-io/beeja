package com.beeja.api.financemanagementservice.modals.clients.finance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private List<RoleDTO> roles;
  private String employeeId;
  private OrganizationDTO organizations;
  private Map<String, String> userPreferences;
  private String createdBy;
  private String modifiedBy;
  private String createdAt;
  private String modifiedAt;
  private boolean active;
}
