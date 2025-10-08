package com.beeja.api.performance_management.model.dto;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.enums.CycleType;
import com.beeja.api.performance_management.enums.Department;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.Question;
import com.beeja.api.performance_management.model.Questionnaire;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCycleDetailsDto {
    // Cycle fields
    private String id;
    private String name;
    private CycleType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate selfEvalDeadline;
    private LocalDate feedbackDeadline;
    private CycleStatus status;
    private Department department;

    // Questionnaire fields
    private String questionnaireId;
    private List<Question> questions;

    // Constructors
    public EvaluationCycleDetailsDto(EvaluationCycle cycle, Questionnaire questionnaire) {
        this.id = cycle.getId();
        this.name = cycle.getName();
        this.type = cycle.getType();
        this.startDate = cycle.getStartDate();
        this.endDate = cycle.getEndDate();
        this.selfEvalDeadline = cycle.getSelfEvalDeadline();
        this.feedbackDeadline = cycle.getFeedbackDeadline();
        this.status = cycle.getStatus();
        this.department = cycle.getDepartment();

        if (questionnaire != null) {
            this.questionnaireId = questionnaire.getId();
            this.questions = questionnaire.getQuestions();
        }
    }
}