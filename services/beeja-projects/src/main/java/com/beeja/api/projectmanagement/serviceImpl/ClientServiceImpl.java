package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.enums.*;
import com.beeja.api.projectmanagement.exceptions.DatabaseException;
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
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.beeja.api.projectmanagement.utils.Constants.OBJECT_MAPPER;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {


    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Client addClient(Client client){
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        Client existingClient = clientRepository.findByEmailAndOrganizationId(organizationId,client.getEmail());
        if (existingClient != null) {
            throw new ResourceAlreadyFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.CLIENT_ALREADY_FOUND,
                            ErrorCode.RESOURCE_ALREADY_EXISTS,
                            Constants.format(Constants.RESOURCE_ALREADY_FOUND, "Client", "email", client.getEmail())
                    )
            );
        }
        if (client.isUsePrimaryAsBillingAddress() && client.getBillingAddress() == null) {
            client.setBillingAddress(client.getPrimaryAddress());
        }
        if (client.getCreatedAt() == null) {
            client.setCreatedAt(Instant.now());
        }
        client.setOrganizationId(organizationId);
        client.setClientId(generateClientId(client.getClientName(),client.getOrganizationId()));
        try {
            return clientRepository.save(client);
        } catch (MongoException | DataAccessException e) {
            throw new DatabaseException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DATABASE_ERROR,
                            ErrorCode.MONGO_SAVE_FAILED,
                            Constants.format("Failed to save client details: %s", e.getMessage())
                    )
            );
        }
    }


    @Override
    public Client updateClientPartially(String id, Map<String, Object> updates)
    {
        System.out.println(id);
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        System.out.println(organizationId);
        ObjectMapper objectMapper = new ObjectMapper();
        Client existingClient = clientRepository.findByIdAndOrganizationId(id,organizationId);
        System.out.println(id);
        System.out.println(existingClient);
        if(existingClient == null){
              throw  new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.NOT_FOUND,
                                ErrorCode.CLIENT_NOT_FOUND,
                                Constants.format(Constants.RESOURCE_NOT_FOUND, "Client", "Id", id)

                        ));

        }

        updates.forEach((key, value) -> {
            if (value != null && !key.equals("_id") && !key.equals("clientId")) {
                try {
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
                        existingClient.setTaxDetails(convertValueSafely(value, TaxDetails.class, key));
                        break;
                    case "primaryAddress":
                        existingClient.setPrimaryAddress(convertValueSafely(value, Address.class, key));
                        break;
                    case "billingAddress":
                        existingClient.setBillingAddress(convertValueSafely(value, Address.class, key));
                        break;
                    default:
                        throw new ValidationException(
                                (BuildErrorMessage.buildErrorMessage(
                                        ErrorType.VALIDATION_ERROR,
                                        ErrorCode.FIELD_VALIDATION_MISSING,
                                        Constants.format(Constants.FIELD_NOT_EXIST_IN_ENTITY, "field not exist",key)
                                ))
                        );

        }
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_ENUM_VALUE,
                            Constants.format(Constants.INVALID_ENUM_VALUE, value, key, Arrays.toString(ClientType.values()))
                    )
            );
        }
            }
        });

        try {
            log.info("Saving updated client data for ID: {}", id);
            return clientRepository.save(existingClient);
        } catch (MongoException | DataAccessException e)
        {
            log.error("MongoDB error while saving client data: {}", e.getMessage(), e);
            throw new DatabaseException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DATABASE_ERROR,
                            ErrorCode.MONGO_SAVE_FAILED,
                            Constants.format(Constants.DB_ERROR_IN_SAVING_DETAILS,"Failed to update client details: ",existingClient)
                    )
            );
        }
    }

    @Override
    public List<ClientDTO> getClients() {
        String organizationId =  UserContext.getLoggedInUserOrganization().get("id").toString();
        List<Client> clients = clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(organizationId);
        if (clients.isEmpty()) {
            return Collections.emptyList();
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
        Client existingClient = clientRepository.findByClientIdAndOrganizationId(generatedClientId,organizationId);
        if (existingClient != null) {
            sequenceNumber++;
            formattedSequenceNumber = String.format("%03d", sequenceNumber);
            generatedClientId = prefix + formattedSequenceNumber;
        }
        return generatedClientId;
    }


    @Override
    public Client getClientById(String  clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.NOT_FOUND,
                                ErrorCode.CLIENT_NOT_FOUND,
                                Constants.format(Constants.RESOURCE_NOT_FOUND, "id", clientId)
                        )
                ));
    }

    /**
     * Safely converts a value to the specified class using ObjectMapper.
     */
    private <T> T convertValueSafely(Object value, Class<T> targetType, String fieldName) {
        if (!(value instanceof Map)) {
            throw new ValidationException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_JSON_STRUCTURE,
                            Constants.format("Invalid structure for field '%s', expected an object.", fieldName)
                    )
            );
        }

        try {
            return OBJECT_MAPPER.convertValue(value, targetType);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_JSON_STRUCTURE,
                            Constants.format("Invalid structure for field '%s'", fieldName)
                    )
            );
        }
    }

}
