package com.beeja.api.accounts.repository;

import com.beeja.api.accounts.model.Organization.OrganizationPattern;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationPatternsRepository
    extends MongoRepository<OrganizationPattern, String> {
  OrganizationPattern deleteByOrganizationIdAndPatternTypeAndId(
      String organizationId, String patternType, String patternId);

  List<OrganizationPattern> findByOrganizationIdAndPatternType(
      String organizationId, String patternType);

  OrganizationPattern findByIdAndPatternTypeAndOrganizationId(
      String id, String patternType, String organizationId);

  boolean existsByOrganizationIdAndPatternTypeAndPatternLengthAndPrefix(
      String organizationId, String patternType, int patternLength, String prefix);

  OrganizationPattern findByOrganizationIdAndPatternTypeAndActive(
      String organizationId, String patternType, boolean active);
}
