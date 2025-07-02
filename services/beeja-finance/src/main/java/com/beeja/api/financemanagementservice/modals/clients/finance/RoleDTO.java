package com.beeja.api.financemanagementservice.modals.clients.finance;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
  private String id;
  private String name;
  private String code;
  private List<String> permissions;
}
