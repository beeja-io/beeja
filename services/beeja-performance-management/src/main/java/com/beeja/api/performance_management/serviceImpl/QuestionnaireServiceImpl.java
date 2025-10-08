package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.Department;
import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.exceptions.BadRequestException;
import com.beeja.api.performance_management.exceptions.DuplicateDataException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.Questionnaire;
import com.beeja.api.performance_management.repository.QuestionnaireRepository;
import com.beeja.api.performance_management.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
   private QuestionnaireRepository questionnaireRepository;

    /**
     * Creates a new {@link Questionnaire} for a specific {@link Department}.
     *
     * <p>Ensures that a questionnaire does not already exist for the given department before saving.
     *
     * @param questionnaire the {@link Questionnaire} entity to be created
     * @return the created {@link Questionnaire}
     * @throws BadRequestException if the department is null
     * @throws DuplicateDataException if a questionnaire already exists for the specified department
     */
    @Override
    public Questionnaire createQuestionnaire(Questionnaire questionnaire) {
        if (questionnaire.getDepartment() == null) {
            throw new BadRequestException(
                    ErrorType.VALIDATION_ERROR + "," +
                            ErrorCode.FIELD_VALIDATION_MISSING + "," +
                            "Department is required"
            );
        }

        List<Questionnaire> existing = questionnaireRepository.findByDepartment(questionnaire.getDepartment().name(), Sort.by(Sort.Direction.ASC, "department"));
        if (!existing.isEmpty()) {
            throw new DuplicateDataException(
                    ErrorType.RESOURCE_EXISTS_ERROR + "," +
                            ErrorCode.RESOURCE_EXISTS_ERROR + "," +
                            "Questionnaire already exists for department: " + questionnaire.getDepartment()
            );
        }

        return questionnaireRepository.save(questionnaire);
    }

    /**
     * Retrieves all {@link Questionnaire} entries sorted by department in ascending order.
     *
     * @return a list of all {@link Questionnaire} objects
     */
    @Override
    public List<Questionnaire> getAllQuestionnaires() {
        return questionnaireRepository.findAll(Sort.by(Sort.Direction.ASC, "department"));
    }

    /**
     * Retrieves all {@link Questionnaire} entries for a specified {@link Department}.
     *
     * @param department the name of the department to filter questionnaires
     * @return a list of {@link Questionnaire} objects for the given department
     * @throws BadRequestException if the department is null, blank, or invalid
     */
    @Override
    public List<Questionnaire> getQuestionnairesByDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new BadRequestException(
                    ErrorType.VALIDATION_ERROR + "," +
                            ErrorCode.FIELD_VALIDATION_MISSING + "," +
                            "Department cannot be null or blank"
            );
        }
        try {
            Department dep = Department.valueOf(department.toUpperCase());
            return questionnaireRepository.findByDepartment(dep.name(), Sort.by(Sort.Direction.ASC, "department"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    ErrorType.VALIDATION_ERROR + "," +
                            ErrorCode.NUll_VALUE + "," +
                            "Invalid department: " + department
            );
        }
    }

    /**
     * Retrieves a {@link Questionnaire} by its unique identifier.
     *
     * @param id the unique identifier of the {@link Questionnaire}
     * @return the matching {@link Questionnaire} entity
     * @throws ResourceNotFoundException if no {@link Questionnaire} is found with the given ID
     */
    @Override
    public Questionnaire getQuestionnaireById(String id) {
        return questionnaireRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        ErrorType.RESOURCE_NOT_FOUND_ERROR + "," +
                                ErrorCode.RESOURCE_NOT_FOUND + "," +
                                "Questionnaire not found with id: " + id
                ));
    }

    /**
     * Updates an existing {@link Questionnaire} by ID with the new details provided.
     *
     * <p>If the department is being changed, ensures no duplicate exists before updating.
     *
     * @param id the unique identifier of the questionnaire to update
     * @param questionnaire the new questionnaire details
     * @return the updated {@link Questionnaire}
     * @throws BadRequestException if the department is null
     * @throws DuplicateDataException if another questionnaire exists for the new department
     * @throws ResourceNotFoundException if the questionnaire to update is not found
     */
    @Override
    public Questionnaire updateQuestionnaire(String id, Questionnaire questionnaire) {
        Questionnaire existing = getQuestionnaireById(id);

        if (questionnaire.getDepartment() == null) {
            throw new BadRequestException(
                    ErrorType.VALIDATION_ERROR + "," +
                            ErrorCode.FIELD_VALIDATION_MISSING + "," +
                            "Department is required"
            );
        }

        if (!existing.getDepartment().equals(questionnaire.getDepartment())) {
            List<Questionnaire> depExists = questionnaireRepository.findByDepartment(questionnaire.getDepartment().name(), Sort.by(Sort.Direction.ASC, "department"));
            if (!depExists.isEmpty()) {
                throw new DuplicateDataException(
                        ErrorType.RESOURCE_EXISTS_ERROR + "," +
                                ErrorCode.RESOURCE_EXISTS_ERROR + "," +
                                "Questionnaire already exists for department: " + questionnaire.getDepartment()
                );
            }
        }

        existing.setDepartment(questionnaire.getDepartment());
        existing.setQuestions(questionnaire.getQuestions());

        return questionnaireRepository.save(existing);
    }

    /**
     * Deletes a {@link Questionnaire} by its unique identifier.
     *
     * @param id the unique identifier of the questionnaire to delete
     * @throws ResourceNotFoundException if no {@link Questionnaire} exists with the given ID
     */

    @Override
    public void deleteQuestionnaire(String id) {
        if (!questionnaireRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ErrorType.RESOURCE_NOT_FOUND_ERROR + "," +
                            ErrorCode.RESOURCE_NOT_FOUND + "," +
                            "Questionnaire not found with id: " + id
            );
        }
        questionnaireRepository.deleteById(id);
    }
}
