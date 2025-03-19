package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {
    Project findByIdAndOrganizationId(String projectId, String organizationId);

    Project findTopByOrderByProjectIdDesc();
}
