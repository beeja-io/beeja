package com.beeja.api.performance_management.model.dto;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.enums.CycleType;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.Question;
import com.beeja.api.performance_management.model.Questionnaire;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO representing detailed information about an evaluation cycle,
 * including its metadata and related questionnaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCycleDetailsDto {
    // Cycle fields
    private String id;
    private String name;
    private CycleType type;
    private String formDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate feedbackDeadline;
    private LocalDate selfEvalDeadline;
    private CycleStatus status;

    // Questionnaire fields
    private String questionnaireId;
    private List<Question> questions;

    // Constructor
    public EvaluationCycleDetailsDto(EvaluationCycle cycle, Questionnaire questionnaire) {
        this.id = cycle.getId();
        this.name = cycle.getName();
        this.type = cycle.getType();
        this.formDescription = cycle.getFormDescription();
        this.startDate = cycle.getStartDate();
        this.endDate = cycle.getEndDate();
        this.feedbackDeadline = cycle.getFeedbackDeadline();
        this.selfEvalDeadline = cycle.getSelfEvalDeadline();
        this.status = cycle.getStatus();

        if (questionnaire != null) {
            this.questionnaireId = questionnaire.getId();
            this.questions = questionnaire.getQuestions();
        }
    }
}