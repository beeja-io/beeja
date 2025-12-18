package com.beeja.api.projectmanagement.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for creating/updating a timesheet.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetRequestDto {

    @NotBlank
    private String projectId;

    private String contractId;

    private Instant startDate;

    @Min(value = 1)
    @Max(value = 1440)
    private int timeInMinutes;

    private String description;
}