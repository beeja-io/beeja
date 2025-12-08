package com.beeja.api.projectmanagement.model.dto;

import lombok.Data;
import java.util.List;
import com.beeja.api.projectmanagement.model.Timesheet;

@Data
public class DayTimesheetResponse {
    private String date;
    private double dayTotalHours;
    private List<Timesheet> timesheets;
}