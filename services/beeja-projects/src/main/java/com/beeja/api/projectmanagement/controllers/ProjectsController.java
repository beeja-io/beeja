package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects")
public class ProjectsController {
    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest projectRequest) {
        Project createdProject = projectService.createProjectForClient(projectRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @GetMapping("/{projectId}/{clientId}")
    public ResponseEntity<Project> getProjectById(@PathVariable String projectId, @PathVariable String clientId) {
        Project project = projectService.getProjectByIdAndClientId(projectId, clientId);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Project>> getProjectsByClientId(@PathVariable String clientId) {
        List<Project> projects = projectService.getProjectsByClientIdInOrganization(clientId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjectsInOrganization();
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Project> updateProject(@PathVariable String projectId, @RequestBody ProjectRequest projectRequest) {
        Project updatedProject = projectService.updateProjectByProjectId(projectRequest, projectId);
        return ResponseEntity.ok(updatedProject);
    }
}
