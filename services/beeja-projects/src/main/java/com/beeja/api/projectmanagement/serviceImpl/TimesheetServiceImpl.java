package com.beeja.api.projectmanagement.serviceImpl;


import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.repository.TimesheetRepository;
import com.beeja.api.projectmanagement.service.TimesheetService;
import com.beeja.api.projectmanagement.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;

    private final MongoTemplate mongoTemplate;

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
        existingTimesheet.setClientId(dto.getClientId());
        existingTimesheet.setProjectId(dto.getProjectId());
        existingTimesheet.setContractId(dto.getContractId());
        existingTimesheet.setTimeInMinutes(dto.getTimeInMinutes());
        existingTimesheet.setDescription(dto.getDescription());
        existingTimesheet.setModifiedAt(new Date());
        return timesheetRepository.save(existingTimesheet);
    }

    @Override
    public List<Timesheet> getTimesheets(String day, Integer week, String month) {
        String employeeId = UserContext.getLoggedInEmployeeId();
        String organizationId = (String) UserContext.getLoggedInUserOrganization().get("id");

        Criteria criteria = Criteria.where("employeeId").is(employeeId)
                .and("organizationId").is(organizationId);

        ZoneId zone = ZoneId.systemDefault();

        if (day != null) {
            LocalDate localDate = LocalDate.parse(day);
            Date start = Date.from(localDate.atStartOfDay(zone).toInstant());
            Date end = Date.from(localDate.plusDays(1).atStartOfDay(zone).toInstant());
            criteria.and("startDate").gte(start).lt(end);
        }
        else if (week != null) {
            LocalDate now = LocalDate.now();
            LocalDate startOfWeek = now.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            Date start = Date.from(startOfWeek.atStartOfDay(zone).toInstant());
            Date end = Date.from(endOfWeek.plusDays(1).atStartOfDay(zone).toInstant());
            criteria.and("startDate").gte(start).lt(end);
        }
        else if (month != null) {
            YearMonth yearMonth = YearMonth.parse(month);
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();
            Date start = Date.from(startOfMonth.atStartOfDay(zone).toInstant());
            Date end = Date.from(endOfMonth.plusDays(1).atStartOfDay(zone).toInstant());
            criteria.and("startDate").gte(start).lt(end);
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Timesheet.class);
    }

    @Override
    public void deleteTimesheet(String id) {
        Timesheet existing = timesheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found with ID: " + id));

        timesheetRepository.delete(existing);
    }
}
