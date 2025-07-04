package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.request.ContractRequest;
import java.util.List;

/** Service interface for managing {@link Contract} entities. */
public interface ContractService {

  /**
   * Creates a new contract based on the provided contract request.
   *
   * @param request the request object containing the details of the contract to be created
   * @return the created {@link Contract} object
   */
  Contract createContract(ContractRequest request);

  /**
   * Retrieves a contract by its unique identifier.
   *
   * @param contractId the unique identifier of the contract
   * @return the {@link Contract} object corresponding to the given contract ID
   */
  Contract getContractById(String contractId);

  /**
   * Retrieves a list of contracts associated with a given project.
   *
   * @param projectId the unique identifier of the project
   * @return a list of {@link Contract} objects associated with the given project ID
   */
  List<Contract> getContractsByProjectId(String projectId);

  /**
   * Updates an existing contract based on the provided contract request.
   *
   * @param contractId the unique identifier of the contract to be updated
   * @param request the request object containing the updated contract details
   * @return the updated {@link Contract} object
   */
  Contract updateContract(String contractId, ContractRequest request);

  List<Contract> getAllContracts(int pageNumber, int pageSize, String projectId, ProjectStatus status);
  Long getTotalContractSize(String projectId, ProjectStatus status);
}
