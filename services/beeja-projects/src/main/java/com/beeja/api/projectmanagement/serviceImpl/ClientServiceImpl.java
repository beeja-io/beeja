package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.FileClient;
import com.beeja.api.projectmanagement.config.LogoValidator;
import com.beeja.api.projectmanagement.enums.ClientType;
import com.beeja.api.projectmanagement.enums.TaxCategory;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.enums.Industry;
import com.beeja.api.projectmanagement.exceptions.FeignClientException;
import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ValidationException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.TaxDetails;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.service.ClientService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import static com.beeja.api.projectmanagement.utils.Constants.*;

/**
 * Implementation of the {@link ClientService} interface.
 *
 * <p>Provides business logic for managing {@link Client} entities within an organization, including
 * adding, updating, retrieving a single client, and retrieving all clients.
 */
@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

  @Autowired ClientRepository clientRepository;

  @Autowired LogoValidator logoValidator;

  @Autowired FileClient fileClient;


    private void checkDuplicateEmail(String email, String organizationId, String clientIdToExclude) {
        if (email == null || email.isEmpty()) return;

        Client clientWithEmail = clientRepository.findByEmailAndOrganizationId(email, organizationId);
        if (clientWithEmail != null) {
            if (clientIdToExclude == null || !clientWithEmail.getClientId().equals(clientIdToExclude)) {
                throw new ResourceAlreadyFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.CLIENT_ALREADY_FOUND,
                                ErrorCode.RESOURCE_ALREADY_EXISTS,
                                Constants.CLIENT_FOUND_EMAIL));
            }
        }
    }

    private String generateClientIdFromName(String clientName, long orgClientCount) {
        if (clientName == null || clientName.isEmpty()) {
            throw new IllegalArgumentException("Client name cannot be null or empty");
        }

        String[] words = clientName.trim().split("\\s+");
        String prefix;

        if (words.length == 1) {
            prefix = words[0].substring(0, Math.min(3, words[0].length())).toUpperCase();
        } else if (words.length == 2) {
            String part1 = words[0].substring(0, Math.min(2, words[0].length()));
            String part2 = words[1].substring(0, 1);
            prefix = (part1 + part2).toUpperCase();
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(3, words.length); i++) {
                sb.append(words[i].substring(0, 1).toUpperCase());
            }
            prefix = sb.toString();
        }

        long nextNumber = orgClientCount + 1;
        String numberPart = String.format("%03d", nextNumber);

        return prefix + numberPart;
    }



    private void handleClientType(ClientRequest clientRequest, Client clientEntity) {
        if (clientRequest.getClientType() != null) {
            clientEntity.setClientType(clientRequest.getClientType());

            if (clientRequest.getClientType() == ClientType.OTHER) {
                String customType = clientRequest.getCustomClientType();
                if (customType == null || customType.trim().isEmpty()) {
                    throw new ValidationException(
                            new ErrorResponse(
                                    ErrorType.VALIDATION_ERROR,
                                    ErrorCode.VALIDATION_ERROR,
                                    Constants.CUSTOM_CLIENT_TYPE_REQUIRED,
                                    "")
                    );
                }
                clientEntity.setCustomClientType(customType.trim());
            } else {
                clientEntity.setCustomClientType(null);
            }
        }
    }

    private void handleTaxDetails(ClientRequest clientRequest, Client clientEntity) {
        if (clientRequest.getTaxDetails() != null) {
            TaxDetails taxDetails = clientRequest.getTaxDetails();

            if (taxDetails.getTaxCategory() == TaxCategory.OTHER) {
                String customTaxCategory = taxDetails.getCustomTaxCategory();
                if (customTaxCategory == null || customTaxCategory.trim().isEmpty()) {
                    throw new ValidationException(
                            new ErrorResponse(
                                    ErrorType.VALIDATION_ERROR,
                                    ErrorCode.VALIDATION_ERROR,
                                    Constants.CUSTOM_TAX_CATEGORY_REQUIRED,
                                    "")
                    );
                }
                taxDetails.setCustomTaxCategory(customTaxCategory.trim());
            } else {
                taxDetails.setCustomTaxCategory(null);
            }

            clientEntity.setTaxDetails(taxDetails);
        }
    }
    private void handleIndustryType(ClientRequest clientRequest, Client clientEntity) {
        if (clientRequest.getIndustry() != null) {
            clientEntity.setIndustry(clientRequest.getIndustry());

            if (clientRequest.getIndustry() == Industry.OTHER) {
                String customIndustry = clientRequest.getCustomIndustry();
                if (customIndustry == null || customIndustry.trim().isEmpty()) {
                    throw new ValidationException(
                            new ErrorResponse(
                                    ErrorType.VALIDATION_ERROR,
                                    ErrorCode.VALIDATION_ERROR,
                                    Constants.CUSTOM_INDUSTRY_TYPE_REQUIRED,
                                    "")
                    );
                }
                clientEntity.setCustomIndustry(customIndustry.trim());
            } else {
                clientEntity.setCustomIndustry(null);
            }
        }
    }

  /**
   * Adds a new {@link Client} to the currently logged-in user's organization.
   *
   * @param client the {@link ClientRequest} containing client details to be added
   * @return the newly created {@link Client} entity
   * @throws ResourceAlreadyFoundException if a {@link Client} with the same email already exists in
   *     the organization
   * @throws Exception if an error occurs while saving the {@link Client} to the database
   */
  @Override
  public Client addClientToOrganization(ClientRequest client) throws Exception {

      String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();

      checkDuplicateEmail(client.getEmail(), organizationId, null);
    if (client.getLogo() != null && !client.getLogo().isEmpty()) {
      String contentType = client.getLogo().getContentType();
      if (!logoValidator.getAllowedTypes().contains(contentType)) {
        throw new ValidationException(
                new ErrorResponse(
                        ErrorType.VALIDATION_ERROR,
                        ErrorCode.VALIDATION_ERROR,
                        Constants.FILE_NOT_ALLOWED,
                        ""));
      }
    }
    Client newClient = new Client();
    handleClientType(client, newClient);
    handleIndustryType(client, newClient);
    handleTaxDetails(client, newClient);

    if (client.getClientName() != null) {
      newClient.setClientName(client.getClientName());
    }
    if (client.getEmail() != null) {
      newClient.setEmail(client.getEmail());
    }
    if (client.getContact() != null) {
      newClient.setContact(client.getContact());
    }
    if (client.getPrimaryAddress() != null) {
      newClient.setPrimaryAddress(client.getPrimaryAddress());
    }
    if (client.getBillingAddress() != null) {
      newClient.setBillingAddress(client.getBillingAddress());
    }
    if (client.getDescription() != null) {
      newClient.setDescription(client.getDescription());
    }
    if (client.getLogo() != null) {
      // TODO: Implement logo upload
      FileUploadRequest fileUploadRequest =
              new FileUploadRequest(
                      client.getLogo(),
                      client.getLogo().getName(),
                      client.getDescription(),
                      Constants.FILE_TYPE_PROJECT,
                      Constants.ENTITY_TYPE_CLIENT,
                      Constants.ENTITY_TYPE_CLIENT);
      try {
        ResponseEntity<?> response = fileClient.uploadFile(fileUploadRequest);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        String logId = responseBody.get(Constants.ID).toString();
        newClient.setLogoId(logId);
      } catch (Exception e) {
        log.error(ERROR_IN_LOGO_UPLOAD);
        throw new FeignClientException(ERROR_IN_LOGO_UPLOAD);
      }
    }

    try {
      long existingClientsCount = clientRepository.countByOrganizationId(
                  UserContext.getLoggedInUserOrganization().get(Constants.ID).toString()
          );

          String clientName = client.getClientName();
          String generatedClientId = generateClientIdFromName(clientName, existingClientsCount);

        newClient.setClientId(generatedClientId);

    } catch (Exception e) {
      log.error(Constants.ERROR_IN_GENERATING_CLIENT_ID, e);
      throw new Exception(Constants.ERROR_IN_GENERATING_CLIENT_ID);
    }
    newClient.setOrganizationId(
            UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());

    try {
      newClient = clientRepository.save(newClient);
    } catch (Exception e) {
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
   * @throws ResourceNotFoundException if no {@link Client} is found with the provided clientId in
   *     the organization
   * @throws Exception if an error occurs while updating the {@link Client} in the database
   */
  @Override
  public Client updateClientOfOrganization(ClientRequest clientRequest, String clientId)
          throws Exception {
      String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
      Client existingClient = clientRepository.findByClientIdAndOrganizationId(clientId, organizationId);

      if (existingClient == null) {
          throw new ResourceNotFoundException(
                  BuildErrorMessage.buildErrorMessage(
                          ErrorType.CLIENT_NOT_FOUND,
                          ErrorCode.RESOURCE_NOT_FOUND,
                          Constants.CLIENT_NOT_FOUND));
      }

      checkDuplicateEmail(clientRequest.getEmail(), organizationId, clientId);

    if (clientRequest.getClientName() != null) {
      existingClient.setClientName(clientRequest.getClientName());
    }
    if (clientRequest.getClientType() != null) {
      existingClient.setClientType(clientRequest.getClientType());
    }
    if (clientRequest.getEmail() != null) {
      existingClient.setEmail(clientRequest.getEmail());
    }
    if (clientRequest.getContact() != null) {
      existingClient.setContact(clientRequest.getContact());
    }
    if (clientRequest.getTaxDetails() != null) {
      existingClient.setTaxDetails(clientRequest.getTaxDetails());
    }
    if (clientRequest.getPrimaryAddress() != null) {
      existingClient.setPrimaryAddress(clientRequest.getPrimaryAddress());
    }
    if (clientRequest.getBillingAddress() != null) {
      existingClient.setBillingAddress(clientRequest.getBillingAddress());
    }
    if (clientRequest.getIndustry() != null) {
      existingClient.setIndustry(clientRequest.getIndustry());
    }
    if (clientRequest.getDescription() != null) {
      existingClient.setDescription(clientRequest.getDescription());
    }
    if (clientRequest.isRemoveLogo()) {
          String oldLogoId = existingClient.getLogoId();
          if (oldLogoId != null && !oldLogoId.isEmpty()) {
              try {
                  fileClient.deleteFile(oldLogoId);
                  log.info(DELETED_SUCCESSFULLY, oldLogoId);
              } catch (Exception e) {
                  log.error(ERROR_IN_DELETING_FILE_FROM_FILE_SERVICE);
                  throw new FeignClientException(ERROR_IN_DELETING_FILE_FROM_FILE_SERVICE);
              }
          }
          existingClient.setLogoId(null);

      } else if (clientRequest.getLogo() != null) {
        String contentType = clientRequest.getLogo().getContentType();
          if (!logoValidator.getAllowedTypes().contains(contentType)) {
              throw new ValidationException(
                      new ErrorResponse(
                              ErrorType.VALIDATION_ERROR,
                              ErrorCode.VALIDATION_ERROR,
                              Constants.FILE_NOT_ALLOWED,
                              ""));
          }

        String logoId = existingClient.getLogoId();
        FileUploadRequest fileUploadRequest =
                new FileUploadRequest(
                        clientRequest.getLogo(),
                        clientRequest.getLogo().getName(),
                        clientRequest.getDescription(),
                        Constants.FILE_TYPE_PROJECT,
                        Constants.ENTITY_TYPE_CLIENT,
                        Constants.ENTITY_TYPE_CLIENT);
        try {
          ResponseEntity<?> response;
          if (logoId != null && !logoId.isEmpty()) {
            response = fileClient.updateFile(logoId, fileUploadRequest);
          } else {
            response = fileClient.uploadFile(fileUploadRequest);
          }

          Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
          String newLogoId = responseBody.get(Constants.ID).toString();
          existingClient.setLogoId(newLogoId);

        } catch (Exception e) {
          log.error(ERROR_IN_LOGO_UPLOAD, e);
          throw new FeignClientException(ERROR_IN_LOGO_UPLOAD);
        }
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
   * Retrieves a {@link Client} by its unique identifier within the currently logged-in user's
   * organization.
   *
   * @param clientId the unique identifier of the {@link Client}
   * @return the {@link Client} entity
   * @throws ResourceNotFoundException if no {@link Client} is found with the provided clientId in
   *     the organization
   * @throws Exception if an error occurs while fetching the {@link Client} from the database
   */
  @Override
  public Client getClientById(String clientId) throws Exception {
    Client client;
    try {
      client =
              clientRepository.findByClientIdAndOrganizationId(
                      clientId, UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
    } catch (Exception e) {
      log.error(Constants.ERROR_IN_FETCHING_CLIENTS, e.getMessage());
      throw new Exception(Constants.ERROR_IN_FETCHING_CLIENTS);
    }
    if (client == null) {
      throw new ResourceNotFoundException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.CLIENT_NOT_FOUND,
                      ErrorCode.RESOURCE_NOT_FOUND,
                      Constants.CLIENT_NOT_FOUND));
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
    log.info("Fetching all clients for organization: {}",
            UserContext.getLoggedInUserOrganization().get(Constants.ID));
    // TODO: Implement pagination
    List<Client> clientsInOrganization =
            clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(
                    UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
    if (clientsInOrganization == null || clientsInOrganization.isEmpty()) {
      return List.of();
    }
    return clientsInOrganization;
  }

    @Override
    public Page<Client> getAllClientsOfOrganization(String organizationId, int pageNumber, int pageSize) {

        if (pageNumber < 1) pageNumber = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("createdAt").descending());
        return clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(organizationId, pageable);
    }
}

