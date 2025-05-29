package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.CustomPageResponse;
import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.repository.TimesheetRepository;
import com.beeja.api.projectmanagement.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    private final TimesheetService timesheetService;

    @Autowired
    TimesheetRepository timesheetRepository;

    @PostMapping
    public ResponseEntity<Timesheet> saveTimesheet(@RequestBody TimesheetRequestDto requestDto) {
        Timesheet saved = timesheetService.saveTimesheet(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{Id}")
    public Timesheet updateLog(@RequestBody TimesheetRequestDto dto, @PathVariable String Id) {
        return timesheetService.updateLog(dto, Id);
    }

    @GetMapping
    public ResponseEntity<List<Timesheet>> getTimesheets(
            @RequestParam(required = false) String day,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) String month
    ) {
        List<Timesheet> timesheets = timesheetService.getTimesheets(day, week, month);
        return ResponseEntity.ok(timesheets);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTimesheet(@PathVariable String id) {
        timesheetService.deleteTimesheet(id);
        return ResponseEntity.ok("Timesheet deleted successfully");

    }
}
