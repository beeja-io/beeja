package com.beeja.api.filemanagement.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"com.beeja.api.filemanagement.repository"})
@EnableMongoAuditing
public class MongoDbConfig extends AbstractMongoClientConfiguration {

  @Autowired private Environment environment;

  @Bean
  MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }

  @Override
  protected String getDatabaseName() {
    return environment.getProperty("spring.data.mongodb.database");
  }

  @Override
  public MongoClient mongoClient() {
    final ConnectionString connectionString =
        new ConnectionString(environment.getProperty("spring.data.mongodb.uri"));
    final MongoClientSettings mongoClientSettings =
        MongoClientSettings.builder().applyConnectionString(connectionString).build();
    return MongoClients.create(mongoClientSettings);
  }
}
