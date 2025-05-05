package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    Project findByProjectIdAndOrganizationId(String projectId, String organizationId);
    List<Project> findByClientIdAndOrganizationId(String clientId, String organizationId);
    List<Project> findByOrganizationId(String organizationId);
    Project findByProjectIdAndClientIdAndOrganizationId(String projectId, String clientId, String organizationId);
}