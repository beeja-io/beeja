package com.beeja.api.projectmanagement.model.dto;

import com.beeja.api.projectmanagement.enums.ClientType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
  private String clientId;
  private String clientName;
  private ClientType clientType;
  private String organizationId;
}
