package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.service.ContractService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link ContractService} interface.
 *
 * This service handles the business logic for managing contracts related to projects,
 * including creation, retrieval, and updates of contracts.
 */
@Slf4j
@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Creates a new {@link Contract} for a given {@link Project} and {@link ContractRequest}.
     *
     * @param request the {@link ContractRequest} containing details to create the {@link Contract}
     * @return the newly created {@link Contract}
     * @throws ResourceNotFoundException if no {@link Project} exists for the provided {@link ContractRequest#getProjectId()}
     */
    @Override
    public Contract createContract(ContractRequest request) {
        Project project = projectRepository.findByProjectIdAndClientIdAndOrganizationId(
                request.getProjectId(), request.getClientId(), UserContext.getLoggedInUserOrganization().get("id").toString());

        if (project == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Project not found with given projectId"
                    )
            );
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

        try {
            return contractRepository.save(contract);
        } catch (Exception e) {
            log.error("Failed to create contract: {}", e.getMessage());
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_CREATION_ERROR,
                            "Failed to save contract"
                    )
            );
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
        Contract contract = contractRepository.findByContractIdAndOrganizationId(
                contractId, UserContext.getLoggedInUserOrganization().get("id").toString());

        if (contract == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Contract not found with given contractId"
                    )
            );
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
        List<Contract> contracts = contractRepository.findByProjectIdAndOrganizationId(
                projectId, UserContext.getLoggedInUserOrganization().get("id").toString());

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

        try {
            return contractRepository.save(contract);
        } catch (Exception e) {
            log.error("Failed to update contract: {}", e.getMessage());
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_CREATION_ERROR,
                            "Failed to update contract"
                    )
            );
        }
    }
}
