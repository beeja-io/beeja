package com.beeja.api.accounts.repository;

import com.beeja.api.accounts.model.Organization.OrgDefaults;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgDefaultsRepository extends MongoRepository<OrgDefaults, String> {
  OrgDefaults findByOrganizationIdAndKey(String organizationId, String key);

  List<OrgDefaults> findByOrganizationId(String organizationId);

  List<OrgDefaults> findByOrganizationIdAndKeyIn(String organizationId, List<String> keys);
}
