package com.beeja.api.performance_management.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "overall_ratings")
public class OverallRating {

    @Id
    private String id;

    private String organizationId;

    @Indexed
    private String employeeId;

    private Double rating;

    @NotNull
    private String comments;

    private String givenBy;

    private Boolean published = false;

    private Instant publishedAt;
}

