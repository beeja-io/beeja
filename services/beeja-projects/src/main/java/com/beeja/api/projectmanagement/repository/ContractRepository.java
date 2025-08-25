package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Contract;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for performing CRUD operations on {@link Contract} documents in MongoDB. */
@Repository
public interface ContractRepository extends MongoRepository<Contract, String> {

  /**
   * Retrieves a contract by contract ID and organization ID.
   *
   * @param contractId the unique ID of the contract
   * @param organizationId the ID of the organization the contract belongs to
   * @return the matching {@link Contract}, or {@code null} if not found
   */
  Contract findByContractIdAndOrganizationId(String contractId, String organizationId);

  /**
   * Retrieves all contracts associated with a given project and organization.
   *
   * @param projectId the ID of the project
   * @param organizationId the ID of the organization
   * @return a list of {@link Contract} objects linked to the specified project and organization
   */
  List<Contract> findByProjectIdAndOrganizationId(String projectId, String organizationId);

  List<Contract> findAllContractsByOrganizationId(String organizationId);

    List<Contract> findByClientIdAndOrganizationId(String clientId, String organizationId);



}
