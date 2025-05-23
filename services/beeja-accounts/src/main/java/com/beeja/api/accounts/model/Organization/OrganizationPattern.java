package com.beeja.api.accounts.model.Organization;

import com.beeja.api.accounts.enums.PatternType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "organization-patterns")
public class OrganizationPattern {
  @Id private String id;
  private PatternType patternType;
  private String organizationId;
  private int patternLength;
  private String prefix;
  private int initialSequence;
  private String examplePattern;
  private boolean active;


  public void setPatternId(String patternId) {
  }
}
