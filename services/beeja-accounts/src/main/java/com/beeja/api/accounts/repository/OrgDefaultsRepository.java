package com.beeja.api.accounts.repository;

import com.beeja.api.accounts.model.Organization.OrgDefaults;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgDefaultsRepository extends MongoRepository<OrgDefaults, String> {
  OrgDefaults findByOrganizationIdAndKey(String organizationId, String key);
}
