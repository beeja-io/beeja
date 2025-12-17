package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.Constants.PermissionConstants;
import com.beeja.api.performance_management.annotations.HasPermission;
import com.beeja.api.performance_management.model.FeedbackProvider;
import com.beeja.api.performance_management.request.FeedbackProviderRequest;
import com.beeja.api.performance_management.response.FeedbackProviderDetails;
import com.beeja.api.performance_management.response.ReviewerAssignedEmployeesResponse;
import com.beeja.api.performance_management.service.FeedbackProvidersService;
import com.beeja.api.performance_management.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/feedbackProvider")
public class FeedbackProvidersController {

    @Autowired
    FeedbackProvidersService feedbackProvidersService;

    @PostMapping("/assign/{employeeId}")
    @HasPermission(PermissionConstants.ASSIGN_PROVIDERS)
    public ResponseEntity<List<FeedbackProvider>> assignFeedbackProvider(
            @PathVariable String employeeId,
            @RequestBody FeedbackProviderRequest requestDto) {

        List<FeedbackProvider> assignedProviders = feedbackProvidersService.assignFeedbackProvider(employeeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignedProviders);
    }

    @PutMapping("/providers/{employeeId}")
    @HasPermission(PermissionConstants.UPDATE_PROVIDERS)
    public ResponseEntity<List<FeedbackProvider>> updateFeedbackProviders(
            @PathVariable String employeeId,
            @RequestBody FeedbackProviderRequest request) {
        List<FeedbackProvider> updatedForms = feedbackProvidersService.updateFeedbackProviders(request, employeeId);

        return ResponseEntity.ok(updatedForms);
    }

    @GetMapping("/{employeeId}/{cycleId}")
    @HasPermission(PermissionConstants.READ_PROVIDERS)
    public ResponseEntity<FeedbackProviderDetails> getFeedbackFormDetails(
            @PathVariable String employeeId,
            @PathVariable String cycleId,
            @RequestParam(required = false) String providerStatus
    ) {

        FeedbackProviderDetails response = feedbackProvidersService
                .getFeedbackFormDetails( employeeId, cycleId, providerStatus);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reviewer")
    @HasPermission({PermissionConstants.READ_PROVIDERS, PermissionConstants.READ_RESPONSES})
    public ResponseEntity<ReviewerAssignedEmployeesResponse> getEmployeesAssignedToReviewer() {

        ReviewerAssignedEmployeesResponse response =
                feedbackProvidersService.getEmployeesAssignedToReviewer();

        if (response == null || response.getAssignedEmployees() == null
                || response.getAssignedEmployees().isEmpty()) {

            return ResponseEntity.ok(
                    ReviewerAssignedEmployeesResponse.builder()
                            .reviewerId(UserContext.getLoggedInEmployeeId())
                            .assignedEmployees(List.of())
                            .build()
            );
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/forms/{employeeId}")
    @HasPermission(PermissionConstants.READ_RESPONSES)
    public ResponseEntity<?> getFormsByEmployee(
            @PathVariable String employeeId) {

        String reviewerId = UserContext.getLoggedInEmployeeId();

        var response = feedbackProvidersService
                .getFormsByEmployeeAndReviewer(employeeId, reviewerId);

        if (response == null || response.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(response);
    }
}
