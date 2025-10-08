package com.beeja.api.performance_management.repository;

import com.beeja.api.performance_management.model.Questionnaire;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD and custom query operations on
 * {@link Questionnaire} documents stored in MongoDB.
 * <p>
 * Extends {@link MongoRepository} to provide basic persistence methods and
 * supports additional queries by department and sorting options.
 * </p>
 */
@Repository
public interface QuestionnaireRepository extends MongoRepository<Questionnaire, String> {

    /**
     * Finds all questionnaires associated with a specific department.
     *
     * @param department the department name
     * @return a list of questionnaires belonging to the specified department
     */
    List<Questionnaire> findByDepartment(String department);

    /**
     * Finds all questionnaires associated with any of the given departments.
     *
     * @param departments a list of department names
     * @return a list of questionnaires for the specified departments
     */
    List<Questionnaire> findByDepartmentIn(List<String> departments);

    /**
     * Finds all questionnaires for a given department, sorted by the provided criteria.
     *
     * @param department the department name
     * @param sort       the sort configuration
     * @return a sorted list of questionnaires for the given department
     */
    List<Questionnaire> findByDepartment(String department, Sort sort);

    /**
     * Retrieves all questionnaires, sorted by the provided criteria.
     *
     * @param sort the sort configuration
     * @return a sorted list of all questionnaires
     */
    List<Questionnaire> findAll(Sort sort);
}