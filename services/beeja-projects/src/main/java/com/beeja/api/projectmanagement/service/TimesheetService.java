package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TimesheetService {

    Timesheet saveTimesheet(TimesheetRequestDto requestDto);

    Timesheet updateLog(TimesheetRequestDto dto,String Id);

    List<Timesheet> getTimesheets(String day, Integer week, String month);

    void deleteTimesheet(String id);
}
