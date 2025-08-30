package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.exceptions.FeignClientException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.dto.EmployeeNameDTO;
import com.beeja.api.projectmanagement.model.dto.ResourceAllocation;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.responses.ContractResponsesDTO;
import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.responses.ResourceView;
import com.beeja.api.projectmanagement.service.ContractService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    @Autowired  private ClientRepository clientRepository;

    @Autowired
    AccountClient accountClient;

    @Autowired
    MongoTemplate mongoTemplate;


    @Autowired ProjectServiceImpl projectServiceImpl;

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
    contract.setBillingCurrency(request.getBillingCurrency());
    contract.setBillingType(request.getBillingType());
    contract.setContractType(request.getContractType());
    contract.setStatus(ProjectStatus.IN_PROGRESS);
      if(request.getProjectManagers() != null && !request.getProjectManagers().isEmpty()){
          try{
              List<String> validProjectManagers = projectServiceImpl.validateAndFetchEmployees(request.getProjectManagers());
              contract.setProjectManagers(validProjectManagers);
          } catch (FeignClientException e){
              log.error(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS,e.getMessage(), e);
              throw new FeignClientException(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS);
          }
      }
      if(request.getProjectResources() !=null && !request.getProjectResources().isEmpty()){
          try {
              List<String> employeeIds = request.getProjectResources().stream()
                      .map(ResourceAllocation::getEmployeeId)
                      .collect(Collectors.toList());

              List<String> validatedEmployeeIds = projectServiceImpl.validateAndFetchEmployees(employeeIds);

              List<Object> validProjectResources = request.getProjectResources().stream()
                      .filter(resource -> validatedEmployeeIds.contains(resource.getEmployeeId()))
                      .collect(Collectors.toList());

              contract.setRawProjectResources(validProjectResources);
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
      log.info(Constants.CONTRACT_FETCHING, contractId);

      Contract contract = contractRepository.findByContractIdAndOrganizationId(
              contractId,
              UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());

      if (contract == null) {
          log.warn(Constants.CONTRACT_NOT_FOUND, contractId);
          throw new ResourceNotFoundException(
                  BuildErrorMessage.buildErrorMessage(
                          ErrorType.NOT_FOUND,
                          ErrorCode.RESOURCE_NOT_FOUND,
                          Constants.CONTRACT_NOT_FOUND));
      }
      List<ResourceAllocation> rawResources = contract.normalizeProjectResources(contract.getRawProjectResources());

      if (rawResources != null && !rawResources.isEmpty()) {
          List<String> employeeIds = rawResources.stream()
                  .map(ResourceAllocation::getEmployeeId)
                  .collect(Collectors.toList());
          try {
              List<EmployeeNameDTO> employeeDTOs = accountClient.getEmployeeNamesById(employeeIds);
              log.info(Constants.RESOURCES_SIZE, employeeDTOs.size());
              List<EmployeeNameDTO> activeEmployees = employeeDTOs.stream()
                      .filter(EmployeeNameDTO::isActive)
                      .toList();
              Map<String, String> idToNameMap = activeEmployees.stream()
                      .collect(Collectors.toMap(EmployeeNameDTO::getEmployeeId, EmployeeNameDTO::getFullName));
              List<Object> enrichedResources = rawResources.stream()
                      .filter(resource -> idToNameMap.containsKey(resource.getEmployeeId()))
                      .map(resource -> {
                          ResourceView dto = new ResourceView();
                          dto.setEmployeeId(resource.getEmployeeId());
                          dto.setName(idToNameMap.get(resource.getEmployeeId()));
                          dto.setAllocationPercentage(resource.getAllocationPercentage());
                          return dto;
                      })
                      .collect(Collectors.toList());

              contract.setRawProjectResources(enrichedResources);

          } catch (FeignClientException e) {
              log.error(Constants.FEIGN_CLIENT_ERROR, e.getMessage(), e);
              throw new FeignClientException(Constants.SOMETHING_WENT_WRONG);
          }
      }else{
          log.info(Constants.NO_RESOURCE_FOUND, contractId);
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
    if (request.getBillingCurrency() != null) contract.setBillingCurrency(request.getBillingCurrency());
    if(request.getContractType() != null) contract.setContractType(request.getContractType());
    if(request.getBillingType() != null) contract.setBillingType(request.getBillingType());
    if(request.getProjectManagers() != null && !request.getProjectManagers().isEmpty()){
          try{
              List<String> validProjectManagers = projectServiceImpl.validateAndFetchEmployees(request.getProjectManagers());
              contract.setProjectManagers(validProjectManagers);
          } catch (FeignClientException e){
              log.error(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS,e.getMessage(), e);
              throw new FeignClientException(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS);
          }
    }
      if(request.getProjectResources() !=null && !request.getProjectResources().isEmpty()){
          try {
              List<String> employeeIds = request.getProjectResources().stream()
                      .map(ResourceAllocation::getEmployeeId)
                      .collect(Collectors.toList());

              List<String> validatedEmployeeIds = projectServiceImpl.validateAndFetchEmployees(employeeIds);

              List<ResourceAllocation> validProjectResources = request.getProjectResources().stream()
                      .filter(resource -> validatedEmployeeIds.contains(resource.getEmployeeId()))
                      .collect(Collectors.toList());

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
    @Override
    public List<Contract> getAllContractsInOrganization(String organizationId,int pageNumber, int pageSize, String projectId, ProjectStatus status) {
        try {
            Query query = buildContractQuery(organizationId, projectId, status);
            int skip = (pageNumber - 1) * pageSize;
            if (skip < 0) skip = 0;
            query.skip(skip).limit(pageSize);
            query.with(Sort.by(Sort.Direction.DESC, "createdAt"));

            List<Contract> contracts = mongoTemplate.find(query, Contract.class);

            for (Contract contract : contracts) {
                contract.setProjectResources(contract.normalizeProjectResources(contract.getRawProjectResources()));
            }

            return contracts;
        } catch (Exception e) {
            throw new RuntimeException(
                    String.valueOf(BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Error fetching contract details")));
        }
    }
    @Override
    public Long getTotalContractSize(String organizationId, String projectId, ProjectStatus status) {
        Query query = buildContractQuery(organizationId,projectId, status);
        return mongoTemplate.count(query, Contract.class);
    }

    private Query buildContractQuery(String organizationId, String projectId, ProjectStatus status) {
        Query query = new Query();

        if (projectId != null && !projectId.isEmpty()) {
            query.addCriteria(Criteria.where("projectId").is(projectId));
        }

        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        }

        query.addCriteria(Criteria.where("organizationId")
                .is(organizationId));

        return query;
    }
    @Override
    public Contract changeContractStatus(String contractId, ProjectStatus status) {
        String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();


        Contract contract = contractRepository.findByContractIdAndOrganizationId(contractId, organizationId);

        if (contract == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Contract not found for the given project")
            );
        }

        contract.setStatus(status);

        try {
            contract = contractRepository.save(contract);
        } catch (Exception e) {
            log.error("Error updating contract status: {}", e.getMessage(), e);
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_CREATION_ERROR,
                            Constants.SOMETHING_WENT_WRONG)
            );
        }

        return contract;
    }
    @Override
    public List<ContractResponsesDTO> getAllContracts(String organizationId, int pageNumber, int pageSize, String projectid, ProjectStatus status) {

        List<Contract> contracts;
        try {
            contracts = getAllContractsInOrganization(organizationId, pageNumber, pageSize, projectid, status);
        } catch (Exception e) {
            ErrorResponse error = BuildErrorMessage.buildErrorMessage(
                    ErrorType.DB_ERROR,
                    ErrorCode.DATA_FETCH_ERROR,
                    Constants.CONTRACT_NOT_FOUND + organizationId
            );
            throw new InternalServerErrorException(error.getMessage());
        }
        if (contracts == null || contracts.isEmpty()) {
            return Collections.emptyList();
        }

        return contracts.stream().map(contract -> {
            String projectId = contract.getProjectId();

            Project project = projectRepository.findByProjectId(projectId, organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            BuildErrorMessage.buildErrorMessage(
                                    ErrorType.NOT_FOUND,
                                    ErrorCode.RESOURCE_NOT_FOUND,
                                    Constants.PROJECT_NOT_FOUND + projectId
                            )
                    ));

            List<String> pmIds = contract.getProjectManagers();
            List<String> pmNames;
            try {
                pmNames = (pmIds != null && !pmIds.isEmpty())
                        ? accountClient.getEmployeeNamesById(pmIds).stream()
                        .map(EmployeeNameDTO::getFullName)
                        .toList()
                        : Collections.emptyList();
            } catch (Exception e) {
                throw new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.FEIGN_CLIENT_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.FETCH_ERROR_FOR_PROJECT_MANAGERS + pmIds
                        )
                );
            }

            Client client = clientRepository.findByClientIdAndOrganizationId(contract.getClientId(), organizationId);
            String clientName = (client != null) ? client.getClientName() : Constants.CLIENT_NOT_FOUND;

            return ContractResponsesDTO.builder()
                    .contractId(contract.getContractId())
                    .projectId(contract.getProjectId())
                    .contractTitle(contract.getContractTitle())
                    .projectName(project.getName())
                    .clientName(clientName)
                    .projectManagerIds(pmIds)
                    .projectManagerNames(pmNames)
                    .status(contract.getStatus() != null ? contract.getStatus().name() : null)
                    .build();
        }).collect(Collectors.toList());
    }

}
