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
import com.beeja.api.projectmanagement.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Override
    public Client addClientToOrganization(ClientRequest client) throws Exception {
        Client existingClient = clientRepository.findByEmailAndOrganizationId(client.getEmail(), UserContext.getLoggedInUserOrganization().get("id").toString());
        if (existingClient != null) {
            throw new ResourceAlreadyFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.CLIENT_ALREADY_FOUND,
                            ErrorCode.RESOURCE_ALREADY_EXISTS,
                            "Client Found with provided email"
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
        newClient.setOrganizationId(UserContext.getLoggedInUserOrganization().get("id").toString());

        try{
            newClient = clientRepository.save(newClient);
        } catch (Exception e){
            log.error("Error while saving client: {}", e.getMessage());
            throw new Exception("Error while saving new client to database");
        }
        return newClient;
    }

    @Override
    public Client updateClientOfOrganization(ClientRequest clientRequest, String clientId) throws Exception {
        Client existingClient = clientRepository.findByClientIdAndOrganizationId(clientId, UserContext.getLoggedInUserOrganization().get("id").toString());
        if (existingClient == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.CLIENT_NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Client Not Found with provided clientId"
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
            log.error("Error while updating client: {}", e.getMessage());
            throw new Exception("Error while updating client in database");
        }
        return existingClient;
    }

    @Override
    public Client getClientById(String clientId) throws Exception {
        Client client;
        try{
            client = clientRepository.findByClientIdAndOrganizationId(clientId, UserContext.getLoggedInUserOrganization().get("id").toString());
        } catch (Exception e) {
            log.error("Error while fetching client: {}", e.getMessage());
            throw new Exception("Error while fetching client from database");
        }
        if (client == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.CLIENT_NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Client Not Found with provided clientId"
                    )
            );
        }
        return client;
    }

    @Override
    public List<Client> getAllClientsOfOrganization() {
        // TODO: Implement pagination
        List<Client> clientsInOrganization = clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(UserContext.getLoggedInUserOrganization().get("id").toString());
        if(clientsInOrganization == null || clientsInOrganization.isEmpty()){
            return List.of();
        }
        return clientsInOrganization;
    }
}
