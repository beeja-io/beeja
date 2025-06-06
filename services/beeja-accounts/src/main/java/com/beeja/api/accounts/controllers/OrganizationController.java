package com.beeja.api.accounts.controllers;

import com.beeja.api.accounts.annotations.HasPermission;
import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.response.OrganizationResponse;
import com.beeja.api.accounts.service.OrganizationService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/organizations")
@Slf4j
public class OrganizationController {

  @Autowired OrganizationService organizationService;

  @GetMapping("/{organizationId}/employees")
  @HasPermission((PermissionConstants.READ_ORGANIZATIONS))
  public ResponseEntity<?> getAllEmployeesByOrganizationId(@PathVariable String organizationId)
      throws Exception {
    return ResponseEntity.ok(organizationService.getAllUsersByOrganizationId(organizationId));
  }

  @GetMapping("/{organizationId}")
  @HasPermission(PermissionConstants.READ_ORGANIZATIONS)
  public ResponseEntity<OrganizationResponse> getOrganizationById(
      @PathVariable String organizationId) throws Exception {
    return ResponseEntity.status(HttpStatus.OK)
        .body(organizationService.getOrganizationById(organizationId));
  }

  @PatchMapping("/{organizationId}")
  @HasPermission(PermissionConstants.UPDATE_ORGANIZATIONS)
  public ResponseEntity<?> updateOrganization(
      @PathVariable String organizationId,
      @RequestParam(name = "organizationFields", required = false) String fields,
      @RequestParam(name = "logo", required = false) MultipartFile file)
      throws Exception {

    Organization updatedOrganization =
        organizationService.updateOrganization(organizationId, fields, file);
    return ResponseEntity.ok(updatedOrganization);
  }

  @GetMapping("/logo")
  @HasPermission({
    PermissionConstants.READ_EMPLOYEE,
  })
  public ResponseEntity<?> downloadFile() throws Exception {
    ByteArrayResource resource = organizationService.downloadOrganizationFile();
    HttpHeaders headers = new HttpHeaders();
    headers.add(
        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .headers(headers)
        .body(resource);
  }

  @PutMapping("/update-values")
  @HasPermission(PermissionConstants.UPDATE_ORGANIZATIONS)
  public ResponseEntity<OrgDefaults> updateOrganizationValues(@RequestBody OrgDefaults orgDefaults)
      throws Exception {
    return ResponseEntity.ok(organizationService.updateOrganizationValues(orgDefaults));
  }

  @GetMapping("/values/{key}")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<OrgDefaults> getOrganizationValuesByKey(@PathVariable String key)
      throws Exception {
    OrgDefaults orgDefaults = organizationService.getOrganizationValuesByKey(key);
    if (orgDefaults == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(organizationService.getOrganizationValuesByKey(key));
  }

  @GetMapping("/values")
  @HasPermission(PermissionConstants.READ_EMPLOYEE)
  public ResponseEntity<List<OrgDefaults>> getOrganizationValues(@RequestParam List<String> keys)
      throws Exception {
    return ResponseEntity.ok(organizationService.getOrganizationValues(keys));
  }

  @PostMapping("/generate-defaults")
  @HasPermission(PermissionConstants.UPDATE_ORGANIZATIONS)
  public ResponseEntity<?> generateOrganizationDefaults() throws Exception {
    organizationService.generateOrganizationDefaults();
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
