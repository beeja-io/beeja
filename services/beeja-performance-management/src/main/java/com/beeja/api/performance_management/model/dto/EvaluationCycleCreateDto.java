package com.beeja.api.performance_management.model.dto;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.enums.CycleType;
import com.beeja.api.performance_management.model.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for creating a new Evaluation Cycle along with its questionnaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCycleCreateDto {

    // EvaluationCycle fields
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Cycle type is required")
    private CycleType type;

    private String formDescription;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Feedback deadline is required")
    private LocalDate feedbackDeadline;

    @NotNull(message = "Self-evaluation deadline is required")
    private LocalDate selfEvalDeadline;

    private CycleStatus status;

    // Questionnaire fields
    private List<@NotNull Question> questions;
}
