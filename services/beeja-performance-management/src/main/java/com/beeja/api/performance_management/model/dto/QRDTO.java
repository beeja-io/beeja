package com.beeja.api.performance_management.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRDTO {
    private String questionId;
    private String description;
    private List<ReviewerAnswerDTO> responses;

}
