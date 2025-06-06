package com.beeja.api.projectmanagement.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
public class LogoValidator {

  private List<String> allowedTypes;

  public List<String> getAllowedTypes() {
    return allowedTypes;
  }

  public void setAllowedTypes(List<String> allowedTypes) {
    this.allowedTypes = allowedTypes;
  }
}
