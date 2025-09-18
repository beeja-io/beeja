package com.beeja.api.performance_management.model;

import com.beeja.api.performance_management.enums.ReviewType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude
@Document(collection = "review_cycles")
public class ReviewCycle {

    @Id
    private String id;

    @Indexed(unique = true)
    private String cycleId;

    private String name;
    private String description;

    private String reviewFormId;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status;

    private List<String> managerIds;
    private List<String> employeeIds;

    private String organizationId;
    private String createdBy;

    private ReviewType reviewType;
}
