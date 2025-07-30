package com.beeja.api.performance_management.requests;

import com.beeja.api.performance_management.model.Questions;
import lombok.Data;

import java.util.List;

@Data
public class CreateReviewFormRequest {
    private String description;
    List<Questions> questionsList;
}
