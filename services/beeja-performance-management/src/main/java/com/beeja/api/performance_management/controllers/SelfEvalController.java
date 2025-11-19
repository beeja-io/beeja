package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.SelfEvaluation;
import com.beeja.api.performance_management.model.dto.SelfEvaluationRequest;
import com.beeja.api.performance_management.service.SelfEvaluationService;
import com.beeja.api.performance_management.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
/**
 * REST controller for managing employee self-evaluations.
 */
@RestController
@RequestMapping("/v1/api/responses/self-evaluation")
@Validated
public class SelfEvalController {

    private final SelfEvaluationService selfService;

    public SelfEvalController(SelfEvaluationService selfService) {
        this.selfService = selfService;
    }

    /**
     * Submit a self-evaluation.
     *
     * @param req the self-evaluation request
     * @return the saved SelfEvaluation
     */
    @PostMapping
    public ResponseEntity<SelfEvaluation> submitSelfEval(@Valid @RequestBody SelfEvaluationRequest req) {
        SelfEvaluation se = new SelfEvaluation();
        se.setEmployeeId(req.getEmployeeId());
        se.setSubmittedBy(req.getSubmittedBy());
        se.setResponses(req.getResponses());
        return ResponseEntity.ok(selfService.submitSelfEvaluation(se));
    }

    /**
     * Get all self-evaluations for a given employee.
     *
     * @param employeeId the employee ID
     * @return list of self-evaluations
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<SelfEvaluation>> getSelfEvals(@PathVariable String employeeId) {
        return ResponseEntity.ok(selfService.getByEmployee(employeeId));
    }

    /**
     * Get all self-evaluations for the logged-in employee.
     *
     * @return list of self-evaluations
     */
    @GetMapping("/employee/me")
    public ResponseEntity<List<SelfEvaluation>> getSelfEvalsForLoggedInEmployee() {
        String employeeId = UserContext.getLoggedInEmployeeId();
        if (employeeId == null || employeeId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Employee not found in UserContext.");
        }
        return ResponseEntity.ok(selfService.getByEmployee(employeeId));
    }
}