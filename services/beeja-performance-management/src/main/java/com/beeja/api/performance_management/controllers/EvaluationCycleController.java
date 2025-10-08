package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.exceptions.InvalidOperationException;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.Questionnaire;
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
 * REST controller for managing {@link EvaluationCycle} entities.
 * Provides endpoints for creating, retrieving, updating, and linking questionnaires
 * to evaluation cycles, as well as status management and detailed views.
 * <p>
 * Base URL: /api/cycles
 * </p>
 */
@RestController
@RequestMapping("/api/cycles")
@CrossOrigin(origins = "*")
public class EvaluationCycleController {

    @Autowired
    private EvaluationCycleService cycleService;

    @Autowired
    private QuestionnaireService questionnaireService;


    /**
     * Creates a new Evaluation Cycle.
     *
     * @param cycle the EvaluationCycle object to create
     * @return ResponseEntity containing the created EvaluationCycle and HTTP status 201
     */
    @PostMapping
    public ResponseEntity<EvaluationCycle> createEvaluationCycle(@Valid @RequestBody EvaluationCycle cycle) {
        EvaluationCycle created = cycleService.createCycle(cycle);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Retrieves all Evaluation Cycles.
     *
     * @return ResponseEntity with a list of all EvaluationCycle objects and HTTP status 200
     */
    @GetMapping
    public ResponseEntity<List<EvaluationCycle>> getAllEvaluationCycles() {
        List<EvaluationCycle> cycles = cycleService.getAllCycles();
        return ResponseEntity.ok(cycles);
    }

    /**
     * Retrieves all Evaluation Cycles along with their associated Questionnaires.
     *
     * @return ResponseEntity containing a list of EvaluationCycleDetailsDto
     */
    @GetMapping("/with-questionnaires")
    public ResponseEntity<List<EvaluationCycleDetailsDto>> getCyclesWithQuestionnaires() {
        List<EvaluationCycle> allCycles = cycleService.getAllCycles();

        List<EvaluationCycleDetailsDto> result = allCycles.stream()
                .map(cycle -> {
                    Questionnaire questionnaire = null;
                    if (cycle.getDepartment() != null) {
                        if (cycle.getQuestionnaireId() != null) {
                            try {
                                questionnaire = questionnaireService.getQuestionnaireById(cycle.getQuestionnaireId());
                            } catch (Exception e) {
                            }
                        }

                        if (questionnaire == null) {
                            try {
                                questionnaire = questionnaireService
                                        .getQuestionnairesByDepartment(cycle.getDepartment().name())
                                        .stream()
                                        .findFirst()
                                        .orElse(null);
                            } catch (Exception e) {
                            }
                        }
                    }

                    return new EvaluationCycleDetailsDto(cycle, questionnaire);
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves an Evaluation Cycle by its ID.
     *
     * @param id the ID of the EvaluationCycle
     * @return ResponseEntity containing the EvaluationCycle object and HTTP status 200
     */
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCycle> getEvaluationCycleById(@PathVariable String id) {
        EvaluationCycle cycle = cycleService.getCycleById(id);
        return ResponseEntity.ok(cycle);
    }


    /**
     * Retrieves the detailed view of an Evaluation Cycle, including associated questionnaire.
     *
     * @param id the ID of the EvaluationCycle
     * @return ResponseEntity containing EvaluationCycleDetailsDto and HTTP status 200
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<EvaluationCycleDetailsDto> getEvaluationCycleDetails(@PathVariable String id) {
        EvaluationCycleDetailsDto dto = cycleService.getCycleWithQuestionnaire(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Retrieves the currently active Evaluation Cycle.
     *
     * @return ResponseEntity containing the active EvaluationCycle and HTTP status 200
     */
    @GetMapping("/active")
    public ResponseEntity<EvaluationCycle> getActiveEvaluationCycle() {
        EvaluationCycle cycle = cycleService.getCurrentActiveCycle();
        return ResponseEntity.ok(cycle);
    }

    /**
     * Updates an existing Evaluation Cycle.
     *
     * @param id the ID of the EvaluationCycle to update
     * @param cycle the updated EvaluationCycle object
     * @return ResponseEntity containing the updated EvaluationCycle and HTTP status 200
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
     * @param id the ID of the EvaluationCycle
     * @param statusUpdate a map containing the new status value
     * @return ResponseEntity containing the updated EvaluationCycle and HTTP status 200
     * @throws InvalidOperationException if the status field is missing or invalid
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
     * Links a Questionnaire to an Evaluation Cycle.
     *
     * @param id the ID of the EvaluationCycle
     * @param request a map containing the questionnaireId
     * @return ResponseEntity containing the updated EvaluationCycle and HTTP status 200
     * @throws InvalidOperationException if questionnaireId is missing or empty
     */
    @PatchMapping("/{id}/questionnaire")
    public ResponseEntity<EvaluationCycle> linkQuestionnaire(
            @PathVariable String id, @RequestBody Map<String, String> request) {

        String questionnaireId = request.get("questionnaireId");
        if (questionnaireId == null || questionnaireId.trim().isEmpty()) {
            throw new InvalidOperationException("Missing 'questionnaireId' field in request body");
        }

        EvaluationCycle cycle = cycleService.getCycleById(id);
        cycle.setQuestionnaireId(questionnaireId);
        EvaluationCycle updated = cycleService.updateCycle(id, cycle);

        return ResponseEntity.ok(updated);
    }
}