package com.beeja.api.accounts.model.Organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "roles")
public class Role {
  @Id private String id;
  private String name;
  private String description;
  private Set<String> permissions;
  private String organizationId;
}
