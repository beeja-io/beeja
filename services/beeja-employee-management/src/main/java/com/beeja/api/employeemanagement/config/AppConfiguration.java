package com.beeja.api.employeemanagement.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class AppConfiguration {

  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
    messageSource.setBasenames("classpath:/messages/messages", "classpath:/messages/messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }
}
