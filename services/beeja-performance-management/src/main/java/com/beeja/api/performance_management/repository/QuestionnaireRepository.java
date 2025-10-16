package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.Questionnaire;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireRepository extends MongoRepository<Questionnaire, String> {

    /**
     * Retrieves all questionnaires, sorted by the provided criteria.
     *
     * @param sort the sort configuration
     * @return a sorted list of all questionnaires
     */
    List<Questionnaire> findAll(Sort sort);
}