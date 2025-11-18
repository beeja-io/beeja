package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.OverallRating;
import com.beeja.api.performance_management.model.dto.EmployeeCycleInfo;
import com.beeja.api.performance_management.model.dto.PaginatedEmployeePerformanceResponse;

import java.util.List;

public interface MyTeamOverviewService {

    PaginatedEmployeePerformanceResponse getEmployeePerformanceData(
            String department,
            String designation,
            String employmentType,
            String status,
            int pageNumber,
            int pageSize);
    OverallRating createOrUpdateOverallRating(String employeeId, Double rating, String comments);

    OverallRating getOverallRatingByEmployeeId(String employeeId);

    void deleteOverallRatingByEmployeeId(String employeeId);

    List<EmployeeCycleInfo> getCycleIdsByEmployeeId(String employeeId);

}
