package com.beeja.api.performance_management.model.dto;

import com.beeja.api.performance_management.model.QuestionAnswer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class SelfEvaluationRequest {
    @NotBlank
    private String employeeId;

    @NotBlank
    private String submittedBy;

    @NotEmpty
    private List<QuestionAnswer> responses;
}
