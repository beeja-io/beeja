package com.beeja.api.accounts.service;

import com.beeja.api.accounts.model.featureFlags.FeatureToggle;

public interface FeatureToggleService {
  FeatureToggle getFeatureToggleByOrganizationId(String organizationId);

  FeatureToggle updateFeatureToggleByOrganizationId(
      String organizationId, FeatureToggle featureToggle) throws Exception;
}
