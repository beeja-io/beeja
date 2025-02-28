package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Resource;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResourceRepository extends MongoRepository<Resource, String> {

    List<Resource> findByEmployeeIdIn(List<String> requestedEmployeeIds);
}
