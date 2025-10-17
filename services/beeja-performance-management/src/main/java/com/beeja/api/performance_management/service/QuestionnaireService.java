
package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.exceptions.InvalidOperationException;
import com.beeja.api.performance_management.model.Question;
import com.beeja.api.performance_management.model.Questionnaire;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Service interface for managing {@link Questionnaire} business logic.
 * Defines operations for creating, retrieving, updating, and deleting questionnaires.
 */
public interface QuestionnaireService {

    /**
     * Creates a new questionnaire and saves it to the database.
     *
     * @param questionnaire the questionnaire object to be created
     * @return the saved {@link Questionnaire} with generated ID and timestamps
     */
    Questionnaire createQuestionnaire(Questionnaire questionnaire);

    /**
     * Retrieves all questionnaires from the database.
     *
     * @return a list of all {@link Questionnaire} entries
     */
    List<Questionnaire> getAllQuestionnaires();

    /**
     * Retrieves questionnaires assigned to a specific department.
     *
     * @param department the department name to filter by
     * @return a list of {@link Questionnaire} objects for the given department
     */
    List<Questionnaire> getQuestionnairesByDepartment(String department);

    /**
     * Retrieves a questionnaire by its unique identifier.
     *
     * @param id the ID of the questionnaire
     * @return the matching {@link Questionnaire}, or throws an exception if not found
     */
    Questionnaire getQuestionnaireById(String id);

    /**
     * Updates an existing questionnaire with new information.
     *
     * @param id the ID of the questionnaire to update
     * @param questionnaire the updated questionnaire object
     * @return the updated {@link Questionnaire} object
     */
    Questionnaire updateQuestionnaire(String id, Questionnaire questionnaire);

    /**
     * Updates the list of questions for the specified questionnaire.
     *
     * @param id the ID of the questionnaire to update
     * @param updatedQuestions the new list of {@link Question} objects to replace existing questions
     * @return the updated {@link Questionnaire} object
     * @throws InvalidOperationException if the questionnaire does not exist or the questions list is empty
     */
    Questionnaire updateQuestions(String id, @Valid List<Question> updatedQuestions);

    /**
     * Deletes a questionnaire by its ID.
     *
     * @param id the ID of the questionnaire to delete
     */
    void deleteQuestionnaire(String id);
}
