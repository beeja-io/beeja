package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Contract;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ContractRepository extends MongoRepository<Contract, String> {
    Contract findByIdAndOrganizationId(String projectId, String organizationId);

    Contract findTopByOrderByContractIdDesc();
}
