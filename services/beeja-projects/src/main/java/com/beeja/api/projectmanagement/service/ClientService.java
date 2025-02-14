package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.enums.ClientStatus;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.dto.ClientDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ClientService {
    Client addClient(Client client);

    Client updateClientPartially(String clientId, Map<String, Object> updates);

    Client getClientById(String id);

    List<ClientDTO> getSortedClients();
}
