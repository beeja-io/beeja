package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.Questionnaire;
import com.beeja.api.performance_management.service.QuestionnaireService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing questionnaires within the performance management system.
 * Provides endpoints for creating, retrieving, updating, and deleting questionnaires.
 */
@RestController
@RequestMapping("/v1/api/questionnaires")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionnaireService;

    /**
     * Creates a new questionnaire.
     *
     * @param questionnaire The questionnaire object sent in the request body.
     * @return The created questionnaire with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Questionnaire> createQuestionnaire(@Valid @RequestBody Questionnaire questionnaire) {
        Questionnaire created = questionnaireService.createQuestionnaire(questionnaire);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Retrieves all questionnaires.
     * Note: Department filtering has been removed.
     *
     * @return A list of all questionnaires.
     */
    @GetMapping
    public ResponseEntity<List<Questionnaire>> getAllQuestionnaires() {
        List<Questionnaire> questionnaires = questionnaireService.getAllQuestionnaires();
        return ResponseEntity.ok(questionnaires);
    }

    /**
     * Retrieves a single questionnaire by its ID.
     *
     * @param id The unique identifier of the questionnaire.
     * @return The requested questionnaire.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Questionnaire> getQuestionnaireById(@PathVariable String id) {
        Questionnaire questionnaire = questionnaireService.getQuestionnaireById(id);
        return ResponseEntity.ok(questionnaire);
    }

    /**
     * Updates an existing questionnaire by its ID.
     *
     * @param id            The ID of the questionnaire to update.
     * @param questionnaire The updated questionnaire data.
     * @return The updated questionnaire.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Questionnaire> updateQuestionnaire(
            @PathVariable String id, @Valid @RequestBody Questionnaire questionnaire) {
        Questionnaire updated = questionnaireService.updateQuestionnaire(id, questionnaire);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a questionnaire by its ID.
     *
     * @param id The ID of the questionnaire to delete.
     * @return HTTP 204 (No Content) on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionnaire(@PathVariable String id) {
        questionnaireService.deleteQuestionnaire(id);
        return ResponseEntity.noContent().build();
    }
}
