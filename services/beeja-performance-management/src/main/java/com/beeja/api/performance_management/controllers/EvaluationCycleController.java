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

@RestController
@RequestMapping("/v1/api/cycles")
public class EvaluationCycleController {

    @Autowired
    private EvaluationCycleService cycleService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @PostMapping
    public ResponseEntity<EvaluationCycle> createEvaluationCycle(@Valid @RequestBody EvaluationCycle cycle) {
        EvaluationCycle created = cycleService.createCycle(cycle);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/create-with-questions")
    public ResponseEntity<EvaluationCycleDetailsDto> createCycleWithQuestions(
            @Valid @RequestBody EvaluationCycleCreateDto dto) {
        EvaluationCycleDetailsDto created = cycleService.createCycleWithQuestions(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EvaluationCycle>> getAllEvaluationCycles() {
        List<EvaluationCycle> cycles = cycleService.getAllCycles();
        return ResponseEntity.ok(cycles);
    }

    @GetMapping("/with-questionnaires")
    public ResponseEntity<List<EvaluationCycleDetailsDto>> getCyclesWithQuestionnaires() {
        List<EvaluationCycle> allCycles = cycleService.getAllCycles();

        List<EvaluationCycleDetailsDto> result = allCycles.stream()
                .map(cycle -> {
                    Questionnaire questionnaire = null;
                    if (cycle.getQuestionnaireId() != null) {
                        try {
                            questionnaire = questionnaireService.getQuestionnaireById(cycle.getQuestionnaireId());
                        } catch (Exception ignored) { }
                    }
                    return new EvaluationCycleDetailsDto(cycle, questionnaire);
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCycle> getEvaluationCycleById(@PathVariable String id) {
        EvaluationCycle cycle = cycleService.getCycleById(id);
        return ResponseEntity.ok(cycle);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<EvaluationCycleDetailsDto> getEvaluationCycleDetails(@PathVariable String id) {
        EvaluationCycleDetailsDto dto = cycleService.getCycleWithQuestionnaire(id);
        return ResponseEntity.ok(dto);
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<EvaluationCycle> updateEvaluationCycle(
            @PathVariable String id, @Valid @RequestBody EvaluationCycle cycle) {
        EvaluationCycle updated = cycleService.updateCycle(id, cycle);
        return ResponseEntity.ok(updated);
    }

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

    @PutMapping("/{id}/full-update")
    public ResponseEntity<EvaluationCycleDetailsDto> updateFullEvaluationCycle(
            @PathVariable String id,
            @Valid @RequestBody EvaluationCycleDetailsDto dto) {
        EvaluationCycleDetailsDto updated = cycleService.updateFullCycle(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluationCycle(@PathVariable String id) {
        cycleService.deleteCycle(id);
        return ResponseEntity.noContent().build();
    }
}
