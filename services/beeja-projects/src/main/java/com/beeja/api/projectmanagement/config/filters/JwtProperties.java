package com.beeja.api.projectmanagement.config.filters;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that binds JWT-related properties from the application's configuration (e.g.,
 * application.properties or application.yml) using the prefix {@code jwt}.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  /** The secret key used to sign and verify JWT tokens. */
  private String secret;
}
