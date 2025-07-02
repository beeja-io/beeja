package com.beeja.api.performance_management.config.filters;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "authentication")
public class AuthUrlProperties {
  private String clientId;
  private String tokenUri;
}
