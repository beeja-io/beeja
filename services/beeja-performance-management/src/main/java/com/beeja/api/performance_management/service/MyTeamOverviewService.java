package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.OverallRating;
import com.beeja.api.performance_management.model.dto.EmployeeCycleInfo;

import java.util.List;

public interface MyTeamOverviewService {

    OverallRating createOrUpdateOverallRating(String employeeId, Double rating, String comments);

    OverallRating getOverallRatingByEmployeeId(String employeeId);

    void deleteOverallRatingByEmployeeId(String employeeId);

    List<EmployeeCycleInfo> getCycleIdsByEmployeeId(String employeeId);

}
