package com.beeja.api.projectmanagement.requests;

import com.beeja.api.projectmanagement.model.LogHours.LogHours;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class LogHoursRequest {
    @NotBlank(message = "Employee ID cannot be empty.")
    private String employeeId;

   @NotNull(message = "Log hours list cannot be null.")
   @Valid
    private List<LogHours> logHours;
}
