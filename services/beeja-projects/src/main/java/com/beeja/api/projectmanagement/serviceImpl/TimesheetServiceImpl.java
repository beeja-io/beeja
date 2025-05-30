package com.beeja.api.projectmanagement.serviceImpl;


import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.TimesheetRequestDto;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.repository.TimesheetRepository;
import com.beeja.api.projectmanagement.service.TimesheetService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TimesheetServiceImpl implements TimesheetService {

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ContractRepository contractRepository;

    private final MongoTemplate mongoTemplate;

    @Override
    public Timesheet saveTimesheet(TimesheetRequestDto requestDto) {
        String employeeId = UserContext.getLoggedInEmployeeId();
        String organizationId = (String) UserContext.getLoggedInUserOrganization().get("id");

        if (!projectRepository.existsByProjectId(requestDto.getProjectId())) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.PROJECT_NOT_FOUND
                    )
            );
        }

        if (!contractRepository.existsByContractId(requestDto.getContractId())) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.CONTRACT_NOT_FOUND
                    )
            );
        }

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
    public Page<Timesheet> getTimesheets(String day, Integer week, String month, String employeeId,int page, int size) {
        String loggedInEmployeeId = UserContext.getLoggedInEmployeeId();
        String organizationId = (String) UserContext.getLoggedInUserOrganization().get("id");

        String employeeIdToUse;
        if (employeeId != null && !employeeId.isEmpty()) {
            if (UserContext.hasPermission(PermissionConstants.READ_ALL_TIMESHEETS)) {

                employeeIdToUse = employeeId;
            } else {
                employeeIdToUse = loggedInEmployeeId;
            }
        } else {
            employeeIdToUse = loggedInEmployeeId;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate"));
        Criteria criteria = Criteria.where("employeeId").is(employeeIdToUse)
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

        Query query = new Query(criteria).with(pageable);
        List<Timesheet> results = mongoTemplate.find(query, Timesheet.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Timesheet.class);

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Map<String, Object> getTimesheetsGroupedByWeek(String month) {
        String employeeId = UserContext.getLoggedInEmployeeId();
        String organizationId = (String) UserContext.getLoggedInUserOrganization().get("id");

        YearMonth yearMonth = YearMonth.parse(month);
        ZoneId zoneId = ZoneId.systemDefault();

        Date startDate = Date.from(yearMonth.atDay(1).atStartOfDay(zoneId).toInstant());
        Date endDate = Date.from(yearMonth.atEndOfMonth().plusDays(1).atStartOfDay(zoneId).toInstant());

        Criteria criteria = Criteria.where("employeeId").is(employeeId)
                .and("organizationId").is(organizationId)
                .and("startDate").gte(startDate).lt(endDate);

        List<Timesheet> timesheets = mongoTemplate.find(
                new Query(criteria).with(Sort.by(Sort.Direction.DESC, "startDate")),
                Timesheet.class
        );

        WeekFields weekFields = WeekFields.ISO;
        Map<String, Map<String, Object>> weekMap = new TreeMap<>();
        double totalMonthHours = 0.0;

        for (Timesheet ts : timesheets) {
            if (ts.getStartDate() == null)
                continue;

            LocalDate localDate = ts.getStartDate().toInstant().atZone(zoneId).toLocalDate();
            int weekNumber = localDate.get(weekFields.weekOfWeekBasedYear());
            int year = localDate.get(weekFields.weekBasedYear());
            String weekKey = "week-" + weekNumber;

            Map<String, Object> weekData = weekMap.computeIfAbsent(weekKey, k -> {
                Map<String, Object> map = new HashMap<>();
                map.put("timesheets", new ArrayList<Timesheet>());
                map.put("totalHours", 0.0);


                LocalDate weekStart = LocalDate.ofYearDay(year, 1).with(weekFields.weekOfWeekBasedYear(), weekNumber).with(weekFields.dayOfWeek(), 1);
                LocalDate weekEnd = weekStart.plusDays(6);

                map.put("weekStartDate", weekStart.toString());
                map.put("weekEndDate", weekEnd.toString());
                return map;
            });

            ((List<Timesheet>) weekData.get("timesheets")).add(ts);

            double hours = ts.getTimeInMinutes() / 60.0;
            totalMonthHours += hours;

            double weekTotal = (double) weekData.get("totalHours");
            weekData.put("totalHours", Math.round((weekTotal + hours) * 10.0) / 10.0);
        }

        Map<String, Object> finalResponse = new LinkedHashMap<>();
        finalResponse.put("weekTimesheets", weekMap);
        finalResponse.put("monthlyTotalHours", Math.round(totalMonthHours * 10.0) / 10.0);

        return finalResponse;
    }

    @Override
    public void deleteTimesheet(String id) {
        Timesheet existing = timesheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found with ID: " + id));

        timesheetRepository.delete(existing);
    }
}
