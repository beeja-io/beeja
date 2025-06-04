package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.ReviewCycle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewCycleRepository extends MongoRepository<ReviewCycle,String> {
    List<ReviewCycle> findByOrganizationId(String organizationId);
}
