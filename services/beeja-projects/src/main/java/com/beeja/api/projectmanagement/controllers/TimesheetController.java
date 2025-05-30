package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.annotations.HasPermission;
import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.model.CustomPageResponse;
import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    @Autowired
    private TimesheetService timesheetService;


    @PostMapping
    @HasPermission(PermissionConstants.CREATE_TIMESHEET)
    public ResponseEntity<Timesheet> saveTimesheet(@RequestBody TimesheetRequestDto requestDto) {
        Timesheet saved = timesheetService.saveTimesheet(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{Id}")
    @HasPermission(PermissionConstants.UPDATE_TIMESHEET)
    public Timesheet updateLog(@RequestBody TimesheetRequestDto dto, @PathVariable String Id) {
        return timesheetService.updateLog(dto, Id);
    }

    @GetMapping
    @HasPermission(PermissionConstants.GET_TIMESHEET)
    public ResponseEntity<?> getTimesheets(
            @RequestParam(required = false) String day,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String employeeId
    ) {
        if (month != null && day == null && week == null) {
            Map<String, Object> grouped = timesheetService.getTimesheetsGroupedByWeek(month);
            return ResponseEntity.ok(grouped);
        } else {
            Page<Timesheet> pageResult = timesheetService.getTimesheets(day, week, month, employeeId,page, size);
            CustomPageResponse<Timesheet> response = new CustomPageResponse<>(
                    pageResult.getContent(),
                    pageResult.getNumber(),
                    pageResult.getSize(),
                    pageResult.getTotalElements(),
                    pageResult.getTotalPages()
            );
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/{id}")
    @HasPermission(PermissionConstants.DELETE_TIMESHEET)
    public ResponseEntity<String> deleteTimesheet(@PathVariable String id) {
        timesheetService.deleteTimesheet(id);
        return ResponseEntity.ok("Timesheet deleted successfully");
    }
}
