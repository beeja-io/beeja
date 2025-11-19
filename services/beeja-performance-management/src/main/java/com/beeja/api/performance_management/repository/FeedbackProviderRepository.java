package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.FeedbackProvider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackProviderRepository extends MongoRepository<FeedbackProvider, String> {
    Optional<FeedbackProvider> findByOrganizationIdAndEmployeeIdAndCycleId(
            String organizationId,
            String employeeId,
            String cycleId
    );

    List<FeedbackProvider> findByOrganizationId(String organizationId);

    List<FeedbackProvider> findByOrganizationIdAndEmployeeId(String organizationId, String employeeId);

    void deleteByCycleIdAndOrganizationId(String cycleId, String organizationId);
}