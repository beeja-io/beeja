package com.beeja.api.apigateway.config.security.properties;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "skip-gateway-filter-routes")
public class SkipGatewayFilterRoutesProperty {
  private String routes;

  public List<String> getRoutesAsList() {
    return Arrays.asList(routes.split(","));
  }
}
