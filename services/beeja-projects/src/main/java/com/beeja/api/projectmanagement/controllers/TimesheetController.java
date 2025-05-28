package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    private final TimesheetService timesheetService;

    @PostMapping("/send")
    public ResponseEntity<Timesheet> saveTimesheet(@RequestBody TimesheetRequestDto requestDto) {
        Timesheet saved = timesheetService.saveTimesheet(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
