package com.beeja.api.projectmanagement.serviceImpl;



import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ClientStatus;
import com.beeja.api.projectmanagement.enums.ClientType;
import com.beeja.api.projectmanagement.enums.Industry;
import com.beeja.api.projectmanagement.exceptions.DatabaseException;
import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ValidationException;
import com.beeja.api.projectmanagement.model.Address;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.TaxDetails;
import com.beeja.api.projectmanagement.model.dto.ClientDTO;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.service.ClientService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Arrays;

import static com.beeja.api.projectmanagement.utils.Constants.OBJECT_MAPPER;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {


    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Client addClient(ClientRequest clientRequest) {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

        Client existingClient = clientRepository.findByEmailAndOrganizationId( clientRequest.getEmail(),organizationId);
        if (existingClient != null) {
            throw new ResourceAlreadyFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.CLIENT_ALREADY_FOUND,
                            ErrorCode.RESOURCE_ALREADY_EXISTS,
                            Constants.format(Constants.RESOURCE_ALREADY_FOUND, "Client", "email", clientRequest.getEmail())
                    )
            );
        }

        Client client = new Client();
        client.setClientName(clientRequest.getClientName());
        client.setClientType(clientRequest.getClientType());
        client.setEmail(clientRequest.getEmail());
        client.setContact(clientRequest.getContact());
        client.setTaxDetails(clientRequest.getTaxDetails());
        client.setPrimaryAddress(clientRequest.getPrimaryAddress());
        client.setUsePrimaryAsBillingAddress(clientRequest.isUsePrimaryAsBillingAddress());

        if(clientRequest.getIndustry() != null) {
            client.setIndustry(clientRequest.getIndustry());
        }

        if(clientRequest.getDescription() != null) {
            client.setDescription(clientRequest.getDescription());
        }

        if(clientRequest.getLogo() != null) {
            client.setLogo(clientRequest.getLogo());
        }

        client.setOrganizationId(organizationId);
        client.setClientId(generateClientId(client.getClientName(), client.getOrganizationId()));

        if (client.isUsePrimaryAsBillingAddress() && client.getBillingAddress() == null && client.getPrimaryAddress() != null) {
            client.setBillingAddress(client.getPrimaryAddress());
        }
        if (client.getCreatedAt() == null) {
            client.setCreatedAt(Instant.now());
        }

        try {
            return clientRepository.save(client);
        } catch (MongoException | DataAccessException e) {
            throw new DatabaseException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DATABASE_ERROR,
                            ErrorCode.MONGO_SAVE_FAILED,
                            Constants.format(Constants.DB_ERROR_IN_SAVING_DETAILS,"Failed to save client details: ",clientRequest)
                    )
            );
        }
    }

    @Override
    public Client updateClientPartially(String id, Map<String, Object> updates) {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

        Client existingClient = clientRepository.findByIdAndOrganizationId(id, organizationId);

        if (existingClient == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.NOT_FOUND,
                            ErrorCode.CLIENT_NOT_FOUND,
                            Constants.format(Constants.RESOURCE_NOT_FOUND, "Client", "Id", id)
                    )
            );
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
                            TaxDetails existingTaxDetails = existingClient.getTaxDetails();
                            TaxDetails updatedTaxDetails = convertValueSafely(value, TaxDetails.class, key);

                            if (existingTaxDetails == null) {
                                existingClient.setTaxDetails(updatedTaxDetails);
                            } else {
                                if (updatedTaxDetails.getTaxCategory() != null) {
                                    existingTaxDetails.setTaxCategory(updatedTaxDetails.getTaxCategory());
                                }
                                if (updatedTaxDetails.getTaxNumber() != null) {
                                    existingTaxDetails.setTaxNumber(updatedTaxDetails.getTaxNumber());
                                }
                            }
                            break;
                        case "primaryAddress":
                            Address existingPrimaryAddress = existingClient.getPrimaryAddress();
                            Address updatedPrimaryAddress = convertValueSafely(value, Address.class, key);

                            if (existingPrimaryAddress == null) {
                                existingClient.setPrimaryAddress(updatedPrimaryAddress);
                            } else {
                                if (updatedPrimaryAddress.getStreet() != null) {
                                    existingPrimaryAddress.setStreet(updatedPrimaryAddress.getStreet());
                                }
                                if (updatedPrimaryAddress.getCity() != null) {
                                    existingPrimaryAddress.setCity(updatedPrimaryAddress.getCity());
                                }
                                if (updatedPrimaryAddress.getState() != null) {
                                    existingPrimaryAddress.setState(updatedPrimaryAddress.getState());
                                }
                                if (updatedPrimaryAddress.getPostalCode() != null) {
                                    existingPrimaryAddress.setPostalCode(updatedPrimaryAddress.getPostalCode());
                                }
                                if (updatedPrimaryAddress.getCountry() != null) {
                                    existingPrimaryAddress.setCountry(updatedPrimaryAddress.getCountry());
                                }
                            }
                            break;
                        case "billingAddress":
                            Address existingBillingAddress = existingClient.getBillingAddress();
                            Address updatedBillingAddress = convertValueSafely(value, Address.class, key);

                            if (existingBillingAddress == null) {
                                existingClient.setBillingAddress(updatedBillingAddress);
                            } else {
                                if (updatedBillingAddress.getStreet() != null) {
                                    existingBillingAddress.setStreet(updatedBillingAddress.getStreet());
                                }
                                if (updatedBillingAddress.getCity() != null) {
                                    existingBillingAddress.setCity(updatedBillingAddress.getCity());
                                }
                                if (updatedBillingAddress.getState() != null) {
                                    existingBillingAddress.setState(updatedBillingAddress.getState());
                                }
                                if (updatedBillingAddress.getPostalCode() != null) {
                                    existingBillingAddress.setPostalCode(updatedBillingAddress.getPostalCode());
                                }
                                if (updatedBillingAddress.getCountry() != null) {
                                    existingBillingAddress.setCountry(updatedBillingAddress.getCountry());
                                }
                            }
                            break;

                        default:
                            throw new ValidationException(
                                    BuildErrorMessage.buildErrorMessage(
                                            ErrorType.VALIDATION_ERROR,
                                            ErrorCode.FIELD_VALIDATION_MISSING,
                                            Constants.format(Constants.FIELD_NOT_EXIST_IN_ENTITY, "field not exist", key)
                                    )
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
            return clientRepository.save(existingClient);
        } catch (MongoException | DataAccessException e) {
            log.error("MongoDB error while saving client data: {}", e.getMessage(), e);
            throw new DatabaseException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DATABASE_ERROR,
                            ErrorCode.MONGO_SAVE_FAILED,
                            Constants.format(Constants.DB_ERROR_IN_SAVING_DETAILS, "Failed to update client details: ", existingClient)
                    )
            );
        }
    }

    @Override
    public List<ClientDTO> getClients() {
        String organizationId =  UserContext.getLoggedInUserOrganization().get("id").toString();
        List<ClientDTO> clients = clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(organizationId);
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

        return prefix + formattedSequenceNumber;
    }


    @Override
    public Client getClientById(String id) {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

        Client client= clientRepository.findByIdAndOrganizationId(id, organizationId);
        if (client==null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.NOT_FOUND,
                            ErrorCode.CLIENT_NOT_FOUND,
                            Constants.format(Constants.RESOURCE_NOT_FOUND, "id", id) // FIXED: Added missing `id`
                    )
            );
        }

        return client;
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
                            Constants.format(Constants.INVALID_JSON_STRUCTURE, fieldName)
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
                            Constants.format(Constants.INVALID_JSON_STRUCTURE, fieldName)
                    )
            );
        }
    }

}
