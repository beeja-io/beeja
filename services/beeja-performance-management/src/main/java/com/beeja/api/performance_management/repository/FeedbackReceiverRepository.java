package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.FeedbackReceivers;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackReceiverRepository extends MongoRepository<FeedbackReceivers, String> {

    List<FeedbackReceivers> findByOrganizationIdAndCycleIdAndQuestionnaireId(
            String organizationId, String cycleId, String questionnaireId);

    List<FeedbackReceivers> findByEmployeeIdAndOrganizationId(String employeeId, String string);

    void deleteByCycleIdAndOrganizationId(String cycleId, String organizationId);
}
