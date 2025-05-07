package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.service.ClientService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the {@link ClientService} interface.
 *
 * Provides business logic for managing {@link Client} entities within an organization,
 * including adding, updating, retrieving a single client, and retrieving all clients.
 */
@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    /**
     * Adds a new {@link Client} to the currently logged-in user's organization.
     *
     * @param client the {@link ClientRequest} containing client details to be added
     * @return the newly created {@link Client} entity
     * @throws ResourceAlreadyFoundException if a {@link Client} with the same email already exists in the organization
     * @throws Exception if an error occurs while saving the {@link Client} to the database
     */
    @Override
    public Client addClientToOrganization(ClientRequest client) throws Exception {
        Client existingClient = clientRepository.findByEmailAndOrganizationId(client.getEmail(), UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
        if (existingClient != null) {
            throw new ResourceAlreadyFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.CLIENT_ALREADY_FOUND,
                            ErrorCode.RESOURCE_ALREADY_EXISTS,
                            Constants.CLIENT_FOUND_EMAIL
                    )
            );
        }
        Client newClient = new Client();
        if(client.getClientName() != null) {
            newClient.setClientName(client.getClientName());
        }
        if(client.getClientType() != null) {
            newClient.setClientType(client.getClientType());
        }
        if(client.getEmail() != null) {
            newClient.setEmail(client.getEmail());
        }
        if(client.getContact() != null) {
            newClient.setContact(client.getContact());
        }
        if(client.getTaxDetails() != null) {
            newClient.setTaxDetails(client.getTaxDetails());
        }
        if(client.getPrimaryAddress() != null) {
            newClient.setPrimaryAddress(client.getPrimaryAddress());
        }
        if(client.getBillingAddress() != null) {
            newClient.setBillingAddress(client.getBillingAddress());
        }
        if(client.getIndustry() != null) {
            newClient.setIndustry(client.getIndustry());
        }
        if(client.getDescription() != null) {
            newClient.setDescription(client.getDescription());
        }
        if(client.getLogo() != null) {
            // TODO: Implement logo upload
        }
        newClient.setOrganizationId(UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());

        try{
            newClient = clientRepository.save(newClient);
        } catch (Exception e){
            log.error(Constants.ERROR_SAVING_CLIENT, e.getMessage());
            throw new Exception(Constants.ERROR_SAVING_CLIENT);
        }
        return newClient;
    }

    /**
     * Updates an existing {@link Client} within the currently logged-in user's organization.
     *
     * @param clientRequest the {@link ClientRequest} containing updated client details
     * @param clientId the unique identifier of the {@link Client} to be updated
     * @return the updated {@link Client} entity
     * @throws ResourceNotFoundException if no {@link Client} is found with the provided clientId in the organization
     * @throws Exception if an error occurs while updating the {@link Client} in the database
     */
    @Override
    public Client updateClientOfOrganization(ClientRequest clientRequest, String clientId) throws Exception {
        Client existingClient = clientRepository.findByClientIdAndOrganizationId(clientId, UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
        if (existingClient == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.CLIENT_NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.CLIENT_NOT_FOUND
                    )
            );
        }
        if(clientRequest.getClientName() != null) {
            existingClient.setClientName(clientRequest.getClientName());
        }
        if(clientRequest.getClientType() != null) {
            existingClient.setClientType(clientRequest.getClientType());
        }
        if(clientRequest.getEmail() != null) {
            existingClient.setEmail(clientRequest.getEmail());
        }
        if(clientRequest.getContact() != null) {
            existingClient.setContact(clientRequest.getContact());
        }
        if(clientRequest.getTaxDetails() != null) {
            existingClient.setTaxDetails(clientRequest.getTaxDetails());
        }
        if(clientRequest.getPrimaryAddress() != null) {
            existingClient.setPrimaryAddress(clientRequest.getPrimaryAddress());
        }
        if(clientRequest.getBillingAddress() != null) {
            existingClient.setBillingAddress(clientRequest.getBillingAddress());
        }
        if(clientRequest.getIndustry() != null) {
            existingClient.setIndustry(clientRequest.getIndustry());
        }
        if(clientRequest.getDescription() != null) {
            existingClient.setDescription(clientRequest.getDescription());
        }
        if(clientRequest.getLogo() != null) {
//            TODO: Implement logo upload
        }
        try {
            existingClient = clientRepository.save(existingClient);
        } catch (Exception e) {
            log.error(Constants.ERROR_UPDATING_CLIENT, e.getMessage());
            throw new Exception(Constants.ERROR_UPDATING_CLIENT);
        }
        return existingClient;
    }

    /**
     * Retrieves a {@link Client} by its unique identifier within the currently logged-in user's organization.
     *
     * @param clientId the unique identifier of the {@link Client}
     * @return the {@link Client} entity
     * @throws ResourceNotFoundException if no {@link Client} is found with the provided clientId in the organization
     * @throws Exception if an error occurs while fetching the {@link Client} from the database
     */
    @Override
    public Client getClientById(String clientId) throws Exception {
        Client client;
        try{
            client = clientRepository.findByClientIdAndOrganizationId(clientId, UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
        } catch (Exception e) {
            log.error(Constants.ERROR_IN_FETCHING_CLIENTS, e.getMessage());
            throw new Exception(Constants.ERROR_IN_FETCHING_CLIENTS);
        }
        if (client == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.CLIENT_NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.CLIENT_NOT_FOUND
                    )
            );
        }
        return client;
    }

    /**
     * Retrieves all {@link Client} entities belonging to the currently logged-in user's organization.
     *
     * @return a list of {@link Client} entities; returns an empty list if no clients are found
     */
    @Override
    public List<Client> getAllClientsOfOrganization() {
        // TODO: Implement pagination
        List<Client> clientsInOrganization = clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
        if(clientsInOrganization == null || clientsInOrganization.isEmpty()){
            return List.of();
        }
        return clientsInOrganization;
    }
}
