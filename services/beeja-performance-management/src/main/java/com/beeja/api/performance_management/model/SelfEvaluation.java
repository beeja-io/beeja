package com.beeja.api.performance_management.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "self_evaluations")
public class SelfEvaluation {
    @Id
    private String id;

    private String organizationId;

    private String employeeId;

    private String submittedBy;

    private Instant submittedAt;

    private boolean submitted;

    private List<QuestionAnswer> responses;
}
