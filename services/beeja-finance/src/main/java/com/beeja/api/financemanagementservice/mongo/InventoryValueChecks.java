package com.beeja.api.financemanagementservice.mongo;

import com.beeja.api.financemanagementservice.modals.Inventory;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Configuration class responsible for performing startup checks and data fixes on the Inventory
 * collection.
 *
 * <p>Specifically, this class ensures that every Inventory document has a non-null {@code
 * created_at} field. If any document is missing the {@code created_at} timestamp, it will be
 * automatically updated with the current system date.
 *
 * <p>This check runs only once during application startup.
 *
 * <p>Note: This uses MongoTemplate to avoid deserializing Inventory objects, thereby bypassing
 * issues with default field initialization.
 */
@Configuration
@Slf4j
public class InventoryValueChecks {

  private final MongoTemplate mongoTemplate;

  public InventoryValueChecks(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @PostConstruct
  public void assignCreatedDateIfMissing() {
    try {
      Query query = new Query(Criteria.where("created_at").is(null));
      List<Inventory> inventoriesWithoutCreatedDate = mongoTemplate.find(query, Inventory.class);

      for (Inventory inventory : inventoriesWithoutCreatedDate) {
        Query updateQuery = new Query(Criteria.where("_id").is(inventory.getId()));
        Update update = new Update().set("created_at", new Date());
        mongoTemplate.updateFirst(updateQuery, update, Inventory.class);
      }

      if (!inventoriesWithoutCreatedDate.isEmpty()) {
        log.info(
            "Updated created_at for {} inventory items.", inventoriesWithoutCreatedDate.size());
      }
    } catch (Exception e) {
      log.error("Error while updating created_at: {}", e.getMessage());
    }
  }
}
