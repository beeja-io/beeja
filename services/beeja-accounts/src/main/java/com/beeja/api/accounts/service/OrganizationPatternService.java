package com.beeja.api.accounts.service;

import com.beeja.api.accounts.model.Organization.OrganizationPattern;
import com.beeja.api.accounts.requests.OrganizationPatternRequest;

import java.util.List;

public interface OrganizationPatternService {
  OrganizationPattern updatePatternStatusByPatternIdAndPatternType(
      String patternId, String patternType);

  OrganizationPattern addPatternByPatternIdAndPatternType(
      OrganizationPatternRequest organizationPatternRequest);

  void deletePatternByPatternIdAndPatternType(String patternId, String patternType);

  List<OrganizationPattern> getPatternsByPatternType(String patternType) throws Exception;

  OrganizationPattern getActivePatternByPatternType(String patternType) throws Exception;
}
