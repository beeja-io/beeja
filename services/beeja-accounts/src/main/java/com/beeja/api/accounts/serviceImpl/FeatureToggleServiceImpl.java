package com.beeja.api.accounts.serviceImpl;

import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.featureFlags.FeatureToggle;
import com.beeja.api.accounts.repository.FeatureToggleRepository;
import com.beeja.api.accounts.service.FeatureToggleService;
import com.beeja.api.accounts.utils.BuildErrorMessage;
import com.beeja.api.accounts.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleServiceImpl implements FeatureToggleService {

  @Autowired FeatureToggleRepository featureToggleRepository;

  @Override
  public FeatureToggle getFeatureToggleByOrganizationId(String organizationId) {
    FeatureToggle featureToggle = featureToggleRepository.findByOrganizationId(organizationId);
    if (featureToggle != null) {
      return featureToggleRepository.findByOrganizationId(organizationId);
    } else {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.FEATURES_ARE_NOT_FOUND,
              Constants.RESOURCE_NOT_FOUND));
    }
  }

  @Override
  public FeatureToggle updateFeatureToggleByOrganizationId(
      String organizationId, FeatureToggle featureToggle) throws Exception {

    FeatureToggle optionalFeatureToggle =
        featureToggleRepository.findByOrganizationId(organizationId);
    if (optionalFeatureToggle == null) {
      throw new ResourceNotFoundException(Constants.RESOURCE_NOT_FOUND);
    }
    optionalFeatureToggle.setFeatureToggles(featureToggle.getFeatureToggles());
    try {
      return featureToggleRepository.save(optionalFeatureToggle);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.RESOURCE_CREATING_ERROR,
              Constants.RESOURCE_UPDATING_ERROR_FEATURE_TOGGLE));
    }
  }
}
