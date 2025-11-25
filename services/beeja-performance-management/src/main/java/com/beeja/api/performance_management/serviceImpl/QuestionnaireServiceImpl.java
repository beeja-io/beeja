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

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
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
     * @throws BadRequestException     if questions list is empty or duplicates found inside same form
     * @throws DuplicateDataException  if duplicate questionnaire exists for same org + cycle
     * @throws InvalidOperationException if save fails
     */
    @Override
    public Questionnaire createQuestionnaire(Questionnaire questionnaire) {
        log.info(Constants.INFO_CREATING_QUESTIONNAIRE);

        String orgId = UserContext.getLoggedInUserOrganization().get("id").toString();
        questionnaire.setOrganizationId(orgId);

        // basic validations
        validateQuestionnaireRequest(questionnaire);
        ensureNoDuplicateQuestions(questionnaire.getQuestions());

        // cycleId is required for per-cycle scoping
        if (questionnaire.getCycleId() == null || questionnaire.getCycleId().trim().isEmpty()) {
            throw new BadRequestException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.FIELD_VALIDATION_MISSING,
                            "cycleId is required for questionnaire creation"
                    ));
        }

        // check duplicate questionnaires only within same org + same cycle
        checkDuplicateQuestionnaire(null, questionnaire);

        Questionnaire newQuestionnaire = new Questionnaire();
        newQuestionnaire.setOrganizationId(orgId);
        newQuestionnaire.setCycleId(questionnaire.getCycleId());
        newQuestionnaire.setQuestions(questionnaire.getQuestions());

        // ensure SELF-target questions are required
        if (newQuestionnaire.getQuestions() != null) {
            newQuestionnaire.getQuestions().forEach(q -> {
                if (!q.isRequired() && q.getTarget() == TargetType.SELF) {
                    q.setRequired(true);
                }
            });
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
     * sorted by their ID in ascending order
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
     * @param id           Questionnaire ID
     * @param questionnaire Updated questionnaire data (usually contains only questions)
     * @return Updated Questionnaire
     * @throws BadRequestException      if questions list is empty or duplicates found inside form
     * @throws DuplicateDataException   if another questionnaire in same org + cycle has identical questions
     * @throws InvalidOperationException if save fails
     */
    @Override
    public Questionnaire updateQuestionnaire(String id, Questionnaire questionnaire) {
        log.info(Constants.INFO_UPDATING_QUESTIONNAIRE_BY_ID, id);
        Questionnaire existing = getQuestionnaireById(id);

        validateQuestionnaireRequest(questionnaire);
        ensureNoDuplicateQuestions(questionnaire.getQuestions());

        // Use existing orgId + cycleId so duplicate check is scoped correctly
        questionnaire.setOrganizationId(existing.getOrganizationId());
        questionnaire.setCycleId(existing.getCycleId());

        // Check duplicates in same (orgId, cycleId), ignoring this current questionnaire id
        checkDuplicateQuestionnaire(id, questionnaire);

        // Apply new questions
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
     * This endpoint only enforces:
     *  - questions list not empty
     *  - no duplicate question texts within the same questionnaire
     *
     * It does NOT check "full form duplicate" across other questionnaires.
     *
     * @param id              Questionnaire ID
     * @param updatedQuestions List of updated questions
     * @return Updated Questionnaire
     * @throws BadRequestException       if questions list is empty or duplicates found
     * @throws InvalidOperationException if save fails
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

        // Ensure no duplicate question texts within this form
        ensureNoDuplicateQuestions(updatedQuestions);

        existing.setQuestions(updatedQuestions);
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
        return existing;
    }

    /**
     * Basic validation to ensure questions are present.
     */
    private void validateQuestionnaireRequest(Questionnaire questionnaire) {
        if (questionnaire.getQuestions() == null || questionnaire.getQuestions().isEmpty()) {
            throw new BadRequestException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.FIELD_VALIDATION_MISSING,
                            Constants.ERROR_QUESTION_LIST_EMPTY
                    ));
        }
    }

    /**
     * Checks for duplicate questionnaires (same questions list) in the same org + same cycle.
     */
    private void checkDuplicateQuestionnaire(String currentId, Questionnaire questionnaire) {
        String orgId = questionnaire.getOrganizationId();
        String cycleId = questionnaire.getCycleId();

        if (orgId == null || cycleId == null) {
            throw new BadRequestException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.FIELD_VALIDATION_MISSING,
                            "cycleId is required for duplicate validation"
                    ));
        }

        List<Questionnaire> existingList =
                questionnaireRepository.findByOrganizationIdAndCycleId(orgId, cycleId, Sort.by(Sort.Direction.ASC, "id"));

        boolean isDuplicate = existingList.stream()
                .filter(q -> currentId == null || !q.getId().equals(currentId))
                .anyMatch(q -> q.getQuestions() != null &&
                        q.getQuestions().equals(questionnaire.getQuestions()));

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
     * Ensure there are no duplicate question texts within the provided list.
     * Uses normalization (case-insensitive, ignores punctuation and extra spaces).
     */
    private void ensureNoDuplicateQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) return;

        Set<String> seen = new HashSet<>();

        for (Question q : questions) {
            if (q == null) continue;

            String raw = q.getQuestion();
            String normalized = normalizeString(raw);

            if (normalized.isEmpty()) continue;

            if (!seen.add(normalized)) {
                throw new BadRequestException(
                        ErrorUtils.formatError(
                                ErrorType.VALIDATION_ERROR,
                                ErrorCode.FIELD_VALIDATION_MISSING,
                                "Duplicate question found in the request: \"" + raw + "\""
                        )
                );
            }
        }
    }

    private String normalizeString(String s) {
        if (s == null) return "";

        // 1. Unicode normalize
        String out = Normalizer.normalize(s, Normalizer.Form.NFKC);

        // 2. trim + lowercase
        out = out.trim().toLowerCase();

        // 3. remove punctuation
        out = out.replaceAll("\\p{Punct}", "");

        // 4. collapse multiple spaces
        return out.replaceAll("\\s+", " ").trim();
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
}
