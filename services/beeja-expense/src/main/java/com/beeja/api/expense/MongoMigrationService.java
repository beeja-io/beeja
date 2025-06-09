package com.beeja.api.expense;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MongoMigrationService {

  @Autowired private MongoTemplate mongoTemplate;

  @PostConstruct
  public void renameAndRemoveDuplicates() {
    /*
       Step 1: Rename paymentDate to paymentSettled
    */
    Query query = new Query(Criteria.where("paymentDate").exists(true));
    Update update = new Update().rename("paymentDate", "paymentSettled");
    mongoTemplate.updateMulti(query, update, "expenses");

    /*
       Step 2: Remve duplicate "paymentSettled" keys if they exist
    */
    List<Document> documents = mongoTemplate.find(new Query(), Document.class, "expenses");
    for (Document doc : documents) {
      if (doc.containsKey("paymentSettled")) {
        Object value = doc.get("paymentSettled");

        doc.remove("paymentSettled");
        doc.append("paymentSettled", value);

        mongoTemplate.save(doc, "expenses");
      }
    }

    log.info(
        "Migration completed: Renamed 'paymentDate' to 'paymentSettled' and removed duplicates.");
  }
}
