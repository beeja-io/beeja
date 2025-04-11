package com.beeja.api.accounts.controllers;

import com.beeja.api.accounts.annotations.HasPermission;
import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.model.Organization.OrganizationPattern;
import com.beeja.api.accounts.requests.OrganizationPatternRequest;
import com.beeja.api.accounts.service.OrganizationPatternService;
import com.beeja.api.accounts.utils.ValidationUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/organization/patterns")
public class OrganizationPatternController {

  @Autowired private OrganizationPatternService organizationPatternService;

  @PutMapping("/update-status")
  @HasPermission(PermissionConstants.UPDATE_ORGANIZATIONS)
  public ResponseEntity<OrganizationPattern> updatePatternStatus(
      @RequestParam String patternId, @RequestParam String patternType) {
    OrganizationPattern updatedPattern =
        organizationPatternService.updatePatternStatusByPatternIdAndPatternType(
            patternId, patternType);
    return ResponseEntity.ok(updatedPattern);
  }

  @PostMapping
  @HasPermission(PermissionConstants.UPDATE_ORGANIZATIONS)
  public ResponseEntity<OrganizationPattern> addPattern(
      @Valid @RequestBody OrganizationPatternRequest organizationPatternRequest,
      BindingResult bindingResult)
      throws Exception {
    Map<String, Object> errors = ValidationUtil.handleValidation(bindingResult);
    if (errors != null) {
      throw new Exception(String.valueOf(errors));
    }
    OrganizationPattern createdPattern =
        organizationPatternService.addPatternByPatternIdAndPatternType(organizationPatternRequest);
    return ResponseEntity.ok(createdPattern);
  }

  @DeleteMapping
  @HasPermission(PermissionConstants.UPDATE_ORGANIZATIONS)
  public ResponseEntity<Void> deletePattern(
      @RequestParam String patternId, @RequestParam String patternType) {
    organizationPatternService.deletePatternByPatternIdAndPatternType(patternId, patternType);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<List<OrganizationPattern>> getPatternsByType(
      @RequestParam String patternType) throws Exception {
    List<OrganizationPattern> patterns =
        organizationPatternService.getPatternsByPatternType(patternType);
    return ResponseEntity.ok(patterns);
  }

  @GetMapping("/active")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<OrganizationPattern> getActivePatternByType(
      @RequestParam String patternType) throws Exception {
    OrganizationPattern pattern =
        organizationPatternService.getActivePatternByPatternType(patternType);
    return ResponseEntity.ok(pattern);
  }
}
