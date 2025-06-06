package com.beeja.api.projectmanagement.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.service.ProjectService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ProjectsControllerTest {

  @Mock private ProjectService projectService;

  @InjectMocks private ProjectsController projectsController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testCreateProject() {

    ProjectRequest projectRequest = new ProjectRequest();
    projectRequest.setName("Test Project");
    projectRequest.setClientId("client123");
    projectRequest.setStatus(ProjectStatus.IN_PROGRESS);
    projectRequest.setStartDate(new Date());

    Project createdProject = new Project();
    createdProject.setId("project123");
    createdProject.setName("Test Project");
    createdProject.setClientId("client123");

    when(projectService.createProjectForClient(projectRequest)).thenReturn(createdProject);

    ResponseEntity<Project> responseEntity = projectsController.createProject(projectRequest);

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(createdProject, responseEntity.getBody());
    verify(projectService, times(1)).createProjectForClient(projectRequest);
  }

  @Test
  public void testGetProjectById() {

    String projectId = "project123";
    String clientId = "client123";
    Project project = new Project();
    project.setId(projectId);
    project.setName("Test Project");
    project.setClientId(clientId);

    when(projectService.getProjectByIdAndClientId(projectId, clientId)).thenReturn(project);

    ResponseEntity<Project> responseEntity = projectsController.getProjectById(projectId, clientId);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(project, responseEntity.getBody());
    verify(projectService, times(1)).getProjectByIdAndClientId(projectId, clientId);
  }

  @Test
  public void testGetProjectsByClientId() {

    String clientId = "client123";
    List<Project> projects = new ArrayList<>();
    Project project1 = new Project();
    project1.setId("project1");
    project1.setName("Project 1");
    project1.setClientId(clientId);
    projects.add(project1);

    Project project2 = new Project();
    project2.setId("project2");
    project2.setName("Project 2");
    project2.setClientId(clientId);
    projects.add(project2);

    when(projectService.getProjectsByClientIdInOrganization(clientId)).thenReturn(projects);

    ResponseEntity<List<Project>> responseEntity =
        projectsController.getProjectsByClientId(clientId);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(projects, responseEntity.getBody());
    verify(projectService, times(1)).getProjectsByClientIdInOrganization(clientId);
  }

  @Test
  public void testGetAllProjects() {

    List<Project> projects = new ArrayList<>();
    Project project1 = new Project();
    project1.setId("project1");
    project1.setName("Project 1");
    project1.setClientId("client1");
    projects.add(project1);

    Project project2 = new Project();
    project2.setId("project2");
    project2.setName("Project 2");
    project2.setClientId("client2");
    projects.add(project2);

    when(projectService.getAllProjectsInOrganization()).thenReturn(projects);

    ResponseEntity<List<Project>> responseEntity = projectsController.getAllProjects();

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(projects, responseEntity.getBody());
    verify(projectService, times(1)).getAllProjectsInOrganization();
  }

  @Test
  public void testUpdateProject() {

    String projectId = "project123";
    ProjectRequest projectRequest = new ProjectRequest();
    projectRequest.setName("Updated Project Name");
    projectRequest.setClientId("client123");
    projectRequest.setStatus(ProjectStatus.COMPLETED);
    projectRequest.setStartDate(new Date());

    Project updatedProject = new Project();
    updatedProject.setId(projectId);
    updatedProject.setName("Updated Project Name");
    updatedProject.setClientId("client123");

    when(projectService.updateProjectByProjectId(projectRequest, projectId))
        .thenReturn(updatedProject);

    ResponseEntity<Project> responseEntity =
        projectsController.updateProject(projectId, projectRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(updatedProject, responseEntity.getBody());
    verify(projectService, times(1)).updateProjectByProjectId(projectRequest, projectId);
  }
}
