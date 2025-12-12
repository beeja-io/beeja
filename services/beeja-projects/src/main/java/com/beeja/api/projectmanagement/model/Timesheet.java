package com.beeja.api.projectmanagement.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Timesheet entity stored in MongoDB.
 * Contains audit fields and compound indexes to prevent duplicate entries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "Timesheet")
public class Timesheet {

    @Id
    private String id;

    @NotBlank
    @Indexed
    private String employeeId;

    @Indexed
    private String organizationId;

    @NotBlank
    private String projectId;

    private String contractId;

    @NotNull
    private Instant startDate;

    @Min(1)
    @Max(24 * 60)
    private int timeInMinutes;

    private String description;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    private String modifiedBy;

    @LastModifiedDate
    private Instant modifiedAt;
}