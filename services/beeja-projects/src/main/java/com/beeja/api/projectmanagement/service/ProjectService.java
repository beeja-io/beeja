package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Project;

import java.util.List;
import java.util.Map;

public interface ProjectService {

    Project addProject(Project project);

    Project updateProject(String projectId, Project updatedProject);

    Project getProjectById(String id);

    List<Project> getAllProjects();

}
