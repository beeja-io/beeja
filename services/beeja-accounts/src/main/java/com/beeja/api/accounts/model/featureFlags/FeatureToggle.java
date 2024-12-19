package com.beeja.api.accounts.model.featureFlags;

import com.beeja.api.accounts.enums.FeatureToggles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class FeatureToggle {
  @Id private String id;

  @Indexed(unique = true)
  private String organizationId;

  @Builder.Default
  private Set<FeatureToggles> featureToggles =
      new HashSet<>(
          Arrays.asList(FeatureToggles.EMPLOYEE_MANAGEMENT, FeatureToggles.DOCUMENT_MANAGEMENT));
}
