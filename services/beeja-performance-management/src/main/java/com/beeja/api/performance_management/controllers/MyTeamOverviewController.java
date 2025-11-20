package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.Constants.PermissionConstants;
import com.beeja.api.performance_management.annotations.HasPermission;
import com.beeja.api.performance_management.client.AccountClient;
import com.beeja.api.performance_management.model.FeedbackResponse;
import com.beeja.api.performance_management.model.GroupedFeedbackResponse;
import com.beeja.api.performance_management.model.OverallRating;
import com.beeja.api.performance_management.model.QuestionAnswer;
import com.beeja.api.performance_management.model.dto.*;
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

    @Autowired
    private AccountClient accountClient;

    @GetMapping("/employees")
    @HasPermission(PermissionConstants.READ_ALL_RESPONSES)
    public ResponseEntity<PaginatedEmployeePerformanceResponse> getEmployeePerformanceData(
            @RequestParam(name = "department", required = false) String department,
            @RequestParam(name = "designation", required = false) String designation,
            @RequestParam(name = "employmentType", required = false) String employmentType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        PaginatedEmployeePerformanceResponse result =
                myTeamOverviewService.getEmployeePerformanceData(
                        department, designation, employmentType, status, pageNumber, pageSize);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/employee/{employeeId}/cycle/{cycleId}/groupedResponse")
    @HasPermission(PermissionConstants.READ_ALL_RESPONSES)
    public ResponseEntity<?> getGroupedResponsesByEmployeeCycle(
            @PathVariable String employeeId,
            @PathVariable String cycleId) {
        try {

            List<FeedbackResponse> responses =
                    responseService.getByEmployeeAndCycle(employeeId, cycleId);

            Map<String, List<ReviewerAnswerDTO>> grouped = new LinkedHashMap<>();
            Map<String, String> questionDescriptions = new LinkedHashMap<>();

            for (FeedbackResponse feedback : responses) {

                if (feedback.getResponses() == null) continue;

                EmployeeName en = accountClient.getEmployeeName(feedback.getReviewerId());
                String fullName = en.getFirstName() + " " + en.getLastName();

                for (QuestionAnswer qa : feedback.getResponses()) {

                    questionDescriptions.putIfAbsent(qa.getQuestionId(), qa.getDescription());

                    grouped.computeIfAbsent(qa.getQuestionId(), k -> new ArrayList<>())
                            .add(new ReviewerAnswerDTO(fullName, qa.getAnswer()));
                }
            }

            List<QRDTO> questions = grouped.entrySet().stream()
                    .map(entry -> {
                        String questionId = entry.getKey();
                        String description = questionDescriptions.getOrDefault(questionId, "");

                        QRDTO dto = new QRDTO();
                        dto.setQuestionId(questionId);
                        dto.setDescription(description);
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
    @HasPermission(PermissionConstants.PROVIDE_RATING)
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
    @HasPermission(PermissionConstants.READ_ALL_RESPONSES)
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
