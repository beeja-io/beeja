package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.FinalRating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CRUD operations on FinalRating documents.
 *
 * <p>
 * All queries are explicitly scoped by organizationId to ensure strict data isolation
 * across different tenants in a multi-organization setup.
 * </p>
 */
@Repository
public interface FinalRatingRepository extends MongoRepository<FinalRating, String> {

    /**
     * Retrieves all final ratings for an employee in a specific cycle and organization.
     *
     * @param employeeId Employee ID
     * @param cycleId    Evaluation cycle ID
     * @param organizationId Organization ID
     * @return List of matching FinalRating objects
     */
    List<FinalRating> findByEmployeeIdAndCycleIdAndOrganizationId(
            String employeeId, String cycleId, String organizationId);

    /**
     * Retrieves a specific final rating by ID and organization.
     * This method ensures safe access within the correct tenant context.
     *
     * @param id FinalRating ID
     * @param organizationId Organization ID
     * @return Optional containing the FinalRating if found
     */
    Optional<FinalRating> findByIdAndOrganizationId(String id, String organizationId);
}
