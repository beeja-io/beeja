package com.beeja.api.performance_management.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file-format")
public class AllowedContentTypes {
  private String[] allowedTypes;
}
