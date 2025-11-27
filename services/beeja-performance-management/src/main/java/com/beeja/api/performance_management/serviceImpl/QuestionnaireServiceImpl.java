package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.enums.TargetType;
import com.beeja.api.performance_management.exceptions.BadRequestException;
import com.beeja.api.performance_management.exceptions.DuplicateDataException;
import com.beeja.api.performance_management.exceptions.InvalidOperationException;
import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.Question;
import com.beeja.api.performance_management.model.Questionnaire;
import com.beeja.api.performance_management.repository.QuestionnaireRepository;
import com.beeja.api.performance_management.service.QuestionnaireService;
import com.beeja.api.performance_management.utils.BuildErrorMessage;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.ErrorUtils;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

/**
 * Service implementation for managing Questionnaires.
 * Handles creation, retrieval, update, and deletion of questionnaires
 * including validation and duplicate checks.
 * Implements {@link QuestionnaireService}.
 */
@Slf4j
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    /**
     * Creates a new questionnaire after validating and checking for duplicates.
     *
     * @param questionnaire Questionnaire object to create
     * @return Created Questionnaire
     * @throws BadRequestException if questions list is empty
     * @throws DuplicateDataException if duplicate questionnaire exists
     * @throws InvalidOperationException if save fails
     */
    @Override
    public Questionnaire createQuestionnaire(Questionnaire questionnaire) {
        log.info(Constants.INFO_CREATING_QUESTIONNAIRE);

        String orgId = UserContext.getLoggedInUserOrganization().get("id").toString();

        questionnaire.setOrganizationId(orgId);
        validateQuestionnaireRequest(questionnaire);

        checkDuplicateQuestionnaire(null, questionnaire);

        Questionnaire newQuestionnaire = new Questionnaire();
        newQuestionnaire.setOrganizationId(orgId);

        if (questionnaire.getQuestions() != null && !questionnaire.getQuestions().isEmpty()) {
                questionnaire.getQuestions().forEach(q -> {
                    if (!q.isRequired() && q.getTarget() == TargetType.SELF) {
                        q.setRequired(true);
                    }
                });

            newQuestionnaire.setQuestions(questionnaire.getQuestions());
        }

        try {
            newQuestionnaire = questionnaireRepository.save(newQuestionnaire);
        } catch (Exception e) {
            log.error(Constants.ERROR_SAVING_QUESTIONNAIRE, e);
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.INTERNAL_SERVER_ERROR,
                            ErrorCode.DATABASE_ERROR,
                            Constants.ERROR_SAVING_QUESTIONNAIRE
                    ));
        }

        log.info(Constants.INFO_QUESTIONNAIRE_CREATED, newQuestionnaire.getId());
        return newQuestionnaire;
    }

    /**
     * Retrieves all questionnaires associated with the currently logged-in user's organization,
     * sorted by questionnaire ID in ascending order.
     *
     * @return a list of {@link Questionnaire} objects belonging to the user's organization,
     *         sorted by their ID in ascending order
     */
    @Override
    public List<Questionnaire> getAllQuestionnaires() {
        log.info(Constants.INFO_FETCHING_ALL_QUESTIONNAIRES);

        String orgId = UserContext.getLoggedInUserOrganization().get("id").toString();
        return questionnaireRepository.findByOrganizationId(orgId, Sort.by(Sort.Direction.ASC, "id"));
    }

    /**
     * Returns all questionnaires. Department filter no longer applies.
     *
     * @param department Department name (ignored)
     * @return List of all Questionnaires
     */
    @Override
    public List<Questionnaire> getQuestionnairesByDepartment(String department) {
        return getAllQuestionnaires();
    }

    /**
     * Retrieves a questionnaire by its ID.
     *
     * @param id Questionnaire ID
     * @return Questionnaire object
     * @throws ResourceNotFoundException if not found
     */
    @Override
    public Questionnaire getQuestionnaireById(String id) {
        log.info(Constants.INFO_FETCHING_QUESTIONNAIRE_BY_ID, id);

        return questionnaireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.ERROR_INVALID_QUESTIONNAIRE + id
                        )));
    }

    /**
     * Updates the questions of an existing questionnaire after validation.
     *
     * @param id Questionnaire ID
     * @param questionnaire Updated questionnaire data
     * @return Updated Questionnaire
     * @throws BadRequestException if questions list is empty
     * @throws DuplicateDataException if duplicate questionnaire exists
     * @throws InvalidOperationException if save fails
     */
    @Override
    public Questionnaire updateQuestionnaire(String id, Questionnaire questionnaire) {
        log.info(Constants.INFO_UPDATING_QUESTIONNAIRE_BY_ID, id);
        Questionnaire existing = getQuestionnaireById(id);

        validateQuestionnaireRequest(questionnaire);
        checkDuplicateQuestionnaire(id, questionnaire);

        existing.setQuestions(questionnaire.getQuestions());

        try {
            existing = questionnaireRepository.save(existing);
        } catch (Exception e) {
            log.error(Constants.ERROR_UPDATING_QUESTIONNAIRE, e);
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.INTERNAL_SERVER_ERROR,
                            ErrorCode.DATABASE_ERROR,
                            Constants.ERROR_UPDATING_QUESTIONNAIRE
                    )
            );
        }

        log.info(Constants.INFO_QUESTIONNAIRE_UPDATED_SUCCESS, existing.getId());
        return existing;
    }

    /**
     * Updates the questions list of a questionnaire.
     *
     * @param id Questionnaire ID
     * @param updatedQuestions List of updated questions
     * @return Updated Questionnaire
     * @throws InvalidOperationException if questions list is empty
     */
    @Override
    public Questionnaire updateQuestions(String id, List<Question> updatedQuestions) {
        log.info(Constants.INFO_UPDATING_QUESTIONS_FOR_QUESTIONNAIRE, id);

        Questionnaire existing = getQuestionnaireById(id);

        if (updatedQuestions == null || updatedQuestions.isEmpty()) {
            throw new InvalidOperationException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.INVALID_OPERATION,
                            Constants.ERROR_QUESTION_LIST_EMPTY
                    ));
        }
        ensureNoDuplicateQuestions(updatedQuestions);
        existing.setQuestions(updatedQuestions);
        return questionnaireRepository.save(existing);
    }

    private void validateQuestionnaireRequest(Questionnaire questionnaire) {
        if (questionnaire.getQuestions() == null || questionnaire.getQuestions().isEmpty()) {
            throw new BadRequestException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.FIELD_VALIDATION_MISSING,
                            Constants.ERROR_QUESTION_LIST_EMPTY
                    ));
        }
        ensureNoDuplicateQuestions(questionnaire.getQuestions());
    }

    private void checkDuplicateQuestionnaire(String currentId, Questionnaire questionnaire) {
        List<Questionnaire> existingList = questionnaireRepository.findAll();

        List<String> incoming = questionnaire.getQuestions().stream()
                .map(q -> normalizeText(q.getQuestion()))
                .toList();

        boolean isDuplicate = existingList.stream()
                .filter(q -> currentId == null || !q.getId().equals(currentId))
                .anyMatch(q -> q.getQuestions() != null &&
                        q.getQuestions().stream()
                                        .map(eq -> normalizeText(eq.getQuestion()))
                                        .toList()
                                        .equals(incoming));

        if (isDuplicate) {
            throw new DuplicateDataException(
                    ErrorUtils.formatError(
                            ErrorType.RESOURCE_EXISTS_ERROR,
                            ErrorCode.RESOURCE_EXISTS_ERROR,
                            Constants.DUPLICATE_QUESTIONNAIRE_WITH_SAME_QUESTIONS
                    ));
        }
    }

    /**
     * Deletes a questionnaire by ID.
     *
     * @param id Questionnaire ID
     * @throws ResourceNotFoundException if not found
     */
    @Override
    public void deleteQuestionnaire(String id) {
        log.info(Constants.INFO_DELETING_QUESTIONNAIRE_BY_ID, id);

        if (!questionnaireRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.RESOURCE_NOT_FOUND_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.ERROR_QUESTIONNAIRE_NOT_FOUND + id
                    ));
        }
        questionnaireRepository.deleteById(id);
        log.info(Constants.INFO_QUESTIONNAIRE_DELETED, id);
    }

    /**
     * Your question-level duplicate validation (with normalized text).
     * Kept as a separate method and reused in create/update/updateQuestions.
     */
    @Override
    public void ensureNoDuplicateQuestions(List<Question> questions) {
        Set<String> seen = new HashSet<>();

        for (Question q : questions) {

            if (q == null || q.getQuestion() == null)
                continue;

            String normalized = normalizeText(q.getQuestion());

            if (normalized.isBlank()) {
                throw new BadRequestException(
                        ErrorUtils.formatError(
                                ErrorType.VALIDATION_ERROR,
                                ErrorCode.FIELD_VALIDATION_MISSING,
                                Constants.ERROR_INVALID_QUESTION_TEXT + q.getQuestion()
                        ));
            }

            if (!seen.add(normalized)) {
                throw new DuplicateDataException(
                        ErrorUtils.formatError(
                                ErrorType.RESOURCE_EXISTS_ERROR,
                                ErrorCode.RESOURCE_EXISTS_ERROR,
                                Constants.ERROR_DUPLICATE_QUESTION + q.getQuestion()
                        ));
            }
        }
    }

    /**
     * Your normalizeText logic: lowercasing, removing accents/symbols,
     * collapsing spaces, handling repeated patterns, etc.
     */
    private String normalizeText(String text) {

        if (text == null) return "";

        text = text.toLowerCase().trim();
        text = Normalizer.normalize(text, Normalizer.Form.NFKD);
        text = text.replaceAll("\\p{M}", "");
        text = text.replaceAll("[\\p{So}\\p{Cn}\\p{Cf}]", "");
        text = text.replaceAll("[^a-z0-9 ]", "");
        text = text.replaceAll("\\s+", " ").trim();

        if (text.isBlank()) return "";

        String[] parts = text.split(" ");
        boolean allSingleLetters = true;

        for (String p : parts) {
            if (p.length() != 1) {
                allSingleLetters = false;
                break;
            }
        }

        if (allSingleLetters) {
            text = text.replace(" ", "");
        } else {

            Set<String> uniqueWords = new HashSet<>();
            StringBuilder builder = new StringBuilder();
            for (String word : parts) {
                if (uniqueWords.add(word)) {
                    if (builder.length() > 0) builder.append(" ");
                    builder.append(word);
                }
            }
            text = builder.toString();
            text = text.replace(" ", "");
        }

        while (text.matches("^(.+?)\\1+$")) {
            text = text.substring(0, text.length() / 2);
        }
        return text;
    }
}
