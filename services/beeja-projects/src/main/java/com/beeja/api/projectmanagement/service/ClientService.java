package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.dto.ClientDTO;

import java.util.List;
import java.util.Map;

public interface ClientService {
    Client addClient(Client client);

    Client updateClientPartially(String id, Map<String, Object> updates);

    Client getClientById(String clientId);

    List<ClientDTO> getClients();
}
