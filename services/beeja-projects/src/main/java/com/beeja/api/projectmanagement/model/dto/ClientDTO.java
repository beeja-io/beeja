package com.beeja.api.projectmanagement.model.dto;

import com.beeja.api.projectmanagement.enums.ClientType;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ClientDTO {
    private String clientId;
    private String clientName;
    private ClientType clientType;
    private String organizationId;

}
