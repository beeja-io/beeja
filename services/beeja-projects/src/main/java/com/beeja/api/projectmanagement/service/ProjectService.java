package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.responses.ProjectResponseDTO;

import java.util.List;

/** Service interface for managing {@link Project} entities. */
public interface ProjectService {

  /**
   * Creates a new project for a client based on the provided project request.
   *
   * @param project the request object containing the details of the project to be created
   * @return the created {@link Project} object
   */
  Project createProjectForClient(ProjectRequest project);

  /**
   * Retrieves a project by its unique project ID and associated client ID.
   *
   * @param projectId the unique identifier of the project
   * @param clientId the unique identifier of the client
   * @return the {@link Project} object corresponding to the given project ID and client ID
   */
  Project getProjectByIdAndClientId(String projectId, String clientId);

  /**
   * Retrieves a list of projects associated with a given client within an organization.
   *
   * @param clientId the unique identifier of the client
   * @return a list of {@link Project} objects associated with the given client ID
   */
  List<Project> getProjectsByClientIdInOrganization(String clientId);

  /**
   * Retrieves a list of all projects within an organization.
   *
   * @return a list of all {@link Project} objects within the organization
   */
  List<Project> getAllProjectsInOrganization(int pageNumber, int pageSize,String projectId, ProjectStatus status);

  Long getTotalProjectsInOrganization(String projectId, ProjectStatus status);


  /**
   * Updates an existing project based on the provided project ID and project request.
   *
   * @param project the request object containing the updated project details
   * @param projectId the unique identifier of the project to be updated
   * @return the updated {@link Project} object
   */
  Project updateProjectByProjectId(ProjectRequest project, String projectId);

  Project changeProjectStatus(String projectId, ProjectStatus status);

  List<ProjectResponseDTO> getAllProjects(int pageNumber, int pageSize, String projectId, ProjectStatus status);

}
