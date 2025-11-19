package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.FeedbackResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for CRUD operations on FeedbackResponse documents.
 */
@Repository
public interface FeedbackResponseRepository extends MongoRepository<FeedbackResponse, String> {

    /** Retrieves all feedback responses for an organization. */
    List<FeedbackResponse> findByOrganizationId(String organizationId);

    /** Retrieves all feedback responses for an employee within an organization. */
    List<FeedbackResponse> findByEmployeeIdAndOrganizationId(String employeeId, String organizationId);

    /** Retrieves feedback responses by form ID and organization ID. */
    List<FeedbackResponse> findByFormIdAndOrganizationId(String formId, String organizationId);

    /** Retrieves feedback responses by cycle ID and organization ID. */
    List<FeedbackResponse> findByCycleIdAndOrganizationId(String cycleId, String organizationId);

    /** Retrieves feedback responses by employee ID, cycle ID, and organization ID. */
    List<FeedbackResponse> findByEmployeeIdAndCycleIdAndOrganizationId(String employeeId, String cycleId, String organizationId);

    /** Deletes feedback responses for a given cycle ID and organization ID. **/
    void deleteByCycleIdAndOrganizationId(String cycleId, String organizationId);
}