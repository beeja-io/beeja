package com.beeja.api.projectmanagement.model.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TimesheetMonthResponse {
    private Map<String, WeekTimesheetResponse> weekTimesheets;
    private double monthlyTotalHours;
}