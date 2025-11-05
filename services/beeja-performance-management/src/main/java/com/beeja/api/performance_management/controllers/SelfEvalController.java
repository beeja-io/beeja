package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.SelfEvaluation;
import com.beeja.api.performance_management.model.dto.SelfEvaluationRequest;
import com.beeja.api.performance_management.service.SelfEvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing self-evaluations.
 */
@RestController
@RequestMapping("/v1/api/responses/self-evaluation")
@Validated
public class SelfEvalController {

    private final SelfEvaluationService selfService;

    public SelfEvalController(SelfEvaluationService selfService) {
        this.selfService = selfService;
    }

    /** Submits a self-evaluation for an employee. */
    @PostMapping
    public ResponseEntity<SelfEvaluation> submitSelfEval(@Valid @RequestBody SelfEvaluationRequest req) {
        SelfEvaluation se = new SelfEvaluation();
        se.setEmployeeId(req.getEmployeeId());
        se.setSubmittedBy(req.getSubmittedBy());
        se.setResponses(req.getResponses());
        return ResponseEntity.ok(selfService.submitSelfEvaluation(se));
    }

    /** Retrieves all self-evaluations submitted by an employee. */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<SelfEvaluation>> getSelfEvals(@PathVariable String employeeId) {
        return ResponseEntity.ok(selfService.getByEmployee(employeeId));
    }
}
