package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.exceptions.InvalidOperationException;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.Questionnaire;
import com.beeja.api.performance_management.model.dto.EvaluationCycleCreateDto;
import com.beeja.api.performance_management.model.dto.EvaluationCycleDetailsDto;
import com.beeja.api.performance_management.service.EvaluationCycleService;
import com.beeja.api.performance_management.service.QuestionnaireService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing Evaluation Cycles in the performance management system.
 * Provides endpoints to create, retrieve, update, and delete evaluation cycles,
 * as well as manage related questionnaires and their statuses.
 */
@RestController
@RequestMapping("/api/cycles")
public class EvaluationCycleController {

    @Autowired
    private EvaluationCycleService cycleService;

    @Autowired
    private QuestionnaireService questionnaireService;

    /**
     * Creates a new Evaluation Cycle.
     *
     * @param cycle the EvaluationCycle object to be created
     * @return the created EvaluationCycle with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<EvaluationCycle> createEvaluationCycle(@Valid @RequestBody EvaluationCycle cycle) {
        EvaluationCycle created = cycleService.createCycle(cycle);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Creates a new evaluation cycle along with optional questions.
     * Accepts a JSON payload containing cycle details and an optional list of questions.
     * Returns the created cycle and associated questionnaire information.
     *
     * @param dto The request body containing evaluation cycle data and optional questions.
     * @return A {@link ResponseEntity} with the created {@link EvaluationCycleDetailsDto} and HTTP 201 status.
     */
    @PostMapping("/create-with-questions")
    public ResponseEntity<EvaluationCycleDetailsDto> createCycleWithQuestions(
            @Valid @RequestBody EvaluationCycleCreateDto dto) {
        EvaluationCycleDetailsDto created = cycleService.createCycleWithQuestions(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Retrieves all Evaluation Cycles.
     *
     * @return list of EvaluationCycle objects with HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<List<EvaluationCycle>> getAllEvaluationCycles() {
        List<EvaluationCycle> cycles = cycleService.getAllCycles();
        return ResponseEntity.ok(cycles);
    }

    /**
     * Retrieves all Evaluation Cycles along with their associated Questionnaires.
     *
     * @return list of EvaluationCycleDetailsDto containing cycle and questionnaire data
     */
    @GetMapping("/with-questionnaires")
    public ResponseEntity<List<EvaluationCycleDetailsDto>> getCyclesWithQuestionnaires() {
        List<EvaluationCycle> allCycles = cycleService.getAllCycles();

        List<EvaluationCycleDetailsDto> result = allCycles.stream()
                .map(cycle -> {
                    Questionnaire questionnaire = null;
                    if (cycle.getQuestionnaireId() != null) {
                        try {
                            questionnaire = questionnaireService.getQuestionnaireById(cycle.getQuestionnaireId());
                        } catch (Exception e) {
                            // Log error or handle exception if needed
                        }
                    }

                    return new EvaluationCycleDetailsDto(cycle, questionnaire);
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves a specific Evaluation Cycle by ID.
     *
     * @param id the ID of the Evaluation Cycle
     * @return the EvaluationCycle object with HTTP 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCycle> getEvaluationCycleById(@PathVariable String id) {
        EvaluationCycle cycle = cycleService.getCycleById(id);
        return ResponseEntity.ok(cycle);
    }

    /**
     * Retrieves details of a specific Evaluation Cycle, including its questionnaire.
     *
     * @param id the ID of the Evaluation Cycle
     * @return EvaluationCycleDetailsDto with cycle and questionnaire information
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<EvaluationCycleDetailsDto> getEvaluationCycleDetails(@PathVariable String id) {
        EvaluationCycleDetailsDto dto = cycleService.getCycleWithQuestionnaire(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Retrieves Evaluation Cycles by active status.
     *
     * @param activeStatus optional query parameter for filtering by CycleStatus
     * @return list of EvaluationCycle objects
     */
    @GetMapping("/active")
    public ResponseEntity<List<EvaluationCycle>> getActiveEvaluationCycle(
            @RequestParam(value = "active", required = false) String activeStatus) {
        List<EvaluationCycle> cycles;

        if (activeStatus != null) {
            cycles = cycleService.getCyclesByStatus(CycleStatus.valueOf(activeStatus.toUpperCase()));
        } else {
            cycles = cycleService.getAllCycles();
        }

        return ResponseEntity.ok(cycles);
    }

    /**
     * Updates an existing Evaluation Cycle by ID.
     *
     * @param id    the ID of the Evaluation Cycle to update
     * @param cycle the updated EvaluationCycle object
     * @return the updated EvaluationCycle with HTTP 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationCycle> updateEvaluationCycle(
            @PathVariable String id, @Valid @RequestBody EvaluationCycle cycle) {
        EvaluationCycle updated = cycleService.updateCycle(id, cycle);
        return ResponseEntity.ok(updated);
    }

    /**
     * Updates the status of an Evaluation Cycle.
     *
     * @param id           the ID of the Evaluation Cycle
     * @param statusUpdate map containing the new status (e.g., {"status": "ACTIVE"})
     * @return the updated EvaluationCycle with the new status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<EvaluationCycle> updateEvaluationCycleStatus(
            @PathVariable String id, @RequestBody Map<String, String> statusUpdate) {

        String statusValue = statusUpdate.get("status");
        if (statusValue == null || statusValue.trim().isEmpty()) {
            throw new InvalidOperationException("Missing 'status' field in request body");
        }

        CycleStatus status;
        try {
            status = CycleStatus.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException("Invalid status value: " + statusValue);
        }

        EvaluationCycle updated = cycleService.updateCycleStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    /**
     * Performs a full update of the Evaluation Cycle and its associated Questionnaire.
     *
     * @param id  the ID of the Evaluation Cycle
     * @param dto EvaluationCycleDetailsDto containing updated cycle and questionnaire details
     * @return the updated EvaluationCycleDetailsDto
     */
    @PutMapping("/{id}/full-update")
    public ResponseEntity<EvaluationCycleDetailsDto> updateFullEvaluationCycle(
            @PathVariable String id,
            @Valid @RequestBody EvaluationCycleDetailsDto dto) {

        EvaluationCycleDetailsDto updated = cycleService.updateFullCycle(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes an Evaluation Cycle by ID.
     *
     * @param id the ID of the Evaluation Cycle to delete
     * @return HTTP 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluationCycle(@PathVariable String id) {
        cycleService.deleteCycle(id);
        return ResponseEntity.noContent().build();
    }
}
