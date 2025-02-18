package com.beeja.api.projectmanagement.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.beeja.api.projectmanagement.model.Client;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends MongoRepository<Client,String> {
    Client findByEmailAndOrganizationId(String email,String organizationId);

    Client findByClientIdAndOrganizationId(String clientId, String organizationId);


    Client findByIdAndOrganizationId(String id, String organizationId);

    long countByOrganizationId(String organizationId);

    List<Client> findAllByOrganizationIdOrderByCreatedAtDesc(String organizationId);
}


