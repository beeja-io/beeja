package com.beeja.api.accounts.repository;

import com.beeja.api.accounts.model.Organization.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolesRepository extends MongoRepository<Role, String> {
  Role findByName(String name);

  Role findByNameAndOrganizationId(String name, String organizationId);

  Role findByIdAndOrganizationId(String id, String organizationId);

  List<Role> findByOrganizationId(String organizationId);
}
