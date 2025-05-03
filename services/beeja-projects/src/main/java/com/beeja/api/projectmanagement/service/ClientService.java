package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.request.ClientRequest;

import java.util.List;

public interface ClientService {
    Client addClientToOrganization(ClientRequest client) throws Exception;
    Client updateClientOfOrganization(ClientRequest clientRequest, String clientId) throws Exception;
    Client getClientById(String clientId) throws Exception;
    List<Client> getAllClientsOfOrganization();
}
