package com.beeja.api.accounts.config;

import com.beeja.api.accounts.utils.MongoIndexes;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
public class MongoIndexConfig {
  private final MongoTemplate mongoTemplate;

  public MongoIndexConfig(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @PostConstruct
  public void ensureIndexes() {
    mongoTemplate
        .indexOps("organization-values")
        .ensureIndex(
            new Index()
                .on("key", Sort.Direction.ASC)
                .on("organizationId", Sort.Direction.ASC)
                .unique()
                .named(MongoIndexes.UNIQUE_DEFAULT_TYPE_IS_REQUIRED));
  }
}
