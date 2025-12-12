package com.beeja.api.projectmanagement.model.dto;

import lombok.Data;
import java.util.Map;

@Data
public class WeekTimesheetResponse {
    private int weekNumber;
    private int weekYear;
    private String weekStartDate;
    private String weekEndDate;
    private double weeklyTotalHours;
    private Map<String, DayTimesheetResponse> dailyLogs;
}
