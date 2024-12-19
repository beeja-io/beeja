package com.beeja.api.accounts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

  @Bean
  public MongoTransactionManager transactionManager(MongoTemplate mongoTemplate) {
    return new MongoTransactionManager(mongoTemplate.getMongoDatabaseFactory());
  }
}
