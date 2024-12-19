package com.beeja.api.filemanagement.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "gcp")
public class GCSProperties {
  private Bucket bucket;

  @Getter
  @Setter
  public static class Bucket {
    private String name;
  }
}
