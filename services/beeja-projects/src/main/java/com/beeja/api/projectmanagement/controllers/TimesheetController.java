package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.repository.TimesheetRepository;
import com.beeja.api.projectmanagement.service.TimesheetService;
import jakarta.ws.rs.GET;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    @PutMapping("{Id}")
    public Timesheet updateLog(@RequestBody TimesheetRequestDto dto, @PathVariable String Id) {
        return timesheetService.updateLog(dto, Id); // Replace with authenticated user
    }
    
    @GetMapping
    public List<Timesheet> gettime(){
        return  timesheetRepository.findAll();
    }

}
