package com.beeja.api.projectmanagement.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.responses.ProjectDetailViewResponseDTO;
import com.beeja.api.projectmanagement.service.ProjectService;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

class ProjectsControllerTest {


  @InjectMocks
  private ProjectsController projectsController;

  @Mock
  private ProjectService projectService;

  private static MockedStatic<UserContext> userContextMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    Map<String, Object> orgMap = new HashMap<>();
    orgMap.put(Constants.ID, "org123");

    userContextMock = Mockito.mockStatic(UserContext.class);
    userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);
  }

  @AfterEach
  void tearDown() {
    if (userContextMock != null) userContextMock.close();
  }

  @Test
  void testCreateProject() {
    ProjectRequest request = new ProjectRequest();
    request.setName("Test Project");
    request.setClientId("client123");
    request.setStatus(ProjectStatus.IN_PROGRESS);

    Project project = new Project();
    project.setId("project123");
    project.setName("Test Project");

    when(projectService.createProjectForClient(request)).thenReturn(project);

    ResponseEntity<Project> response = projectsController.createProject(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(project, response.getBody());
    verify(projectService, times(1)).createProjectForClient(request);
  }

  @Test
  void testGetProjectById() {
    String projectId = "project123";
    String clientId = "client123";

    ProjectDetailViewResponseDTO dto = new ProjectDetailViewResponseDTO();
    dto.setProjectId(projectId);

    ResponseEntity<List<ProjectDetailViewResponseDTO>> responseEntity = projectsController.getProjectById(projectId, clientId);

    ResponseEntity<List<ProjectDetailViewResponseDTO>> response =
            projectsController.getProjectById(projectId, clientId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    assertEquals(projectId, response.getBody().get(0).getProjectId());
    verify(projectService, times(1)).getProjectByIdAndClientId(projectId, clientId);
  }

  @Test
  void testGetProjectsByClientId() {
    String clientId = "client123";
    Project project = new Project();
    project.setId("project123");
    List<Project> projects = List.of(project);

    when(projectService.getProjectsByClientIdInOrganization(clientId)).thenReturn(projects);

    ResponseEntity<List<Project>> response = projectsController.getProjectsByClientId(clientId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(projects, response.getBody());
    verify(projectService, times(1)).getProjectsByClientIdInOrganization(clientId);
  }

  @Test
  void testGetAllProjects() {
    Project project = new Project();
    project.setId("project123");
    List<Project> projects = List.of(project);

    when(projectService.getAllProjectsInOrganization("org123", 0, 10, null, null)).thenReturn(projects);

    ResponseEntity<List<Project>> response = projectsController.getAllProjects(0, 10, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(projects, response.getBody());
    verify(projectService, times(1))
            .getAllProjectsInOrganization("org123", 0, 10, null, null);
  }

  @Test
  void testUpdateProject() {
    String projectId = "project123";
    ProjectRequest request = new ProjectRequest();
    request.setName("Updated Project");

    Project updated = new Project();
    updated.setId(projectId);
    updated.setName("Updated Project");

    when(projectService.updateProjectByProjectId(request, projectId)).thenReturn(updated);

    ResponseEntity<Project> response = projectsController.updateProject(projectId, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updated, response.getBody());
    verify(projectService, times(1)).updateProjectByProjectId(request, projectId);
  }

  @Test
  void testChangeProjectStatus() {
    String projectId = "project123";
    ProjectStatus status = ProjectStatus.COMPLETED;

    Project updated = new Project();
    updated.setId(projectId);

    when(projectService.changeProjectStatus(projectId, status)).thenReturn(updated);

    ResponseEntity<Project> response = projectsController.changeProjectStatus(projectId, status);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updated, response.getBody());
    verify(projectService, times(1)).changeProjectStatus(projectId, status);
  }

  @Test
  void testGetAllProject_WithMetadata() throws Exception {
    List<ProjectResponseDTO> projectList = List.of(new ProjectResponseDTO());
    when(projectService.getAllProjects("org123", 1, 10, null, null)).thenReturn(projectList);
    when(projectService.getTotalProjectsInOrganization("org123", null, null)).thenReturn(1L);

    ResponseEntity<Map<String, Object>> response = projectsController.getAllProject(1, 10, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertTrue(body.containsKey("metadata"));
    assertTrue(body.containsKey("projects"));
    verify(projectService, times(1)).getAllProjects("org123", 1, 10, null, null);
    verify(projectService, times(1)).getTotalProjectsInOrganization("org123", null, null);
  }

  @Test
  void testGetEmployeesByProjectId() {
    String projectId = "project123";
    ProjectEmployeeDTO dto = new ProjectEmployeeDTO();
    when(projectService.getEmployeesByProjectId(projectId)).thenReturn(dto);

    ResponseEntity<ProjectEmployeeDTO> response = projectsController.getEmployeesByProjectId(projectId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto, response.getBody());
    verify(projectService, times(1)).getEmployeesByProjectId(projectId);
  }
}
