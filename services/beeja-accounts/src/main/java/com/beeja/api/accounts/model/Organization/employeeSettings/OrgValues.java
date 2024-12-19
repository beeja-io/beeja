package com.beeja.api.accounts.model.Organization.employeeSettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgValues {

  @Field("name")
  private String value;

  private String description;
}
