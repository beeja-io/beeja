package com.beeja.api.performance_management.model;

import com.beeja.api.performance_management.enums.Department;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "questionnaires")
public class Questionnaire {
    @Id
    private String id;

    @NotNull(message = "Department is required")
    private Department department;

    @NotEmpty(message = "Questions list cannot be empty")
    private List<@Valid Question> questions;
}
