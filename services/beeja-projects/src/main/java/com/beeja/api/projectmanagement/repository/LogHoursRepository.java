package com.beeja.api.projectmanagement.repository;


import com.beeja.api.projectmanagement.model.LogHours.Timesheet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface LogHoursRepository extends MongoRepository<Timesheet, String> {
    Timesheet findByEmployeeIdAndOrganizationId(String employeeId, String organizationId);

    @Query("{'employeeId': ?0, 'logHours.date': {$gte: ?1, $lte: ?2}}")
    List<Timesheet> findByEmployeeIdAndLogDateRange(String employeeId, Date start, Date end);

}
