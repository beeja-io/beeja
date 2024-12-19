package com.beeja.api.accounts.repository;

import com.beeja.api.accounts.model.featureFlags.FeatureToggle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureToggleRepository extends MongoRepository<FeatureToggle, String> {
  FeatureToggle findByOrganizationId(String organizationId);
}
