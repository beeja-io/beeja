package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.Questions;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionsRepository extends MongoRepository<Questions,String> {

}
