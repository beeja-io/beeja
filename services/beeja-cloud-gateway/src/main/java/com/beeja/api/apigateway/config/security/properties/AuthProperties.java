package com.beeja.api.apigateway.config.security.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "auth.cors.allowed-origins")
public class AuthProperties {
  private String frontEndUrl;
  private List<String> urls;
}
