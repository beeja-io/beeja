package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.SelfEvaluation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for CRUD operations on SelfEvaluation documents.
 */
@Repository
public interface SelfEvaluationRepository extends MongoRepository<SelfEvaluation, String> {

    /** Retrieves all self-evaluations for an employee within a specific organization. */
    List<SelfEvaluation> findByEmployeeIdAndOrganizationId(String employeeId, String organizationId);

    /** Checks if a self-evaluation has been submitted by an employee in a specific organization. */
    boolean existsByEmployeeIdAndOrganizationIdAndSubmittedTrue(String employeeId, String organizationId);
}
