package com.beeja.api.performance_management.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewerAnswerDTO {
    private String reviewerId;
    private String answer;

    public ReviewerAnswerDTO(String reviewerId, String answer) {
        this.reviewerId = reviewerId;
        this.answer = answer;
    }

}
