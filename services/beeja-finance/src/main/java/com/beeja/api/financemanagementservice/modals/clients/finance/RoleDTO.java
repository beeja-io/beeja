package com.beeja.api.financemanagementservice.modals.clients.finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
  private String id;
  private String name;
  private String code;
  private List<String> permissions;
}
