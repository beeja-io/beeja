package com.beeja.api.projectmanagement.requests;

import com.beeja.api.projectmanagement.model.LogHours.LogHours;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class LogHoursRequest {
    @NotBlank
    private String employeeId;
    private List<LogHours> logHours;
}
