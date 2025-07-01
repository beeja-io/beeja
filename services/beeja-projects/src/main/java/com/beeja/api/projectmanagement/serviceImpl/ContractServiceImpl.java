package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.FeignClientException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.service.ContractService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ContractService} interface.
 *
 * <p>This service handles the business logic for managing contracts related to projects, including
 * creation, retrieval, and updates of contracts.
 */
@Slf4j
@Service
public class ContractServiceImpl implements ContractService {

  @Autowired private ContractRepository contractRepository;

  @Autowired private ProjectRepository projectRepository;

    @Autowired
    AccountClient accountClient;

  /**
   * Creates a new {@link Contract} for a given {@link Project} and {@link ContractRequest}.
   *
   * @param request the {@link ContractRequest} containing details to create the {@link Contract}
   * @return the newly created {@link Contract}
   * @throws ResourceNotFoundException if no {@link Project} exists for the provided {@link
   *     ContractRequest#getProjectId()}
   */
  @Override
  public Contract createContract(ContractRequest request) {
    Project project =
        projectRepository.findByProjectIdAndClientIdAndOrganizationId(
            request.getProjectId(),
            request.getClientId(),
            UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());

    if (project == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, Constants.PROJECT_NOT_FOUND));
    }

    Contract contract = new Contract();
    contract.setContractId(UUID.randomUUID().toString().substring(0, 7).toUpperCase());
    contract.setProjectId(request.getProjectId());
    contract.setClientId(request.getClientId());
    contract.setContractTitle(request.getContractTitle());
    contract.setDescription(request.getDescription());
    contract.setContractValue(request.getContractValue());
    contract.setStartDate(request.getStartDate());
    contract.setEndDate(request.getEndDate());
    contract.setSignedBy(request.getSignedBy());
    contract.setOrganizationId(project.getOrganizationId());

      if(request.getProjectManagers() != null && !request.getProjectManagers().isEmpty()){
          try{
              List<String> validProjectManagers = accountClient.checkEmployeesPresentOrNot(request.getProjectManagers());
              contract.setProjectManagers(validProjectManagers);
          } catch (FeignClientException e){
              log.error(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS,e.getMessage(), e);
              throw new FeignClientException(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS);
          }
      }
      if(request.getProjectResources() !=null && !request.getProjectResources().isEmpty()){
          try {
              List<String> validProjectResources =  accountClient.checkEmployeesPresentOrNot(request.getProjectResources());
              contract.setProjectResources(validProjectResources);
          } catch (FeignClientException e){
              log.error(Constants.ERROR_IN_VALIDATE_PROJECT_RESOURCES,e.getMessage(), e);
              throw new FeignClientException(Constants.ERROR_IN_VALIDATE_PROJECT_RESOURCES);
          }
      }

    try {
      return contractRepository.save(contract);
    } catch (Exception e) {
      log.error(Constants.ERROR_SAVING_CONTRACT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_CREATION_ERROR,
              Constants.ERROR_SAVING_CONTRACT));
    }
  }

  /**
   * Retrieves a {@link Contract} by its unique identifier.
   *
   * @param contractId the unique identifier of the {@link Contract}
   * @return the {@link Contract} entity
   * @throws ResourceNotFoundException if no {@link Contract} is found with the provided contractId
   */
  @Override
  public Contract getContractById(String contractId) {
    Contract contract =
        contractRepository.findByContractIdAndOrganizationId(
            contractId, UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());

    if (contract == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, Constants.CONTRACT_NOT_FOUND));
    }
    return contract;
  }

  /**
   * Retrieves a list of {@link Contract} entities associated with a specific {@link Project}.
   *
   * @param projectId the unique identifier of the {@link Project}
   * @return a list of {@link Contract} entities
   */
  @Override
  public List<Contract> getContractsByProjectId(String projectId) {
    List<Contract> contracts =
        contractRepository.findByProjectIdAndOrganizationId(
            projectId, UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());

    return (contracts == null) ? List.of() : contracts;
  }

  /**
   * Updates an existing {@link Contract} based on the provided {@link ContractRequest}.
   *
   * @param contractId the unique identifier of the {@link Contract} to update
   * @param request the {@link ContractRequest} containing updated contract details
   * @return the updated {@link Contract}
   * @throws ResourceNotFoundException if no {@link Contract} is found with the provided contractId
   */
  @Override
  public Contract updateContract(String contractId, ContractRequest request) {
    Contract contract = getContractById(contractId);

    if (request.getContractTitle() != null) contract.setContractTitle(request.getContractTitle());
    if (request.getDescription() != null) contract.setDescription(request.getDescription());
    if (request.getContractValue() != null) contract.setContractValue(request.getContractValue());
    if (request.getStartDate() != null) contract.setStartDate(request.getStartDate());
    if (request.getEndDate() != null) contract.setEndDate(request.getEndDate());
    if (request.getSignedBy() != null) contract.setSignedBy(request.getSignedBy());

      if(request.getProjectManagers() != null && !request.getProjectManagers().isEmpty()){
          try{
              List<String> validProjectManagers = accountClient.checkEmployeesPresentOrNot(request.getProjectManagers());
              contract.setProjectManagers(validProjectManagers);
          } catch (FeignClientException e){
              log.error(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS,e.getMessage(), e);
              throw new FeignClientException(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS);
          }
      }
      if(request.getProjectResources() !=null && !request.getProjectResources().isEmpty()){
          try {
              List<String> validProjectResources =  accountClient.checkEmployeesPresentOrNot(request.getProjectResources());
              contract.setProjectResources(validProjectResources);
          } catch (FeignClientException e){
              log.error(Constants.ERROR_IN_VALIDATE_PROJECT_RESOURCES,e.getMessage(), e);
              throw new FeignClientException(Constants.ERROR_IN_VALIDATE_PROJECT_RESOURCES);
          }
      }

    try {
      return contractRepository.save(contract);
    } catch (Exception e) {
      log.error(Constants.ERROR_UPDATING_CONTRACT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_CREATION_ERROR,
              Constants.ERROR_UPDATING_CONTRACT));
    }
  }
}
