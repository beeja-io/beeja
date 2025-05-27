package com.beeja.api.accounts.repository;

import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.response.OrganizationResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends MongoRepository<Organization, String> {

  OrganizationResponse findByEmailDomain(String emailDomain);

  @Query(value = "{ 'id' : ?0 }")
  OrganizationResponse findByOrganizationId(String id);

  List<Organization> findByIsBirthDayNotificationEnabled(boolean enabled);
}
