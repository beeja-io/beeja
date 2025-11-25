package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.Questionnaire;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireRepository extends MongoRepository<Questionnaire, String> {

    /**
     * Retrieves all questionnaires for the specified organization, sorted by the provided criteria.
     *
     * @param organizationId the organization ID to filter by
     * @param sort the sort configuration
     * @return a sorted list of questionnaires belonging to the organization
     */
    List<Questionnaire> findByOrganizationId(String organizationId, Sort sort);

    List<Questionnaire> findByOrganizationIdAndCycleId(String organizationId, String cycleId, Sort sort);

}
