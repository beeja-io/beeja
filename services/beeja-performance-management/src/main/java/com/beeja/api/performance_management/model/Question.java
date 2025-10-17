package com.beeja.api.performance_management.model;

import com.beeja.api.performance_management.enums.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @NotBlank(message = "Question cannot be blank")
    private String question;

    @NotBlank(message = "Question description cannot be blank")
    private String questionDescription;

    @NotNull(message = "Target type is required")
    private TargetType target;

    private boolean required = false;
}