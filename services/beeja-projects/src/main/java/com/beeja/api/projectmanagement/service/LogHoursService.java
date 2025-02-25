package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.requests.LogHoursRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public interface LogHoursService {

    void saveLogHours(LogHoursRequest logHoursRequest);
    void updateLogHours(LogHoursRequest logHoursRequest);
    Map<String, Object> getLogHoursSummary(String employeeId, String type, Date date, Integer weekNumber);
}

