package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.ReviewForm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewFormRepository extends MongoRepository<ReviewForm,String> {

}
