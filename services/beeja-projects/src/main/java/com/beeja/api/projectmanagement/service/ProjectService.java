package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.request.ProjectRequest;

import java.util.List;

public interface ProjectService {
    Project createProjectForClient(ProjectRequest project);
    Project getProjectByIdAndClientId(String projectId, String clientId);
    List<Project> getProjectsByClientIdInOrganization(String clientId);
    List<Project> getAllProjectsInOrganization();
    Project updateProjectByProjectId(ProjectRequest project, String projectId);
}