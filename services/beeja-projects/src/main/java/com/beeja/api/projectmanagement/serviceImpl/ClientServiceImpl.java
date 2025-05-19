package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.FeignClientException;
import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ValidationException;
import com.beeja.api.projectmanagement.exceptions.UnAuthorisedException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.dto.File;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.responses.FileDownloadResultMetaData;
import com.beeja.api.projectmanagement.service.ClientService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import com.beeja.api.projectmanagement.config.LogoValidator;
import com.beeja.api.projectmanagement.client.FileClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.beeja.api.projectmanagement.utils.Constants.ERROR_IN_LOGO_UPLOAD;

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

    @Autowired
    LogoValidator logoValidator;

    @Autowired
    FileClient fileClient;


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
        if (client.getLogo() != null && !client.getLogo().isEmpty()) {
            String contentType = client.getLogo().getContentType();
            if (!logoValidator.getAllowedTypes().contains(contentType)) {
                throw new ValidationException(
                        new ErrorResponse(ErrorType.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR, Constants.FILE_NOT_ALLOWED, ""));

            }
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
            FileUploadRequest fileUploadRequest = new FileUploadRequest(client.getLogo(), client.getLogo().getName(), client.getDescription(), Constants.FILE_TYPE_PROJECT, Constants.ENTITY_TYPE_CLIENT, Constants.ENTITY_TYPE_CLIENT);
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
            long existingClientsCount = clientRepository.countByOrganizationId(UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
            if(existingClientsCount == 0){
                newClient.setClientId(UserContext.getLoggedInUserOrganization().get("name").toString().substring(0,3).toUpperCase() + "1");
            } else {
                newClient.setClientId(UserContext.getLoggedInUserOrganization().get("name").toString().substring(0,3).toUpperCase() + (existingClientsCount + 1));
            }
        } catch (Exception e){
            log.error(Constants.ERROR_IN_GENERATING_CLIENT_ID, e.getMessage());
            throw new Exception(Constants.ERROR_IN_GENERATING_CLIENT_ID);
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
            String contentType = clientRequest.getLogo().getContentType();
            if (!logoValidator.getAllowedTypes().contains(contentType)) {
                throw new ValidationException(
                        new ErrorResponse(ErrorType.VALIDATION_ERROR,ErrorCode.VALIDATION_ERROR, Constants.FILE_NOT_ALLOWED,""));
            }
            if(clientRequest.getLogo() != null) {
                String logoId = existingClient.getLogoId();
                FileUploadRequest fileUploadRequest = new FileUploadRequest(clientRequest.getLogo(), clientRequest.getLogo().getName(), clientRequest.getDescription(), Constants.FILE_TYPE_PROJECT, Constants.ENTITY_TYPE_CLIENT, Constants.ENTITY_TYPE_CLIENT);
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

    @Override
    public ByteArrayResource downloadFile(String fileId) throws Exception{

        String fileFormat;
        try {
            ResponseEntity<?> response = fileClient.getFileById(fileId);
            LinkedHashMap<String,Object> responseBody = (LinkedHashMap<String, Object>) response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            File file = objectMapper.convertValue(responseBody, File.class);
            fileFormat = file.getFileFormat();
            if(!Objects.equals(file.getEntityType(),"client")){
                log.error(Constants.UNAUTHORISED_ACCESS);
                throw new UnAuthorisedException(
                        Constants.UNAUTHORISED_ACCESS
                );
            }
        } catch (Exception e) {
            log.error(Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE
                            + " file Id: {}, error: {}",
                    fileId,
                    e.getMessage());
            throw new FeignClientException(
                    Constants.FILE_NOT_FOUND + fileId);
        }

        try {
            ResponseEntity<byte[]> fileResponse = fileClient.downloadFile(fileId);

            byte[] fileData = fileResponse.getBody();
            FileDownloadResultMetaData fileDownloadResultMetaData = getMetaData(fileResponse,fileFormat);

            return  new ByteArrayResource(Objects.requireNonNull(fileData)){
                @Override
                public String getFilename(){
                    return fileDownloadResultMetaData.getFileName() != null
                            ? fileDownloadResultMetaData.getFileName()
                            : "project_Beeja";
                }
            };
        } catch (Exception e){
            log.error(
                    Constants.ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE + "File Id : {}, error: {}",
                    fileId,
                    e.getMessage());
            throw new FeignClientException(Constants.ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE);
        }
    }

    private static FileDownloadResultMetaData getMetaData(ResponseEntity<byte[]> fileResponse,String fileFormat) {
        HttpHeaders headers = fileResponse.getHeaders();
        String contentDisposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
        String createdBy = headers.getFirst("createdby");
        String organizationId = headers.getFirst("organizationid");
        String entityId = headers.getFirst("entityId");
        String filename = headers.getFirst("filename");

        if ((filename == null || filename.isBlank()) && contentDisposition != null && !contentDisposition.isEmpty()) {
            int startIndex = contentDisposition.indexOf("filename=\"") + 10;
            int endIndex = contentDisposition.lastIndexOf("\"");
            if (endIndex != -1) {
                filename = contentDisposition.substring(startIndex, endIndex);
            }
        }
        return new FileDownloadResultMetaData(filename+"."+fileFormat, createdBy, entityId, organizationId);
    }
}
