package com.beeja.api.performance_management.controllers;


import com.beeja.api.performance_management.Constants.PermissionConstants;
import com.beeja.api.performance_management.annotations.HasPermission;
import com.beeja.api.performance_management.model.FeedbackReceivers;
import com.beeja.api.performance_management.request.ReceiverRequest;
import com.beeja.api.performance_management.service.FeedbackReceiversService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/feedbackReceivers")
public class FeedbackReceiversController {

    @Autowired
    private FeedbackReceiversService feedbackReceiversService;

    @PostMapping
    @HasPermission(PermissionConstants.ASSIGN_RECEIVERS)
    public ResponseEntity<List<FeedbackReceivers>> addFeedbackReceivers(@RequestBody ReceiverRequest receiverRequest) {
        List<FeedbackReceivers> savedReceivers = feedbackReceiversService.addFeedbackReceivers(receiverRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReceivers);
    }

    @PutMapping("/cycles/{cycleId}")
    @HasPermission(PermissionConstants.UPDATE_RECEIVERS)
    public ResponseEntity<List<FeedbackReceivers>> updateReceivers(
            @PathVariable String cycleId,
            @RequestBody ReceiverRequest receiverRequest) {
        List<FeedbackReceivers> updated = feedbackReceiversService.updateFeedbackReceivers(cycleId, receiverRequest);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{cycleId}/{questionnaireId}")
    @HasPermission(PermissionConstants.READ_RECEIVERS)
    public ResponseEntity<?> getFeedbackReceivers(
            @PathVariable String cycleId,
            @PathVariable String questionnaireId) {
        return ResponseEntity.ok(feedbackReceiversService.getFeedbackReceiversList(cycleId, questionnaireId));
    }

}
