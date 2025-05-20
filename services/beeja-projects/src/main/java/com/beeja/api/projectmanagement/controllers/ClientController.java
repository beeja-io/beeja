package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.annotations.HasPermission;
import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing clients within an organization.
 * Provides endpoints for adding, updating, retrieving, and listing clients.
 */
@RestController
@RequestMapping("/v1/clients")
public class ClientController {

    @Autowired
    ClientService clientService;

    /**
     * Adds a new client to the organization.
     *
     * @param clientRequest The request object containing client details.
     * @return The created client along with HTTP status 201 (Created).
     * @throws Exception If an error occurs while adding the client.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @HasPermission(PermissionConstants.CREATE_CLIENT)
    public ResponseEntity<Client> addClientToOrganization(@ModelAttribute ClientRequest clientRequest) throws Exception {
        Client client = clientService.addClientToOrganization(clientRequest);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    /**
     * Updates an existing client's details in the organization.
     *
     * @param clientRequest The updated client data.
     * @param clientId      The ID of the client to update.
     * @return The updated client object along with HTTP status 200 (OK).
     * @throws Exception If an error occurs while updating the client.
     */
    @PutMapping("/{clientId}")
    @HasPermission(PermissionConstants.UPDATE_CLIENT)
    public ResponseEntity<Client> updateClientOfOrganization(ClientRequest clientRequest, @PathVariable String clientId) throws Exception {
        Client client = clientService.updateClientOfOrganization(clientRequest, clientId);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    /**
     * Retrieves a client by their unique ID.
     *
     * @param clientId The ID of the client to retrieve.
     * @return The client object along with HTTP status 200 (OK).
     * @throws Exception If the client is not found or an error occurs.
     */
    @GetMapping("/{clientId}")
    @HasPermission(PermissionConstants.GET_CLIENT)
    public ResponseEntity<Client> getClientById(@PathVariable String clientId) throws Exception {
        Client client = clientService.getClientById(clientId);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    /**
     * Retrieves all clients associated with the organization.
     *
     * @return A list of clients along with HTTP status 200 (OK).
     * @throws Exception If an error occurs while retrieving the clients.
     */
    @GetMapping
    @HasPermission(PermissionConstants.GET_CLIENT)
    public ResponseEntity<List<Client>> getAllClientsOfOrganization() throws Exception {
        List<Client> clients = clientService.getAllClientsOfOrganization();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }
}
