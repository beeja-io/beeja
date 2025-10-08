package com.beeja.api.performance_management.model;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.enums.CycleType;
import com.beeja.api.performance_management.enums.Department;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "evaluation_cycles")
public class EvaluationCycle {
    @Id
    private String id;

    @NotNull
    private String name;

    @NotNull
    @Indexed
    private CycleType type;

    @NotNull
    @Indexed
    private LocalDate startDate;

    @NotNull
    @Indexed
    private LocalDate endDate;

    @NotNull
    private LocalDate selfEvalDeadline;

    @NotNull
    private LocalDate feedbackDeadline;

    @NotNull
    private CycleStatus status = CycleStatus.DRAFT;

    @NotNull(message = "Department is required")
    private Department department;

    private String questionnaireId;
}