package com.beeja.api.accounts.mongo;

import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.employeeSettings.OrgValues;
import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;

@Configuration
@Slf4j
public class OrgDefaultsChecks {
  private final MongoTemplate mongoTemplate;

  public OrgDefaultsChecks(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @PostConstruct
  public void initRemoveDuplicatesORGDefaults() {
    removeExistingDuplicates();
  }

  @Async
  public void removeExistingDuplicates() {
    try {

      List<OrgDefaults> orgDefaultsList = mongoTemplate.findAll(OrgDefaults.class);
      for (OrgDefaults orgDefaults : orgDefaultsList) {

        Set<OrgValues> orginalValues = orgDefaults.getValues();
        Set<OrgValues> updatedValues = removeDuplicates(orginalValues);

        if (!updatedValues.equals(orginalValues)) {
          orgDefaults.setValues(updatedValues);
          mongoTemplate.save(orgDefaults);
        }
      }
    } catch (Exception e) {
      log.error("Error removing duplicate values inside OrgDefaults", e);
    }
  }

  private Set<OrgValues> removeDuplicates(Set<OrgValues> orginalValues) {
    Set<String> seenValues = new HashSet<>();
    Set<OrgValues> filteredValues = new HashSet<>();
    for (OrgValues value : orginalValues) {
      if (value.getValue() != null && seenValues.add(value.getValue().toLowerCase())) {
        filteredValues.add(value);
      }
    }
    return filteredValues;
  }
}
