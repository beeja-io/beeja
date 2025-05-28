package com.beeja.api.projectmanagement.serviceImpl;


import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.repository.TimesheetRepository;
import com.beeja.api.projectmanagement.service.TimesheetService;
import com.beeja.api.projectmanagement.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;

    @Override
    public Timesheet saveTimesheet(TimesheetRequestDto requestDto) {
        String employeeId = UserContext.getLoggedInEmployeeId();
        String organizationId = (String) UserContext.getLoggedInUserOrganization().get("id");
        Timesheet timesheet = Timesheet.builder()
                .employeeId(employeeId)
                .organizationId(organizationId)
                .clientId(requestDto.getClientId())
                .projectId(requestDto.getProjectId())
                .contractId(requestDto.getContractId())
                .startDate(requestDto.getStartDate())
                .timeInMinutes(requestDto.getTimeInMinutes())
                .description(requestDto.getDescription())
                .createdAt(new Date())
                .createdBy(employeeId)
                .build();
        return timesheetRepository.save(timesheet);
    }
    @Override
    public Timesheet updateLog(TimesheetRequestDto dto,String Id) {
        Optional<Timesheet> existing = timesheetRepository.findById(Id);
        Timesheet existingTimesheet=existing.get();
        existingTimesheet.setTimeInMinutes(dto.getTimeInMinutes());
        existingTimesheet.setDescription(dto.getDescription());
        existingTimesheet.setModifiedAt(new Date());
        return timesheetRepository.save(existingTimesheet);
    }
    
}
