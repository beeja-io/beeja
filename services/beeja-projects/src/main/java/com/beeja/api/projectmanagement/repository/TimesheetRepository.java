package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Timesheet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimesheetRepository extends MongoRepository<Timesheet, String> {
}
