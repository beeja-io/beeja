package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.annotations.HasPermission;
import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing projects within the project management system.
 * Provides endpoints for creating, retrieving, and updating projects.
 */
@RestController
@RequestMapping("/v1/projects")
public class ProjectsController {
    @Autowired
    private ProjectService projectService;

    /**
     * Creates a new project for a client based on the provided project request.
     * @param projectRequest the request object containing project details
     * @return a {@link ResponseEntity} containing the created project and HTTP status {@code 201 Created}
     */
    @PostMapping
    @HasPermission(PermissionConstants.CREATE_PROJECT)
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest projectRequest) {
        Project createdProject = projectService.createProjectForClient(projectRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * Retrieves a project by its unique identifier and associated client ID.
     * @param projectId the unique identifier of the project
     * @param clientId  the unique identifier of the client
     * @return a {@link ResponseEntity} containing the project and HTTP status {@code 200 OK}
     */
    @GetMapping("/{projectId}/{clientId}")
    @HasPermission(PermissionConstants.GET_PROJECT)
    public ResponseEntity<Project> getProjectById(@PathVariable String projectId, @PathVariable String clientId) {
        Project project = projectService.getProjectByIdAndClientId(projectId, clientId);
        return ResponseEntity.ok(project);
    }

    /**
     * Retrieves all projects associated with a specific client.
     * @param clientId the unique identifier of the client
     * @return a {@link ResponseEntity} containing the list of projects and HTTP status {@code 200 OK}
     */
    @GetMapping("/client/{clientId}")
    @HasPermission(PermissionConstants.GET_PROJECT)
    public ResponseEntity<List<Project>> getProjectsByClientId(@PathVariable String clientId) {
        List<Project> projects = projectService.getProjectsByClientIdInOrganization(clientId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Retrieves all projects within the organization.
     * @return a {@link ResponseEntity} containing the list of all projects and HTTP status {@code 200 OK}
     */
    @GetMapping
    @HasPermission(PermissionConstants.GET_PROJECT)
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjectsInOrganization();
        return ResponseEntity.ok(projects);
    }

    /**
     * Updates an existing project identified by its unique project ID.
     * @param projectId      the unique identifier of the project to update
     * @param projectRequest the request object containing updated project details
     * @return a {@link ResponseEntity} containing the updated project and HTTP status {@code 200 OK}
     */
    @PutMapping("/{projectId}")
    @HasPermission(PermissionConstants.UPDATE_PROJECT)
    public ResponseEntity<Project> updateProject(@PathVariable String projectId, @RequestBody ProjectRequest projectRequest) {
        Project updatedProject = projectService.updateProjectByProjectId(projectRequest, projectId);
        return ResponseEntity.ok(updatedProject);
    }
}
