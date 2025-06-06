package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.service.ProjectService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ProjectService} interface.
 *
 * <p>This service handles the business logic for managing {@link Project} entities within an
 * organization, including creation, retrieval, and updating of projects associated with clients.
 */
@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

  @Autowired ClientRepository clientRepository;

  @Autowired ProjectRepository projectRepository;

  /**
   * Creates a new {@link Project} for a given {@link Client} based on the provided {@link
   * ProjectRequest}.
   *
   * @param project the {@link ProjectRequest} containing the details to create the {@link Project}
   * @return the newly created {@link Project}
   * @throws ResourceNotFoundException if the {@link Client} is not found with the provided clientId
   */
  @Override
  public Project createProjectForClient(ProjectRequest project) {
    Client client =
        clientRepository.findByClientIdAndOrganizationId(
            project.getClientId(),
            UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
    if (client == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.CLIENT_NOT_FOUND,
              ErrorCode.RESOURCE_NOT_FOUND,
              Constants.CLIENT_NOT_FOUND));
    }
    Project newProject = new Project();
    if (project.getName() != null) {
      newProject.setName(project.getName());
    }
    if (project.getDescription() != null) {
      newProject.setDescription(project.getDescription());
    }
    if (project.getStatus() != null) {
      newProject.setStatus(project.getStatus());
    }
    if (project.getStartDate() != null) {
      newProject.setStartDate(project.getStartDate());
    }
    if (project.getEndDate() != null) {
      newProject.setEndDate(project.getEndDate());
    }
    if (project.getClientId() != null) {
      newProject.setClientId(project.getClientId());
    }
    newProject.setOrganizationId(
        UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());

    //        FIXME:  PROJECT ID GENERATION
    newProject.setProjectId(UUID.randomUUID().toString().toUpperCase().substring(0, 6));
    try {
      newProject = projectRepository.save(newProject);
    } catch (Exception e) {
      log.error(Constants.ERROR_SAVING_PROJECT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_CREATION_ERROR,
              Constants.ERROR_SAVING_PROJECT));
    }
    return newProject;
  }

  /**
   * Retrieves a {@link Project} by its unique identifier and associated {@link Client} ID.
   *
   * @param projectId the unique identifier of the {@link Project}
   * @param clientId the unique identifier of the {@link Client}
   * @return the {@link Project} entity
   * @throws ResourceNotFoundException if no {@link Project} is found with the provided projectId
   *     and clientId
   */
  @Override
  public Project getProjectByIdAndClientId(String projectId, String clientId) {
    Project project;
    try {
      project =
          projectRepository.findByProjectIdAndClientIdAndOrganizationId(
              projectId,
              clientId,
              UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
    } catch (Exception e) {
      log.error(Constants.PROJECT_NOT_FOUND_WITH_CLIENT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_NOT_FOUND,
              Constants.PROJECT_NOT_FOUND_WITH_CLIENT));
    }
    if (project == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR, ErrorCode.RESOURCE_NOT_FOUND, Constants.PROJECT_NOT_FOUND));
    }
    return project;
  }

  /**
   * Retrieves a list of {@link Project} entities associated with a specific {@link Client} in the
   * organization.
   *
   * @param clientId the unique identifier of the {@link Client}
   * @return a list of {@link Project} entities
   * @throws ResourceNotFoundException if no {@link Project} is found for the provided clientId
   */
  @Override
  public List<Project> getProjectsByClientIdInOrganization(String clientId) {
    List<Project> projects;
    try {
      projects =
          projectRepository.findByClientIdAndOrganizationId(
              clientId, UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
    } catch (Exception e) {
      log.error(Constants.ERROR_FETCHING_PROJECTS_WITH_CLIENT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_NOT_FOUND,
              Constants.ERROR_FETCHING_PROJECTS_WITH_CLIENT));
    }
    if (projects == null || projects.isEmpty()) {
      return List.of();
    }
    return projects;
  }

  /**
   * Retrieves a list of all {@link Project} entities in the current organization.
   *
   * @return a list of all {@link Project} entities
   * @throws ResourceNotFoundException if no {@link Project} entities are found for the organization
   */
  @Override
  public List<Project> getAllProjectsInOrganization() {
    List<Project> projects;
    try {
      projects =
          projectRepository.findByOrganizationId(
              UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
    } catch (Exception e) {
      log.error(Constants.ERROR_FETCHING_PROJECTS_WITH_ORGANIZATION, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_NOT_FOUND,
              Constants.ERROR_FETCHING_PROJECTS_WITH_ORGANIZATION));
    }
    if (projects == null || projects.isEmpty()) {
      return List.of();
    }
    return projects;
  }

  /**
   * Updates an existing {@link Project} based on the provided {@link ProjectRequest}.
   *
   * @param project the {@link ProjectRequest} containing updated details for the {@link Project}
   * @param projectId the unique identifier of the {@link Project} to update
   * @return the updated {@link Project}
   * @throws ResourceNotFoundException if the {@link Project} is not found with the provided
   *     projectId
   */
  @Override
  public Project updateProjectByProjectId(ProjectRequest project, String projectId) {
    Project existingProject;
    try {
      existingProject =
          projectRepository.findByProjectIdAndOrganizationId(
              projectId, UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
    } catch (Exception e) {
      log.error(Constants.ERROR_FETCHING_PROJECT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR, ErrorCode.RESOURCE_NOT_FOUND, Constants.ERROR_FETCHING_PROJECT));
    }
    if (existingProject == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR, ErrorCode.RESOURCE_NOT_FOUND, Constants.PROJECT_NOT_FOUND));
    }
    if (project.getName() != null) {
      existingProject.setName(project.getName());
    }
    if (project.getDescription() != null) {
      existingProject.setDescription(project.getDescription());
    }
    if (project.getStatus() != null) {
      existingProject.setStatus(project.getStatus());
    }
    if (project.getStartDate() != null) {
      existingProject.setStartDate(project.getStartDate());
    }
    if (project.getEndDate() != null) {
      existingProject.setEndDate(project.getEndDate());
    }
    try {
      existingProject = projectRepository.save(existingProject);
    } catch (Exception e) {
      log.error(Constants.ERROR_UPDATING_PROJECT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_CREATION_ERROR,
              Constants.ERROR_UPDATING_PROJECT));
    }
    return existingProject;
  }
}
