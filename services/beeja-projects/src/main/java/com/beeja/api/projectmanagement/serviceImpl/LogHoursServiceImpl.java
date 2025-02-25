package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.constants.LogHoursConstants;
import com.beeja.api.projectmanagement.enums.LogHourEnum;
import com.beeja.api.projectmanagement.model.LogHours.LogHours;
import com.beeja.api.projectmanagement.model.LogHours.Timesheet;
import com.beeja.api.projectmanagement.repository.LogHoursRepository;
import com.beeja.api.projectmanagement.requests.LogHoursRequest;
import com.beeja.api.projectmanagement.service.LogHoursService;
import com.beeja.api.projectmanagement.utils.UserContext;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class LogHoursServiceImpl implements LogHoursService {

    @Autowired
    private LogHoursRepository logHoursRepository;

    @Autowired
    private AccountClient accountClient;

    @Override
    public void saveLogHours(LogHoursRequest logHoursRequest) {

        if (logHoursRequest.getEmployeeId() == null || logHoursRequest.getEmployeeId().isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be blank.");
        }
        if (logHoursRequest.getLogHours() == null || logHoursRequest.getLogHours().isEmpty()) {
            throw new IllegalArgumentException("Log hours list cannot be empty.");
        }

        ResponseEntity<?> response = accountClient.getEmployeeById(logHoursRequest.getEmployeeId());
        if (response.getStatusCode().is4xxClientError() || response.getBody() == null) {
            throw new IllegalArgumentException("Employee ID is not registered in the accounts system.");
        }

        for (LogHours logHours : logHoursRequest.getLogHours()) {
            validateLogHourEntry(logHours);
        }
        String employeeId = logHoursRequest.getEmployeeId();
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        Timesheet timesheet = logHoursRepository.findByEmployeeIdAndOrganizationId(employeeId, organizationId);

        if (timesheet == null) {
            timesheet = new Timesheet();
            timesheet.setEmployeeId(employeeId);
            timesheet.setOrganizationId(organizationId);
            timesheet.setLogHours(new ArrayList<>());
        }
        List<LogHours> allLogHours = new ArrayList<>(timesheet.getLogHours());
        allLogHours.addAll(logHoursRequest.getLogHours());

        validateTotalHoursPerDay(allLogHours);

        timesheet.getLogHours().addAll(logHoursRequest.getLogHours());
        logHoursRepository.save(timesheet);
    }

    private void validateLogHourEntry(LogHours logHours) {
        if (logHours.getProjectId() == null || logHours.getProjectId().isBlank()) {
            throw new IllegalArgumentException("Project ID cannot be blank.");
        }
        if (logHours.getContractId() == null || logHours.getContractId().isBlank()) {
            throw new IllegalArgumentException("Contract ID cannot be blank.");
        }
        if (logHours.getLoghour() == null || logHours.getLoghour().isBlank()) {
            throw new IllegalArgumentException("Log hour cannot be blank.");
        }
        if (!LogHourEnum.isValid(logHours.getLoghour())) {
            throw new IllegalArgumentException("Invalid log hour format. Allowed values: " + LogHourEnum.getAllowedValues());
        }
        if (logHours.getDate() == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
    }

    private void validateTotalHoursPerDay(List<LogHours> allLogHours) {
        Map<Date, Integer> totalMinutesPerDay = new HashMap<>();
        for (LogHours logHours : allLogHours) {
            int minutes = convertToMinutes(logHours.getLoghour());
            totalMinutesPerDay.put(logHours.getDate(),
                    totalMinutesPerDay.getOrDefault(logHours.getDate(), 0) + minutes);
        }
        for (Map.Entry<Date, Integer> entry : totalMinutesPerDay.entrySet()) {
            if (entry.getValue() > 1440) {
                throw new IllegalArgumentException(
                        "Total logged hours for " + entry.getKey() + " exceed 24 hours."
                );
            }
        }
    }

    private int convertToMinutes(String logHour) {
        String[] parts = logHour.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return (hours * 60) + minutes;
    }

    @Override
    public void updateLogHours(LogHoursRequest logHoursRequest) {
        if (logHoursRequest.getEmployeeId() == null || logHoursRequest.getEmployeeId().isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be blank.");
        }
        if (logHoursRequest.getLogHours() == null || logHoursRequest.getLogHours().isEmpty()) {
            throw new IllegalArgumentException("Log hours list cannot be empty.");
        }

        ResponseEntity<?> response = accountClient.getEmployeeById(logHoursRequest.getEmployeeId());
        if (response.getStatusCode().is4xxClientError() || response.getBody() == null) {
            throw new IllegalArgumentException("Employee ID is not registered in the accounts system.");
        }

        for (LogHours logHours : logHoursRequest.getLogHours()) {
            logHours.validateLogHour();
        }
        String employeeId = logHoursRequest.getEmployeeId();
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        Timesheet timesheet = logHoursRepository.findByEmployeeIdAndOrganizationId(employeeId, organizationId);

        if (timesheet == null) {
            throw new IllegalArgumentException(String.format(LogHoursConstants.TIMESHEET_NOT_FOUND_FOR_EMPLOYEE, employeeId, organizationId));
        }

        List<LogHours> existingLogHours = timesheet.getLogHours();

        for (LogHours logHour : logHoursRequest.getLogHours()) {
            boolean exists = false;

            for (LogHours existingLog : existingLogHours) {
                if (existingLog.getDate().equals(logHour.getDate()) &&
                        existingLog.getProjectId().equals(logHour.getProjectId()) &&
                        existingLog.getContractId().equals(logHour.getContractId())) {

                    if (!existingLog.getLoghour().equals(logHour.getLoghour())) {
                        existingLogHours.add(logHour);
                    } else {
                        existingLog.setDescription(logHour.getDescription());
                        existingLog.setLoghour(logHour.getLoghour());
                    }
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                existingLogHours.add(logHour);
            }
        }
        List<LogHours> allLogHours = new ArrayList<>(timesheet.getLogHours());
        allLogHours.addAll(logHoursRequest.getLogHours());

        validateTotalHoursPerDay(allLogHours);
        logHoursRepository.save(timesheet);
    }

    @Override
    public Map<String, Object> getLogHoursSummary(String employeeId, String type, Date date, Integer weekNumber) {
        LocalDate givenDate = date != null ? convertToLocalDate(date) : LocalDate.now();

        Map<String, Object> summary = new HashMap<>();

        if ("day".equalsIgnoreCase(type)) {
            return getDailyLogHours(employeeId, givenDate);
        } else if ("week".equalsIgnoreCase(type)) {
            return getWeeklyLogHours(employeeId, givenDate, weekNumber);
        } else if ("month".equalsIgnoreCase(type)) {
            return getMonthlyLogHours(employeeId, givenDate);
        }

        summary.put("error", LogHoursConstants.INVALID_TYPE_PARAMETER );
        return summary;
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Map<String, Object> getDailyLogHours(String employeeId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        Date start = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

        List<Timesheet> timesheets = logHoursRepository.findByEmployeeIdAndLogDateRange(employeeId, start, end);

        List<Map<String, Object>> logEntries = new ArrayList<>();
        double totalHours = 0;

        for (Timesheet timesheet : timesheets) {
            for (LogHours log : timesheet.getLogHours()) {
                @NotNull Date logDate = log.getDate();
                LocalDate logLocalDate = convertToLocalDate(logDate);

                if (logLocalDate.equals(date)) {
                    double logHourValue = parseLogHour(log.getLoghour());
                    totalHours += logHourValue;

                    Map<String, Object> logEntry = new HashMap<>();
                    logEntry.put("projectId", log.getProjectId());
                    logEntry.put("contractId", log.getContractId());
                    logEntry.put("description", log.getDescription());
                    logEntry.put("loggedHours", logHourValue);
                    logEntries.add(logEntry);
                }
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("totalHours", totalHours);
        response.put("logEntries", logEntries);

        return response;
    }


    private double parseLogHour(String loghour) {
        if (loghour == null || loghour.isEmpty()) {
            return 0.0;
        }

        try {
            String[] parts = loghour.split(":");
            if (parts.length == 2) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                return hours + (minutes / 60.0); // Convert to hours
            }
        } catch (NumberFormatException e) {
            System.out.println(String.format(LogHoursConstants.INVALID_LOG_HOUR_FORMAT, loghour));
        }

        return 0.0;
    }


    private Map<String, Object> getWeeklyLogHours(String employeeId, LocalDate givenDate, Integer weekNumber) {
        if (weekNumber == null) {
            weekNumber = givenDate.get(WeekFields.ISO.weekOfYear());
        }

        WeekFields weekFields = WeekFields.ISO;
        LocalDate startOfWeek = givenDate.with(weekFields.weekOfYear(), weekNumber).with(weekFields.dayOfWeek(), 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        Date start = Date.from(startOfWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endOfWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        List<Timesheet> timesheets = logHoursRepository.findByEmployeeIdAndLogDateRange(employeeId, start, end);

        List<Map<String, Object>> dailyLogs = new ArrayList<>();
        double totalWeekHours = 0;

        for (Timesheet timesheet : timesheets) {
            for (LogHours log : timesheet.getLogHours()) {
                LocalDate logDate = log.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if ((logDate.isEqual(startOfWeek) || logDate.isAfter(startOfWeek)) && logDate.isBefore(endOfWeek.plusDays(1))) {
                    double logHourValue = parseLogHour(log.getLoghour());
                    totalWeekHours += logHourValue;

                    Map<String, Object> logEntry = new HashMap<>();
                    logEntry.put("projectId", log.getProjectId());
                    logEntry.put("contractId", log.getContractId());
                    logEntry.put("description", log.getDescription());
                    logEntry.put("loggedHours", logHourValue);
                    logEntry.put("logDate", logDate);
                    dailyLogs.add(logEntry);
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("weekNumber", weekNumber);
        response.put("startOfWeek", startOfWeek);
        response.put("endOfWeek", endOfWeek);
        response.put("totalWeekHours", totalWeekHours);
        response.put("dailyLogs", dailyLogs);

        return response;
    }

    private Map<String, Object> getMonthlyLogHours(String employeeId, LocalDate givenDate) {
        YearMonth yearMonth = YearMonth.from(givenDate);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        Date start = Date.from(startOfMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endOfMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        List<Timesheet> timesheets = logHoursRepository.findByEmployeeIdAndLogDateRange(employeeId, start, end);

        List<Map<String, Object>> weeklyLogs = new ArrayList<>();
        double totalMonthHours = 0;

        for (Timesheet timesheet : timesheets) {
            for (LogHours log : timesheet.getLogHours()) {
                LocalDate logDate = log.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if ((logDate.isEqual(startOfMonth) || logDate.isAfter(startOfMonth)) && logDate.isBefore(endOfMonth.plusDays(1))) {
                    double logHourValue = parseLogHour(log.getLoghour());
                    totalMonthHours += logHourValue;

                    int weekNumber = logDate.get(WeekFields.ISO.weekOfYear());
                    Map<String, Object> weekLog = weeklyLogs.stream()
                            .filter(w -> w.get("weekNumber").equals(weekNumber))
                            .findFirst()
                            .orElse(null);

                    if (weekLog == null) {
                        weekLog = new HashMap<>();
                        weekLog.put("weekNumber", weekNumber);
                        weekLog.put("totalHours", 0.0);
                        weekLog.put("logs", new ArrayList<>());
                        weeklyLogs.add(weekLog);
                    }


                    List<Map<String, Object>> logs = (List<Map<String, Object>>) weekLog.get("logs");
                    Map<String, Object> logEntry = new HashMap<>();
                    logEntry.put("projectId", log.getProjectId());
                    logEntry.put("contractId", log.getContractId());
                    logEntry.put("description", log.getDescription());
                    logEntry.put("loggedHours", logHourValue);
                    logs.add(logEntry);

                    double currentWeekTotal = (double) weekLog.get("totalHours");
                    weekLog.put("totalHours", currentWeekTotal + logHourValue);
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("month", yearMonth);
        response.put("totalMonthHours", totalMonthHours);
        response.put("weeklyLogs", weeklyLogs);

        return response;
    }
}
