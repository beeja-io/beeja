package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.annotations.HasPermission;
import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.responses.ProjectDetailViewResponseDTO;
import com.beeja.api.projectmanagement.responses.ProjectDropdownDTO;
import com.beeja.api.projectmanagement.responses.ProjectEmployeeDTO;
import com.beeja.api.projectmanagement.responses.ProjectResponseDTO;
import com.beeja.api.projectmanagement.service.ProjectService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing projects within the project management system. Provides endpoints
 * for creating, retrieving, and updating projects.
 */
@RestController
@RequestMapping("/v1/projects")
public class ProjectsController {
  @Autowired private ProjectService projectService;

  /**
   * Creates a new project for a client based on the provided project request.
   *
   * @param projectRequest the request object containing project details
   * @return a {@link ResponseEntity} containing the created project and HTTP status {@code 201
   *     Created}
   */
  @PostMapping
  @HasPermission(PermissionConstants.CREATE_PROJECT)
  public ResponseEntity<Project> createProject(@RequestBody ProjectRequest projectRequest) {
    Project createdProject = projectService.createProjectForClient(projectRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
  }

  /**
   * Retrieves a project by its unique identifier and associated client ID.
   *
   * @param projectId the unique identifier of the project
   * @param clientId the unique identifier of the client
   * @return a {@link ResponseEntity} containing the project and HTTP status {@code 200 OK}
   */
  @GetMapping("/{projectId}/{clientId}")
  @HasPermission({PermissionConstants.GET_PROJECT, PermissionConstants.GET_CONTRACT})
  public ResponseEntity<List<ProjectDetailViewResponseDTO>> getProjectById(
      @PathVariable String projectId, @PathVariable String clientId) {
    ProjectDetailViewResponseDTO responseDTO = projectService.getProjectByIdAndClientId(projectId, clientId);
    return ResponseEntity.ok(Collections.singletonList(responseDTO));
  }

  /**
   * Retrieves all projects associated with a specific client.
   *
   * @param clientId the unique identifier of the client
   * @return a {@link ResponseEntity} containing the list of projects and HTTP status {@code 200 OK}
   */
  @GetMapping("/client/{clientId}")
  @HasPermission({PermissionConstants.GET_PROJECT, PermissionConstants.GET_CLIENT})
  public ResponseEntity<List<ProjectResponseDTO>> getProjectsByClientId(@PathVariable String clientId) {
      List<ProjectResponseDTO> projects = projectService.getProjectsByClientIdInOrganization(clientId);
      return ResponseEntity.ok(projects);
  }

  /**
   * Retrieves all projects within the organization.
   *
   * @return a {@link ResponseEntity} containing the list of all projects and HTTP status {@code 200
   *     OK}
   */
  @GetMapping
  @HasPermission(PermissionConstants.GET_PROJECT)
  public ResponseEntity<List<Project>> getAllProjects(@RequestParam(defaultValue = "0") int pageNumber,
                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                      @RequestParam(required = false) String projectId,
                                                      @RequestParam(required = false) ProjectStatus status) {
    String organizationId = UserContext.getLoggedInUserOrganization()
            .get(Constants.ID).toString();
    List<Project> projects = projectService.getAllProjectsInOrganization(organizationId, pageNumber,  pageSize, projectId, status );
    return ResponseEntity.ok(projects);
  }

  /**
   * Updates an existing project identified by its unique project ID.
   *
   * @param projectId the unique identifier of the project to update
   * @param projectRequest the request object containing updated project details
   * @return a {@link ResponseEntity} containing the updated project and HTTP status {@code 200 OK}
   */
  @PutMapping("/{projectId}")
  @HasPermission(PermissionConstants.UPDATE_PROJECT)
  public ResponseEntity<Project> updateProject(
      @PathVariable String projectId, @RequestBody ProjectRequest projectRequest) {
    Project updatedProject = projectService.updateProjectByProjectId(projectRequest, projectId);
    return ResponseEntity.ok(updatedProject);
  }

  @GetMapping("/projects")
  @HasPermission(PermissionConstants.GET_PROJECT)
  public ResponseEntity<Map<String, Object>> getAllProjectOf(
          @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
          @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
          @RequestParam(required = false) String projectId,
          @RequestParam(required = false) ProjectStatus status) {

      if (pageNumber < 1 || pageSize < 1) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number and size must be positive integers");
      }
      try {
      String organizationId = UserContext.getLoggedInUserOrganization()
              .get(Constants.ID).toString();
      List<ProjectResponseDTO> projects = projectService.getAllProjects(organizationId,
              pageNumber, pageSize, projectId, status
      );

      long totalRecords = projectService.getTotalProjectsInOrganization(organizationId, projectId, status);
      long totalPages = (long) Math.ceil((double) totalRecords / pageSize);

      Map<String, Object> response = new HashMap<>();
      response.put("metadata", Map.of(
              "totalRecords", totalRecords,
              "pageNumber", pageNumber,
              "pageSize", pageSize,
              "totalPages", totalPages
      ));
      response.put("data", projects);

      return ResponseEntity.ok(response);
    } catch (ResourceNotFoundException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", ex);
    }
  }

    @GetMapping("/{projectId}/employees")
    @HasPermission({PermissionConstants.GET_PROJECT, PermissionConstants.CREATE_CONTRACT})
    public ResponseEntity<ProjectEmployeeDTO> getEmployeesByProjectId(@PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getEmployeesByProjectId(projectId));
    }

    @GetMapping("/projects-dropdown")
    @HasPermission({PermissionConstants.GET_PROJECT, PermissionConstants.CREATE_CONTRACT})
    public List<ProjectDropdownDTO> getAllProjectsForDropdown() {
        String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        return projectService.getAllProjectsForDropdown(organizationId);
    }

    @PatchMapping("/{projectId}/status")
    @HasPermission(PermissionConstants.UPDATE_PROJECT)
    public ResponseEntity<Project> changeProjectStatus(
            @PathVariable String projectId,
            @RequestBody ProjectStatus status) {

        Project updatedContract = projectService.changeProjectStatus(projectId, status);
        return ResponseEntity.ok(updatedContract);
    }
}


