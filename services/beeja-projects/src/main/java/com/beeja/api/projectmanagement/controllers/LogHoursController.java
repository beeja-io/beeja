package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.requests.LogHoursRequest;
import com.beeja.api.projectmanagement.service.LogHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping("/v1/timesheet")
public class LogHoursController {
    @Autowired
    private LogHoursService logHoursService;

    @PostMapping
    public ResponseEntity<String> logHours(@RequestBody LogHoursRequest logHoursRequest) {
        logHoursService.saveLogHours(logHoursRequest);
        return ResponseEntity.ok("Log hours saved successfully.");
    }

    @PutMapping
    public ResponseEntity<String> updateLogHours(@RequestBody LogHoursRequest logHoursRequest) {
        logHoursService.updateLogHours(logHoursRequest);
        return ResponseEntity.ok("Log hours updated successfully.");
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
                return ResponseEntity.badRequest().body("Invalid date format. Expected format: yyyy-MM-dd.");
            }
        } else {
            parsedDate = new Date();
        }

        return ResponseEntity.ok(logHoursService.getLogHoursSummary(employeeId, type, parsedDate, weekNumber));
    }
}
