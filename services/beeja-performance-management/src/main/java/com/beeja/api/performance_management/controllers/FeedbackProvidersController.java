package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.FeedbackProvider;
import com.beeja.api.performance_management.request.FeedbackProviderRequest;
import com.beeja.api.performance_management.response.FeedbackProviderDetails;
import com.beeja.api.performance_management.service.FeedbackProvidersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("feedbackProvider")
public class FeedbackProvidersController {

    @Autowired
    FeedbackProvidersService feedbackProvidersService;

    @PostMapping("/assign")
    public ResponseEntity<List<FeedbackProvider>> assignFeedbackProvider(@RequestBody FeedbackProviderRequest requestDto) {
        List<FeedbackProvider> assignProvider = feedbackProvidersService.assignFeedbackProvider(requestDto);
        return ResponseEntity.ok(assignProvider);
    }

    @PutMapping("/providers/{employeeId}")
    public ResponseEntity<List<FeedbackProvider>> updateFeedbackProviders(
            @PathVariable String employeeId,
            @RequestBody FeedbackProviderRequest request) {
        List<FeedbackProvider> updatedForms = feedbackProvidersService.updateFeedbackProviders(request, employeeId);

        return ResponseEntity.ok(updatedForms);
    }

    @GetMapping("/{employeeId}/{cycleId}")
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
}
