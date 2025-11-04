package com.beeja.api.performance_management.model.dto;

import com.beeja.api.performance_management.model.EvaluationCycle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeGroupedResponsesDTO {
    private EvaluationCycle evaluationCycle;
    private List<QuestionResponseDTO> questions;
}