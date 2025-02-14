package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.enums.*;
import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ValidationException;
import com.beeja.api.projectmanagement.model.Address;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.TaxDetails;
import com.beeja.api.projectmanagement.model.dto.ClientDTO;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.service.ClientService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MongoTemplate mongoTemplate;




    @Override
    public Client addClient(Client client){
        Client existingClient = clientRepository.findByEmail(client.getEmail());
        if (existingClient != null) {
            throw new ResourceAlreadyFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.CLIENT_ALREADY_FOUND,
                            ErrorCode.RESOURCE_ALREADY_EXISTS,
                            Constants.format(Constants.RESOURCE_ALREADY_FOUND, "Client", "email", client.getEmail()),
                            "v1/clients"
                    )
            );
        }
        if (client.isUsePrimaryAsBillingAddress() && client.getBillingAddress() == null) {
            client.setBillingAddress(client.getPrimaryAddress());
        }
        if (client.getCreatedAt() == null) {
            client.setCreatedAt(Instant.now());
        }
        client.setOrganizationId(UserContext.getLoggedInUserOrganization().get("id").toString());
        client.setClientId(generateClientId(client.getClientName(),client.getOrganizationId()));
        return clientRepository.save(client);
    }


    @Override
    public Client updateClientPartially(String clientId, Map<String, Object> updates) {
        ObjectMapper objectMapper = new ObjectMapper();
        Client existingClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.NOT_FOUND,
                                ErrorCode.CLIENT_NOT_FOUND,
                                Constants.format(Constants.RESOURCE_NOT_FOUND, "Client", "Id", clientId),
                                "v1/clients/update"
                        )
                ));

        updates.forEach((key, value) -> {
            if (value != null && !key.equals("_id") && !key.equals("clientId")) {
                switch (key) {
                    case "clientName":
                        existingClient.setClientName(value.toString());
                        break;
                    case "clientType":
                        existingClient.setClientType(ClientType.valueOf(value.toString()));
                        break;
                    case "email":
                        existingClient.setEmail(value.toString());
                        break;
                    case "contact":
                        existingClient.setContact(value.toString());
                        break;
                    case "description":
                        existingClient.setDescription(value.toString());
                        break;
                    case "industry":
                        existingClient.setIndustry(Industry.valueOf(value.toString()));
                        break;
                    case "logo":
                        existingClient.setLogo(value.toString());
                        break;
                    case "status":
                        existingClient.setStatus(ClientStatus.valueOf(value.toString()));
                        break;
                    case "taxDetails":
                        if (value instanceof Map) {
                            TaxDetails taxDetails = objectMapper.convertValue(value, TaxDetails.class);
                            existingClient.setTaxDetails(taxDetails);
                        }
                        break;
                    case "primaryAddress":
                        if (value instanceof Map) {
                            Address primaryAddress = objectMapper.convertValue(value, Address.class);
                            existingClient.setPrimaryAddress(primaryAddress);
                        }
                        break;
                    case "billingAddress":
                        if (value instanceof Map) {
                            Address billingAddress = objectMapper.convertValue(value, Address.class);
                            existingClient.setBillingAddress(billingAddress);
                        }
                        break;
                    default:
                        throw new ValidationException(
                                (BuildErrorMessage.buildErrorMessage(
                                        ErrorType.VALIDATION_ERROR,
                                        ErrorCode.FIELD_VALIDATION_MISSING,
                                        Constants.format(Constants.FIELD_NOT_EXIST_IN_ENTITY, key, "Client"),
                                        "v1/clients/update"
                                ))
                        );

                }
            }
        });

        return clientRepository.save(existingClient);
    }

    @Override
    public List<ClientDTO> getSortedClients() {
        String organizationId =  UserContext.getLoggedInUserOrganization().get("id").toString();
        List<Client> clients = clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(organizationId);
        if (clients.isEmpty()) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.NOT_FOUND,
                            ErrorCode.NO_CLIENTS_FOUND,
                            Constants.format(Constants.RESOURCE_NOT_FOUND, "Organization ID", organizationId),
                            "/clients"
                    )
            );
        }

        return clients.stream()
                .map(client -> new ClientDTO(
                        client.getClientId(),
                        client.getClientName(),
                        client.getClientType(),
                        client.getOrganizationId()
                ))
                .collect(Collectors.toList());
    }

    public String generateClientId(String clientName, String organizationId) {

        String[] nameParts =  clientName.split("(?=[A-Z])");
        StringBuilder prefixBuilder = new StringBuilder();

        for (String part : nameParts) {
            prefixBuilder.append(part.charAt(0));
        }

        String prefix = prefixBuilder.toString().toUpperCase();

        long clientCount = clientRepository.countByOrganizationId(organizationId);
        int sequenceNumber = (int) (clientCount + 1);

        String formattedSequenceNumber = String.format("%03d", sequenceNumber);

        String generatedClientId = prefix + formattedSequenceNumber;
        Client existingClient = clientRepository.findByClientId(generatedClientId);
        if (existingClient != null) {
            sequenceNumber++;
            formattedSequenceNumber = String.format("%03d", sequenceNumber);
            generatedClientId = prefix + formattedSequenceNumber;
        }
        return generatedClientId;
    }


    @Override
    public Client getClientById(String id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.NOT_FOUND,
                                ErrorCode.CLIENT_NOT_FOUND,
                                Constants.format(Constants.RESOURCE_NOT_FOUND, "id", id),
                                "/clients/" + id
                        )
                ));
    }


}
