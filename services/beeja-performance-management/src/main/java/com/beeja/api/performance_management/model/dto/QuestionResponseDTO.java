package com.beeja.api.performance_management.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class QuestionResponseDTO {
    private String questionId;
    private List<String> responses = new ArrayList<>();
}
