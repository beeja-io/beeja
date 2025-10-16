package com.beeja.api.performance_management.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "questionnaires")
public class Questionnaire {
    @Id
    private String id;

    @NotEmpty(message = "Questions list cannot be empty")
    private List<@Valid Question> questions;
}