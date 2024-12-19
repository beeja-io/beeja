package com.beeja.api.accounts.controllers;

import com.beeja.api.accounts.annotations.HasPermission;
import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.model.featureFlags.FeatureToggle;
import com.beeja.api.accounts.service.FeatureToggleService;
import com.beeja.api.accounts.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/features")
public class FeatureToggleController {

  @Autowired FeatureToggleService featureToggleService;

  @GetMapping
  @HasPermission({PermissionConstants.READ_EMPLOYEE, PermissionConstants.READ_ALL_FEATURE_TOGGLES})
  public ResponseEntity<FeatureToggle> getFeatureToggleByOrganizationId(
      @RequestParam(required = false) String organizationId) {
    if (organizationId != null
        && UserContext.getLoggedInUserPermissions()
            .contains(PermissionConstants.READ_ALL_FEATURE_TOGGLES)) {
      return ResponseEntity.ok(
          featureToggleService.getFeatureToggleByOrganizationId(organizationId));
    } else {
      return ResponseEntity.ok(
          featureToggleService.getFeatureToggleByOrganizationId(
              UserContext.getLoggedInUserOrganization().getId()));
    }
  }

  @PutMapping("/{organizationId}")
  @HasPermission({PermissionConstants.UPDATE_ALL_FEATURE_TOGGLES})
  public ResponseEntity<FeatureToggle> updateFeatureToggleByOrganizationId(
      @PathVariable String organizationId, @RequestBody FeatureToggle featureToggle)
      throws Exception {
    return ResponseEntity.ok(
        featureToggleService.updateFeatureToggleByOrganizationId(organizationId, featureToggle));
  }
}
