package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.constants.LogHoursConstants;
import com.beeja.api.projectmanagement.requests.LogHoursRequest;
import com.beeja.api.projectmanagement.service.LogHoursService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.beeja.api.projectmanagement.constants.LogHoursConstants.INVALID_DATE_FORMAT;


@Controller
@RequestMapping("/v1/timesheet")
public class LogHoursController {
    @Autowired
    private LogHoursService logHoursService;

    @PostMapping
    public ResponseEntity<String> logHours(@Valid @RequestBody LogHoursRequest logHoursRequest, BindingResult result)
           throws MethodArgumentNotValidException {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null,result);
        }
        logHoursService.saveLogHours(logHoursRequest);
        return ResponseEntity.ok(LogHoursConstants.LOG_HOURS_SAVED);
  }

    @PutMapping
    public ResponseEntity<String> updateLogHours( @Valid @RequestBody LogHoursRequest logHoursRequest) {
        logHoursService.updateLogHours(logHoursRequest);
        return ResponseEntity.ok(LogHoursConstants.LOG_HOURS_UPDATED);
    }

    @GetMapping("/summary")
    public ResponseEntity<Object> getLogHoursSummary(
            @RequestParam String employeeId,
            @RequestParam String type,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer weekNumber) {

        Date parsedDate = null;
        if (date != null) {
            try {
                parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body(INVALID_DATE_FORMAT);
            }
        } else {
            parsedDate = new Date();
        }

        return ResponseEntity.ok(logHoursService.getLogHoursSummary(employeeId, type, parsedDate, weekNumber));
    }
}
