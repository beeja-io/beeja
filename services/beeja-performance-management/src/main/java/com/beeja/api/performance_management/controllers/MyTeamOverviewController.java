package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.FeedbackResponse;
import com.beeja.api.performance_management.model.GroupedFeedbackResponse;
import com.beeja.api.performance_management.model.OverallRating;
import com.beeja.api.performance_management.model.QuestionAnswer;
import com.beeja.api.performance_management.model.dto.EmployeeCycleInfo;
import com.beeja.api.performance_management.model.dto.OverallRatingRequestDTO;
import com.beeja.api.performance_management.model.dto.QRDTO;
import com.beeja.api.performance_management.model.dto.ReviewerAnswerDTO;
import com.beeja.api.performance_management.service.FeedbackResponseService;
import com.beeja.api.performance_management.service.MyTeamOverviewService;
import com.beeja.api.performance_management.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/overview")
@Validated
@Slf4j
public class MyTeamOverviewController {

    @Autowired
    private FeedbackResponseService responseService;

    @Autowired
    private MyTeamOverviewService myTeamOverviewService;

    @GetMapping("/employee/{employeeId}/cycle/{cycleId}/groupedResponse")
    public ResponseEntity<?> getGroupedResponsesByEmployeeCycle(
            @PathVariable String employeeId,
            @PathVariable String cycleId) {
        try {
            List<FeedbackResponse> responses = responseService.getByEmployeeAndCycle(employeeId, cycleId);
            Map<String, List<ReviewerAnswerDTO>> grouped = new HashMap<>();

            for (FeedbackResponse feedback : responses) {
                if (feedback.getResponses() == null) continue;
                for (QuestionAnswer qa : feedback.getResponses()) {
                    grouped.computeIfAbsent(qa.getQuestionId(), k -> new ArrayList<>())
                            .add(new ReviewerAnswerDTO(feedback.getReviewerId(), qa.getAnswer()));
                }
            }

            List<QRDTO> questions = grouped.entrySet().stream()
                    .map(entry -> {
                        QRDTO dto = new QRDTO();
                        dto.setQuestionId(entry.getKey());
                        dto.setResponses(entry.getValue());
                        return dto;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new GroupedFeedbackResponse(questions));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/employee/{employeeId}/overall-rating")
    public ResponseEntity<?> createOrUpdateOverallRating(
            @PathVariable String employeeId,
            @RequestBody OverallRatingRequestDTO request) {
        try {
            OverallRating saved = myTeamOverviewService.createOrUpdateOverallRating(
                    employeeId,
                    request.getRating(),
                    request.getComments()
            );
            log.info(Constants.FINAL_RATING_COMPUTED_AND_SAVED, employeeId, "N/A");
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error(Constants.ERROR_SAVING_FEEDBACK_RESPONSE_SIMPLE, e);
            return ResponseEntity.internalServerError().body(Constants.ERROR_SAVING_FEEDBACK_RESPONSE_SIMPLE);
        }
    }

    @GetMapping("/employee/{employeeId}/overall-rating")
    public ResponseEntity<?> getOverallRating(@PathVariable String employeeId) {
        try {
            OverallRating rating = myTeamOverviewService.getOverallRatingByEmployeeId(employeeId);
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            log.error(Constants.NO_RATINGS_FOUND_FOR_EMPLOYEE, employeeId);
            return ResponseEntity.internalServerError().body(Constants.NO_RATINGS_FOUND_FOR_EMPLOYEE);
        }
    }

    @DeleteMapping("/employee/{employeeId}/overall-rating")
    public ResponseEntity<?> deleteOverallRating(@PathVariable String employeeId) {
        try {
            myTeamOverviewService.deleteOverallRatingByEmployeeId(employeeId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error(Constants.FINAL_RATING_NOT_FOUND, employeeId);
            return ResponseEntity.internalServerError().body(Constants.FINAL_RATING_NOT_FOUND);
        }
    }

    @GetMapping("/employee/feedbackReceiver/{employeeId}/cycles")
    public ResponseEntity<?> getCycleInfoByEmployeeId(@PathVariable String employeeId) {
        try {
            List<EmployeeCycleInfo> cycles = myTeamOverviewService.getCycleIdsByEmployeeId(employeeId);
            return ResponseEntity.ok(cycles);
        } catch (Exception e) {
            log.error(Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND, employeeId);
            return ResponseEntity.internalServerError().body(Constants.ERROR_EVALUATION_CYCLE_NOT_FOUND);
        }
    }
}
