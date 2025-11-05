package com.beeja.api.performance_management.model.dto;

import com.beeja.api.performance_management.model.QuestionAnswer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SubmitFeedbackRequest {
    @NotBlank
    private String employeeId;

    private String organizationId;

    private String formId;

    @NotBlank
    private String cycleId;

    @NotBlank
    private String reviewerId;

    @NotBlank
    private String reviewerRole;

    @NotEmpty
    private List<QuestionAnswer> responses;
}
