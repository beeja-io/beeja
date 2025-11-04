package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.FinalRating;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * Repository interface for CRUD operations on FinalRating documents.
 */
public interface FinalRatingRepository extends MongoRepository<FinalRating, String> {

    /** Retrieves all final ratings for an employee in a specific cycle and organization. */
    List<FinalRating> findByEmployeeIdAndCycleIdAndOrganizationId(String employeeId, String cycleId, String organizationId);
}
