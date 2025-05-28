package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;

public interface TimesheetService {
    Timesheet saveTimesheet(TimesheetRequestDto requestDto);
    Timesheet updateLog(TimesheetRequestDto dto,String Id);
}
