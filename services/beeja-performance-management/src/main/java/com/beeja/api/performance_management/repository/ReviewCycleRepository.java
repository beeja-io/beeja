package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.ReviewCycle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewCycleRepository extends MongoRepository<ReviewCycle, String> {

    List<ReviewCycle> findByManagerIdsContaining(String managerId);

    List<ReviewCycle> findByStatus(String status);

    Optional<ReviewCycle> findByCycleId(String cycleId);

    @Query("{'employeeIds': ?0}")
    List<ReviewCycle> findByEmployeeId(String employeeId);
}
