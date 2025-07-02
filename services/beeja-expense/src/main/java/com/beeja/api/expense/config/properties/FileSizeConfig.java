package com.beeja.api.expense.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.servlet.multipart")
public class FileSizeConfig {
  private String maxFileSize;
  private String maxRequestSize;
}
