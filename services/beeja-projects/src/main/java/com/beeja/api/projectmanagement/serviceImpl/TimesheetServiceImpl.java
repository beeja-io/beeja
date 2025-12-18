package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.exceptions.InvalidOperationException;

import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.Timesheet;
import com.beeja.api.projectmanagement.model.dto.*;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.repository.TimesheetRepository;
import com.beeja.api.projectmanagement.service.TimesheetService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * Service implementation for timesheet operations.
 *
 * <p>This service provides CRUD and query operations for {@link Timesheet} entities scoped to the
 * logged-in user's organization and employee. It validates resource ownership (projects/contracts)
 * and enforces that updates/deletes are performed only by the owning employee.</p>
 *
 * <p>Logging is present throughout the service to aid debugging and operational observability.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final ProjectRepository projectRepository;
    private final ContractRepository contractRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Creates a new timesheet entry for the logged-in employee.
     *
     * <p>Validates that the referenced project (and optionally contract) belong to the
     * same organization as the logged-in user. Persists the timesheet with created metadata.</p>
     *
     * @param requestDto the DTO containing the timesheet details (projectId, contractId, startDate,
     *                   timeInMinutes, description). Must contain a valid projectId that exists in the
     *                   user's organization.
     * @return the persisted {@link Timesheet} entity (including generated id and createdAt).
     * @throws ResourceNotFoundException if project or contract (when provided) are not found in
     *                                   the user's organization.
     */
    @Override
    @Transactional
    public Timesheet saveTimesheet(TimesheetRequestDto requestDto) {
        String employeeId = UserContext.getLoggedInEmployeeId();
        String organizationId = (String) UserContext.getLoggedInUserOrganization().get(Constants.ID);

        log.info("Saving timesheet for employeeId={}, organizationId={}", employeeId, organizationId);
        log.debug("SaveTimesheet Request DTO: {}", requestDto);

        if (!projectRepository.existsByProjectIdAndOrganizationId(requestDto.getProjectId(), organizationId)) {
            log.error("Project not found: projectId={}, orgId={}", requestDto.getProjectId(), organizationId);
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.PROJECT_NOT_FOUND
                    ));
        }

        if (requestDto.getContractId() != null && !requestDto.getContractId().isEmpty()) {
            if (!contractRepository.existsByContractIdAndOrganizationId(requestDto.getContractId(), organizationId)) {
                log.error("Contract not found: contractId={}, orgId={}", requestDto.getContractId(), organizationId);
                throw new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.NOT_FOUND,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.CONTRACT_NOT_FOUND
                        ));
            }
        }

        Timesheet timesheet = Timesheet.builder()
                .organizationId(organizationId)
                .employeeId(employeeId)
                .projectId(requestDto.getProjectId())
                .contractId(requestDto.getContractId())
                .startDate(requestDto.getStartDate())
                .timeInMinutes(requestDto.getTimeInMinutes())
                .description(requestDto.getDescription())
                .createdBy(employeeId)
                .createdAt(Instant.now())
                .build();

        log.debug("Prepared Timesheet Entity for Save: {}", timesheet);

        Timesheet saved = timesheetRepository.save(timesheet);
        log.info("Timesheet saved successfully with id={}", saved.getId());

        return saved;
    }

    /**
     * Updates an existing timesheet entry.
     *
     * <p>The method ensures the timesheet exists within the logged-in user's organization and that
     * the logged-in employee is the owner of the timesheet. Only provided fields are updated and
     * modification metadata is applied.</p>
     *
     * @param dto DTO containing updated timesheet fields (projectId, contractId, startDate,
     *            timeInMinutes, description).
     * @param id  the ID of the timesheet to update.
     * @return the updated {@link Timesheet} entity.
     * @throws ResourceNotFoundException if the timesheet does not exist in the user's organization.
     * @throws InvalidOperationException if the logged-in employee is not the owner of the timesheet.
     */
    @Override
    @Transactional
    public Timesheet updateLog(TimesheetRequestDto dto, String id) {

        String organizationId = (String) UserContext.getLoggedInUserOrganization().get(Constants.ID);
        String loggedInEmployeeId = UserContext.getLoggedInEmployeeId();

        log.info("Updating timesheet id={} for employeeId={}, orgId={}", id, loggedInEmployeeId, organizationId);
        log.debug("UpdateTimesheet Request DTO: {}", dto);

        Timesheet existing = timesheetRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> {
                    log.error("Timesheet not found for id={}, orgId={}", id, organizationId);
                    return new ResourceNotFoundException(
                            BuildErrorMessage.buildErrorMessage(
                                    ErrorType.NOT_FOUND,
                                    ErrorCode.RESOURCE_NOT_FOUND,
                                    Constants.TIMESHEET_NOT_FOUND
                            ));
                });

        log.debug("Existing Timesheet Before Update: {}", existing);

        if (!existing.getEmployeeId().equals(loggedInEmployeeId)) {
            log.error("Unauthorized update attempt by employeeId={} for timesheetId={}", loggedInEmployeeId, id);
            throw new InvalidOperationException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.INVALID_OPERATION,
                            ErrorCode.UNAUTHORIZED,
                            Constants.NOT_ALLOWED_TO_UPDATE_TIMESHEET
                    ));
        }

        if (dto.getProjectId() != null) existing.setProjectId(dto.getProjectId());
        if (dto.getContractId() != null) existing.setContractId(dto.getContractId());
        if (dto.getStartDate() != null) existing.setStartDate(dto.getStartDate());

        existing.setTimeInMinutes(dto.getTimeInMinutes());
        existing.setDescription(dto.getDescription());
        existing.setModifiedAt(Instant.now());
        existing.setModifiedBy(loggedInEmployeeId);

        log.debug("Updated Timesheet Entity Before Save: {}", existing);

        Timesheet saved = timesheetRepository.save(existing);
        log.info("Timesheet updated successfully id={}", saved.getId());

        return saved;
    }

    /**
     * Retrieves paginated timesheets filtered by day, ISO week, or month.
     *
     * <p>Only one of {@code day}, {@code week}/{@code weekYear}, or {@code month} should be provided.
     * If {@code employeeId} is null or empty, the logged-in employee's timesheets are returned.
     * Results are paginated and sorted by {@code startDate DESC}.</p>
     *
     * @param day        optional filter in {@code yyyy-MM-dd} format; returns records for that day.
     * @param week       optional ISO week number (1–53). If provided, {@code weekYear} is required.
     * @param weekYear   week-based ISO year corresponding to {@code week}. Required when {@code week} is provided.
     * @param month      optional filter in {@code yyyy-MM} format; returns records for that month.
     * @param employeeId optional employee id; if null/empty, the logged-in employee id is used.
     * @param page       page zero-based page index (negative values will be treated as 0).
     * @param size       page size (values &lt; 1 will be treated as 1).
     * @return a {@link Page} of {@link Timesheet} matching the filters.
     * @throws IllegalArgumentException if {@code week} is provided without {@code weekYear} or if date formats are invalid.
     */
    @Override
    public Page<Timesheet> getTimesheets(
            String day, Integer week, Integer weekYear, String month,
            String employeeId, int page, int size) {

        String loggedInEmployeeId = UserContext.getLoggedInEmployeeId();
        String organizationId = (String) UserContext.getLoggedInUserOrganization().get(Constants.ID);

        String employeeIdToUse =
                (employeeId != null && !employeeId.isEmpty()) ? employeeId : loggedInEmployeeId;

        log.info("Fetching timesheets for employeeId={}, orgId={}", employeeIdToUse, organizationId);
        log.debug("Filters received → day={}, week={}, weekYear={}, month={}, page={}, size={}",

                day, week, weekYear, month, page, size);

        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(Sort.Direction.DESC, "startDate")
        );

        Criteria criteria = Criteria.where("organizationId").is(organizationId)
                .and("employeeId").is(employeeIdToUse);

        ZoneId zone = ZoneId.systemDefault();

        if (day != null) {
            LocalDate localDate = parseDay(day);
            criteria = criteria.and("startDate")
                    .gte(localDate.atStartOfDay(zone).toInstant())
                    .lt(localDate.plusDays(1).atStartOfDay(zone).toInstant());

        } else if (week != null) {
            if (weekYear == null) {
                throw new IllegalArgumentException("weekYear is required when using week parameter");
            }

            LocalDate startOfWeek = isoWeekStart(weekYear, week);
            LocalDate endOfWeek = startOfWeek.plusDays(7);

            criteria = criteria.and("startDate")
                    .gte(startOfWeek.atStartOfDay(zone).toInstant())
                    .lt(endOfWeek.atStartOfDay(zone).toInstant());

        } else if (month != null) {
            YearMonth ym = parseMonth(month);
            LocalDate startOfMonth = ym.atDay(1);
            LocalDate endOfMonth = ym.atEndOfMonth().plusDays(1);

            criteria = criteria.and("startDate")
                    .gte(startOfMonth.atStartOfDay(zone).toInstant())
                    .lt(endOfMonth.atStartOfDay(zone).toInstant());
        }

        Query query = new Query(criteria).with(pageable);
        List<Timesheet> entries = mongoTemplate.find(query, Timesheet.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Timesheet.class);

        return new PageImpl<>(entries, pageable, total);
    }

    /**
     * Groups timesheets for a specific month into ISO weeks and aggregates daily and weekly totals.
     *
     * <p>Response structure contains a {@code weekTimesheets} map keyed by {@code week-YYYY-WW} and
     * a {@code monthlyTotalHours} value (hours rounded to one decimal place).</p>
     *
     * @param month month in {@code yyyy-MM} format.
     * @return a {@link Map} containing {@code weekTimesheets} and {@code monthlyTotalHours}.
     * @throws IllegalArgumentException if {@code month} is not in the expected {@code yyyy-MM} format.
     */
    @Override
    public Map<String, Object> getTimesheetsGroupedByWeek(String month) {

        String employeeId = UserContext.getLoggedInEmployeeId();
        String organizationId = (String) UserContext.getLoggedInUserOrganization().get(Constants.ID);

        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(month);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Invalid 'month' format. Expected yyyy-MM. Received: " + month
            );
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = yearMonth.atDay(1).atStartOfDay(zoneId).toInstant();
        Instant endInstant = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay(zoneId).toInstant();

        Query q = new Query();
        q.addCriteria(Criteria.where("organizationId").is(organizationId)
                        .and("employeeId").is(employeeId)
                        .and("startDate").gte(startInstant).lt(endInstant))
                .with(Sort.by(Sort.Direction.DESC, "startDate"));

        List<Timesheet> timesheets = mongoTemplate.find(q, Timesheet.class);

        WeekFields weekFields = WeekFields.ISO;
        Map<String, WeekTimesheetResponse> weekMap = new LinkedHashMap<>();
        double totalMonthHours = 0.0;

        for (Timesheet ts : timesheets) {
            if (ts == null || ts.getStartDate() == null) continue;

            LocalDate localDate = ts.getStartDate().atZone(zoneId).toLocalDate();

            int weekNumber = localDate.get(weekFields.weekOfWeekBasedYear());
            int weekYear = localDate.get(weekFields.weekBasedYear());

            String weekKey = String.format(Constants.WEEK_KEY_FORMAT, weekYear, weekNumber);

            LocalDate weekStart = isoWeekStart(weekYear, weekNumber);
            LocalDate weekEnd = weekStart.plusDays(6);

            WeekTimesheetResponse weekResp = weekMap.computeIfAbsent(weekKey, k -> {
                WeekTimesheetResponse w = new WeekTimesheetResponse();
                w.setWeekNumber(weekNumber);
                w.setWeekYear(weekYear);
                w.setWeekStartDate(weekStart.toString());
                w.setWeekEndDate(weekEnd.toString());
                w.setWeeklyTotalHours(0.0);
                w.setDailyLogs(new LinkedHashMap<>());
                return w;
            });

            String dayKey = localDate.toString();

            DayTimesheetResponse dayResp = weekResp.getDailyLogs().computeIfAbsent(dayKey, d -> {
                DayTimesheetResponse dr = new DayTimesheetResponse();
                dr.setDate(dayKey);
                dr.setDayTotalHours(0.0);
                dr.setTimesheets(new ArrayList<>());
                return dr;
            });

            dayResp.getTimesheets().add(ts);

            double hours = ts.getTimeInMinutes() / 60.0;

            dayResp.setDayTotalHours(Math.round((dayResp.getDayTotalHours() + hours) * 10) / 10.0);
            weekResp.setWeeklyTotalHours(Math.round((weekResp.getWeeklyTotalHours() + hours) * 10) / 10.0);

            totalMonthHours += hours;
        }

        Map<String, Object> finalResponse = new LinkedHashMap<>();
        finalResponse.put("weekTimesheets", weekMap);
        finalResponse.put("monthlyTotalHours", Math.round(totalMonthHours * 10.0) / 10.0);

        return finalResponse;
    }

    /**
     * Deletes a timesheet entry.
     *
     * <p>Validates that the timesheet belongs to the logged-in user's organization and that the
     * logged-in employee is the owner. Performs a hard delete by id.</p>
     *
     * @param id the ID of the timesheet to delete.
     * @throws ResourceNotFoundException if the timesheet does not exist in the user's organization.
     * @throws InvalidOperationException if the logged-in employee is not the owner of the timesheet.
     */
    @Override
    @Transactional
    public void deleteTimesheet(String id) {

        String organizationId = (String) UserContext.getLoggedInUserOrganization().get(Constants.ID);
        String loggedInEmployeeId = UserContext.getLoggedInEmployeeId();

        log.info("Deleting timesheet id={} for employeeId={}, orgId={}", id, loggedInEmployeeId, organizationId);

        Timesheet existing = timesheetRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> {
                    log.error("Timesheet not found for delete: id={}, orgId={}", id, organizationId);
                    return new ResourceNotFoundException(
                            BuildErrorMessage.buildErrorMessage(
                                    ErrorType.NOT_FOUND,
                                    ErrorCode.RESOURCE_NOT_FOUND,
                                    Constants.TIMESHEET_NOT_FOUND_BY_ID
                            ));
                });

        if (!existing.getEmployeeId().equals(loggedInEmployeeId)) {
            log.error("Unauthorized delete attempt by employeeId={} for timesheetId={}", loggedInEmployeeId, id);
            throw new InvalidOperationException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.INVALID_OPERATION,
                            ErrorCode.UNAUTHORIZED,
                            Constants.NOT_ALLOWED_TO_DELETE_TIMESHEET
                    ));
        }

        timesheetRepository.deleteById(existing.getId());
        log.info("Timesheet deleted successfully id={}", id);
    }

    /**
     * Retrieves all projects where the logged-in employee is either a project resource or a project manager.
     *
     * <p>Combines projects found via resources and managers, deduplicates by projectId, and maps to
     * {@link ProjectDropdownDto} suitable for dropdowns or lightweight client lists.</p>
     *
     * @return list of {@link ProjectDropdownDto} representing projects accessible to the logged-in employee.
     */
    @Override
    public List<ProjectDropdownDto> getMyProjects() {

        String employeeId = UserContext.getLoggedInEmployeeId();
        String organizationId = (String) UserContext.getLoggedInUserOrganization().get(Constants.ID);

        log.info("Fetching projects for employeeId={}, orgId={}", employeeId, organizationId);

        List<Project> byResources =
                projectRepository.findByOrganizationIdAndProjectResourcesContaining(organizationId, employeeId);

        List<Project> byManagers =
                projectRepository.findByOrganizationIdAndProjectManagersContaining(organizationId, employeeId);

        log.debug("Projects found as resource={}, as manager={}",
                byResources != null ? byResources.size() : 0,
                byManagers != null ? byManagers.size() : 0);

        Map<String, Project> combined = new LinkedHashMap<>();

        if (byResources != null) byResources.forEach(p -> combined.put(p.getProjectId(), p));
        if (byManagers != null) byManagers.forEach(p -> combined.put(p.getProjectId(), p));

        List<ProjectDropdownDto> result = combined.values().stream()
                .map(p -> new ProjectDropdownDto(p.getId(), p.getProjectId(), p.getName()))
                .toList();

        log.info("Total unique projects available for dropdown={}", result.size());
        return result;
    }

    /**
     * Retrieves all contracts for a given project inside the logged-in user's organization.
     *
     * @param projectId the project identifier to fetch contracts for.
     * @return list of {@link ContractDropdownDto}; empty list if none found.
     */
    @Override
    public List<ContractDropdownDto> getContractsForProject(String projectId) {

        String organizationId = (String) UserContext.getLoggedInUserOrganization().get(Constants.ID);
        log.info("Fetching contracts for projectId={}, orgId={}", projectId, organizationId);

        List<Contract> contracts =
                contractRepository.findByProjectIdAndOrganizationId(projectId, organizationId);

        if (contracts == null) {
            log.info("No contracts found for projectId={}", projectId);
            return List.of();
        }

        log.debug("Contracts found count={}", contracts.size());

        return contracts.stream()
                .map(c -> new ContractDropdownDto(c.getId(), c.getContractId(), c.getContractTitle()))
                .toList();
    }

    private LocalDate parseDay(String day) {
        try {
            return LocalDate.parse(day);
        } catch (Exception ex) {
            log.error("Invalid day format received={}", day);
            throw new IllegalArgumentException("Invalid 'day' format. Expected yyyy-MM-dd. Received: " + day);
        }
    }

    private YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month);
        } catch (Exception ex) {
            log.error("Invalid month format received={}", month);
            throw new IllegalArgumentException("Invalid 'month' format. Expected yyyy-MM. Received: " + month);
        }
    }

    private LocalDate isoWeekStart(int isoYear, int isoWeek) {
        WeekFields wf = WeekFields.ISO;
        LocalDate anchor = LocalDate.of(isoYear, 1, 4);
        return anchor.with(wf.weekOfWeekBasedYear(), isoWeek).with(wf.dayOfWeek(), 1);
    }
}
