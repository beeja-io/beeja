package com.beeja.api.accounts.model.Organization;

import com.beeja.api.accounts.model.Organization.employeeSettings.OrgValues;
import java.util.Set;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "organization-values")
public class OrgDefaults {
  @Id private String id;
  private String organizationId;
  private String key;
  private Set<OrgValues> values;
}
