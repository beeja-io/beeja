package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Contract;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends MongoRepository<Contract, String> {
    Contract findByContractIdAndOrganizationId(String contractId, String organizationId);
    List<Contract> findByProjectIdAndOrganizationId(String projectId, String organizationId);
}
