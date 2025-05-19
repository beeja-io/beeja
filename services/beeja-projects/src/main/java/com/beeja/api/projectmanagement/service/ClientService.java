package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.request.ClientRequest;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

/**
 * Service interface for managing {@link Client} entities within an organization.
 */
public interface ClientService {

    /**
     * Adds a new client to an organization.
     * @param  client the request object containing the details of the client to be added
     * @return the created {@link Client} object
     * @throws Exception if there is any error while adding the client
     */
    Client addClientToOrganization(ClientRequest client) throws Exception;

    /**
     * Updates the details of an existing client in an organization.
     * @param clientRequest the request object containing the updated client details
     * @param clientId the unique identifier of the client to be updated
     * @return the updated {@link Client} object
     * @throws Exception if there is any error while updating the client
     */
    Client updateClientOfOrganization(ClientRequest clientRequest, String clientId) throws Exception;

    /**
     * Retrieves a client by its unique identifier.
     * @param clientId the unique identifier of the client
     * @return the {@link Client} object corresponding to the given client ID
     * @throws Exception if there is any error while retrieving the client
     */
    Client getClientById(String clientId) throws Exception;

    /**
     * Retrieves all clients associated with an organization.
     * @return a list of {@link Client} objects for the organization
     */
    List<Client> getAllClientsOfOrganization();

    ByteArrayResource downloadFile(String fileId) throws Exception;
}
