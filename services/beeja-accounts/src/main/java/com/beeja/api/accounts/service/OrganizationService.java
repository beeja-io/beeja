package com.beeja.api.accounts.service;

import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.response.OrganizationResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrganizationService {
  List<User> getAllUsersByOrganizationId(String organizationId) throws Exception;

  OrganizationResponse getOrganizationById(String id) throws Exception;

  Organization updateOrganization(
      String organizationId, String organizationProfileUpdate, MultipartFile file) throws Exception;

  ByteArrayResource downloadOrganizationFile() throws Exception;

  OrgDefaults updateOrganizationValues(OrgDefaults orgDefaults) throws Exception;

  OrgDefaults getOrganizationValuesByKey(String key) throws Exception;

  List<OrgDefaults> getOrganizationValues(List<String> keys) throws Exception;

  void generateOrganizationDefaults() throws Exception;

  List<String> getOrganizationsWithBirthdayNotificationsEnabled() throws Exception;

  String getBirthdayWebhookUrl(String orgId) throws Exception;

  void updateBirthdayPreference(String orgId, boolean enabled, String webhookUrl) throws Exception;
}
