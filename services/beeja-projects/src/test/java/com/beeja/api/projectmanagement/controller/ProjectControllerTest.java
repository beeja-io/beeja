package com.beeja.api.projectmanagement.controller;

import com.beeja.api.projectmanagement.controllers.ProjectController;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectService projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        project = new Project();
        project.setId("123");
        project.setProjectName("Test Project");
    }

    @Test
    void testAddProject() {
        when(projectService.addProject(any(Project.class))).thenReturn(project);

        ResponseEntity<Project> response = projectController.addProject(project);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Project", response.getBody().getProjectName());
        verify(projectService, times(1)).addProject(any(Project.class));
    }

    @Test
    void testAddProject_Failure_InvalidData() {
        Project invalidProject = new Project(); // No project name set

        when(projectService.addProject(any(Project.class)))
                .thenThrow(new IllegalArgumentException("Project name cannot be null"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                projectController.addProject(invalidProject));

        assertEquals("Project name cannot be null", exception.getMessage());
        verify(projectService, times(1)).addProject(any(Project.class));
    }

    @Test
    void testUpdateProject() {
        when(projectService.updateProject(eq("123"), any(Project.class))).thenReturn(project);

        ResponseEntity<Project> response = projectController.updateProject("123", project);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Project", response.getBody().getProjectName());
        verify(projectService, times(1)).updateProject(eq("123"), any(Project.class));
    }

    @Test
    void testUpdateProject_Failure_ProjectNotFound() {
        // Mock projectService to throw ResourceNotFoundException when an unknown project ID is provided
        when(projectService.updateProject(eq("999"), any(Project.class)))
                .thenThrow(new ResourceNotFoundException(
                        new ErrorResponse(
                                ErrorType.NOT_FOUND,
                                ErrorCode.PROJECT_NOT_FOUND,
                                "Project not found with ID: 999",
                                "v1/projects/update"
                        )
                ));
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                projectController.updateProject("999", project));

        // Verify the exception message
        assertNotNull(exception.getErrorResponse());
        assertEquals(ErrorType.NOT_FOUND, exception.getErrorResponse().getType());
        assertEquals(ErrorCode.PROJECT_NOT_FOUND, exception.getErrorResponse().getCode());
        assertEquals("Project not found with ID: 999", exception.getErrorResponse().getMessage());
        assertEquals("v1/projects/update", exception.getErrorResponse().getPath());
        verify(projectService, times(1)).updateProject(eq("999"), any(Project.class));
    }

    @Test
    void testGetProjectById() {
        when(projectService.getProjectById("123")).thenReturn(project);

        ResponseEntity<Project> response = projectController.getProjectById("123");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Project", response.getBody().getProjectName());
        verify(projectService, times(1)).getProjectById("123");
    }

    @Test
    void testGetAllProjects() {
        List<Project> projects = Arrays.asList(project);
        when(projectService.getAllProjects()).thenReturn(projects);

        ResponseEntity<List<Project>> response = projectController.getAllProjects();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testGetAllProjects_Failure_NoProjectsFound() {
        when(projectService.getAllProjects()).thenReturn(Arrays.asList()); // Empty list

        ResponseEntity<List<Project>> response = projectController.getAllProjects();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(projectService, times(1)).getAllProjects();
    }
}

