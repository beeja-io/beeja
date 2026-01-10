package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.Constants.PermissionConstants;
import com.beeja.api.performance_management.annotations.HasPermission;
import com.beeja.api.performance_management.model.FeedbackResponse;
import com.beeja.api.performance_management.model.dto.CycleWithResponsesDTO;
import com.beeja.api.performance_management.model.dto.EmployeeGroupedResponsesDTO;
import com.beeja.api.performance_management.model.dto.SubmitFeedbackRequest;
import com.beeja.api.performance_management.response.MyFeedbackFormResponse;
import com.beeja.api.performance_management.service.FeedbackResponseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for managing feedback responses.
 */
@RestController
@RequestMapping("/v1/api/responses")
@Validated
@Slf4j
public class FeedbackResponseController {

    private final FeedbackResponseService responseService;

    public FeedbackResponseController(FeedbackResponseService responseService) {
        this.responseService = responseService;
    }

    /** Submits a new feedback response. */
    @PostMapping
    @HasPermission(PermissionConstants.PROVIDE_FEEDBACK)
    public ResponseEntity<FeedbackResponse> submitFeedback(@Valid @RequestBody SubmitFeedbackRequest req) {
        FeedbackResponse created = responseService.submitFeedback(req);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /** Retrieves feedback responses for an employee in a specific cycle. */
    @GetMapping("/employee/{employeeId}/cycle/{cycleId}")
    @HasPermission(PermissionConstants.READ_RESPONSES)
    public ResponseEntity<List<FeedbackResponse>> getResponsesByEmployeeCycle(
            @PathVariable String employeeId,
            @PathVariable String cycleId) {
        List<FeedbackResponse> responses = responseService.getByEmployeeAndCycle(employeeId, cycleId);
        return ResponseEntity.ok(responses);
    }

    /** Retrieves feedback responses by form ID. */
    @GetMapping("/form/{formId}")
    @HasPermission(PermissionConstants.READ_RESPONSES)
    public ResponseEntity<List<FeedbackResponse>> getResponsesForForm(@PathVariable String formId) {
        List<FeedbackResponse> responses = responseService.getByFormId(formId);
        return ResponseEntity.ok(responses);
    }

    /** Retrieves all feedback responses for a specific employee. */
    @GetMapping("/employee/{employeeId}")
    @HasPermission(PermissionConstants.READ_RESPONSES)
    public ResponseEntity<List<FeedbackResponse>> getResponsesByEmployee(@PathVariable String employeeId) {
        List<FeedbackResponse> responses = responseService.getByEmployee(employeeId);
        return ResponseEntity.ok(responses);
    }

    /** Retrieves grouped feedback responses for an employee along with cycle details. */
    @GetMapping("/employee/{employeeId}/grouped")
    public ResponseEntity<EmployeeGroupedResponsesDTO> getGroupedResponsesByEmployee(@PathVariable String employeeId) {
        EmployeeGroupedResponsesDTO dto = responseService.getGroupedResponsesWithCycleByEmployee(employeeId);
        return ResponseEntity.ok(dto);
    }

    /** Retrieves all feedback responses for a specific evaluation cycle with cycle details. */
    @GetMapping("/cycle/{cycleId}")
    public ResponseEntity<CycleWithResponsesDTO> getResponsesForCycle(@PathVariable String cycleId) {
        CycleWithResponsesDTO dto = responseService.getResponsesForCycle(cycleId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Returns all feedback forms (cycles) where the logged-in employee has received feedback.
     */
    @GetMapping("/my-feedback/forms")
    @HasPermission(PermissionConstants.READ_OWN_RESPONSES)
    public ResponseEntity<List<MyFeedbackFormResponse>> getMyFeedbackForms() {
        List<MyFeedbackFormResponse> forms = responseService.getMyFeedbackForms();
        return ResponseEntity.ok(forms);
    }

    /**
     * Returns grouped feedback responses (all answers from reviewers) for the logged-in employee
     * within a specific form (cycle).
     */
    @GetMapping("/my-feedback/cycle/{cycleId}")
    @HasPermission(PermissionConstants.READ_OWN_RESPONSES)
    public ResponseEntity<EmployeeGroupedResponsesDTO> getMyResponsesByCycle(
            @PathVariable String cycleId) {
        EmployeeGroupedResponsesDTO dto = responseService.getMyResponsesByCycle(cycleId);
        return ResponseEntity.ok(dto);
    }
}
