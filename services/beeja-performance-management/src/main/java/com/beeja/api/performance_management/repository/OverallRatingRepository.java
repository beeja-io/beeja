package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.OverallRating;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

public interface OverallRatingRepository extends MongoRepository<OverallRating, String> {

    Optional<Object> findByEmployeeIdAndOrganizationId(String employeeId, String id);

    void deleteByEmployeeIdAndOrganizationId(String employeeId, String id);
}
