package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import org.springframework.data.domain.Page;

public interface TimesheetService {

    Timesheet saveTimesheet(TimesheetRequestDto requestDto);

    Timesheet updateLog(TimesheetRequestDto dto,String Id);

    Page<Timesheet> getTimesheets(String day, Integer week, String month, int page, int size);

    void deleteTimesheet(String id);
}
