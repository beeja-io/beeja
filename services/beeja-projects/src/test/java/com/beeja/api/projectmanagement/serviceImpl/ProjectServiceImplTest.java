package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.exceptions.FeignClientException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.dto.EmployeeNameDTO;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.responses.ProjectDetailViewResponseDTO;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceImplTest {

  private ProjectServiceImpl projectService;

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private ClientRepository clientRepository;

  @Mock
  private AccountClient accountClient;

  @Mock
  private ContractRepository contractRepository;

  @Mock
  private MongoTemplate mongoTemplate;

  private MockedStatic<UserContext> userContextMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    projectService = new ProjectServiceImpl();
    projectService.projectRepository = projectRepository;
    projectService.clientRepository = clientRepository;
    projectService.accountClient = accountClient;
    projectService.contractRepository = contractRepository;
    projectService.mongoTemplate = mongoTemplate;

    if (userContextMock != null) {
      userContextMock.close();
    }
    userContextMock = mockStatic(UserContext.class);
    Map<String, Object> orgMap = Map.of(Constants.ID, "org123");
    userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);
  }

  @AfterEach
  void tearDown() {
    if (userContextMock != null) {
      userContextMock.close();
    }
  }

  @Test
  void testCreateProjectForClient_Success() {
    ProjectRequest request = new ProjectRequest();
    request.setName("Test Project");
    request.setClientId("client123");

    Client client = new Client();
    client.setClientId("client123");

    when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(client);
    when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArguments()[0]);

    Project result = projectService.createProjectForClient(request);

    assertNotNull(result);
    assertEquals("Test Project", result.getName());
    assertEquals("client123", result.getClientId());
  }

  @Test
  void testCreateProjectForClient_ClientNotFound() {
    ProjectRequest request = new ProjectRequest();
    request.setClientId("client123");
    when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(null);

    assertThrows(ResourceNotFoundException.class,
            () -> projectService.createProjectForClient(request));
  }

  @Test
  void testUpdateProjectByProjectId_Success() {
    Project existingProject = new Project();
    existingProject.setProjectId("project123");
    existingProject.setName("Test Project");

    ProjectRequest updateRequest = new ProjectRequest();
    updateRequest.setName("Updated Project");

    when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString()))
            .thenReturn(existingProject);
    when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArguments()[0]);

    Project updated = projectService.updateProjectByProjectId(updateRequest, "project123");

    assertEquals("Updated Project", updated.getName());
  }

  @Test
  void testUpdateProjectByProjectId_ProjectNotFound() {
    when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString())).thenReturn(null);
    ProjectRequest request = new ProjectRequest();

    assertThrows(ResourceNotFoundException.class,
            () -> projectService.updateProjectByProjectId(request, "nonexistent"));
  }

  @Test
  void testUpdateProjectByProjectId_SaveException() {
    Project existingProject = new Project();
    existingProject.setProjectId("p1");
    when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString())).thenReturn(existingProject);
    when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("DB error"));

    ProjectRequest request = new ProjectRequest();
    assertThrows(ResourceNotFoundException.class,
            () -> projectService.updateProjectByProjectId(request, "p1"));
  }

  @Test
  void testGetProjectByIdAndClientId_Success() {
    Project project = new Project();
    project.setProjectId("project123");
    project.setClientId("client123");

    Client client = new Client();
    client.setClientId("client123");
    client.setClientName("Client ABC");

    when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(anyString(), anyString(), anyString()))
            .thenReturn(project);
    when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString()))
            .thenReturn(client);
    when(accountClient.getEmployeeNamesById(anyList())).thenReturn(Collections.emptyList());

    ProjectDetailViewResponseDTO dto = projectService.getProjectByIdAndClientId("project123", "client123");

    assertEquals("project123", dto.getProjectId());
    assertEquals("Client ABC", dto.getClientName());
  }

  @Test
  void testGetProjectByIdAndClientId_ProjectNotFound() {
    when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(anyString(), anyString(), anyString()))
            .thenReturn(null);

    assertThrows(ResourceNotFoundException.class,
            () -> projectService.getProjectByIdAndClientId("p1", "c1"));
  }

  @Test
  void testGetProjectsByClientIdInOrganization_Success() {
    Project project = new Project();
    project.setProjectId("project123");
    when(projectRepository.findByClientIdAndOrganizationId(anyString(), anyString()))
            .thenReturn(List.of(project));

    List<Project> projects = projectService.getProjectsByClientIdInOrganization("client123");

    assertEquals(1, projects.size());
  }

  @Test
  void testGetProjectsByClientIdInOrganization_Empty() {
    when(projectRepository.findByClientIdAndOrganizationId(anyString(), anyString()))
            .thenReturn(Collections.emptyList());

    List<Project> projects = projectService.getProjectsByClientIdInOrganization("client123");
    assertTrue(projects.isEmpty());
  }

  @Test
  void testChangeProjectStatus_Success() {
    Project project = new Project();
    project.setProjectId("project123");
    project.setStatus(ProjectStatus.IN_PROGRESS);

    when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString()))
            .thenReturn(project);
    when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArguments()[0]);

    Project updated = projectService.changeProjectStatus("project123", ProjectStatus.COMPLETED);
    assertEquals(ProjectStatus.COMPLETED, updated.getStatus());
  }

  @Test
  void testChangeProjectStatus_ProjectNotFound() {
    when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString())).thenReturn(null);

    assertThrows(ResourceNotFoundException.class,
            () -> projectService.changeProjectStatus("p1", ProjectStatus.COMPLETED));
  }

  @Test
  void testValidateAndFetchEmployees_Success() {
    EmployeeNameDTO emp1 = new EmployeeNameDTO("emp1", "John Doe", true);
    EmployeeNameDTO emp2 = new EmployeeNameDTO("emp2", "Jane Doe", false);

    when(accountClient.getEmployeeNamesById(anyList())).thenReturn(List.of(emp1, emp2));

    List<String> result = projectService.validateAndFetchEmployees(List.of("emp1", "emp2"));

    assertEquals(1, result.size());
    assertTrue(result.contains("emp1"));
  }

  @Test
  void testValidateAndFetchEmployees_FeignClientException() {
    when(accountClient.getEmployeeNamesById(anyList()))
            .thenThrow(new FeignClientException("Feign failed"));

    assertThrows(FeignClientException.class,
            () -> projectService.validateAndFetchEmployees(List.of("e1", "e2")));
  }

  @Test
  void testValidateAndFetchEmployees_AllInactive() {
    EmployeeNameDTO emp1 = new EmployeeNameDTO("emp1", "John Doe", false);
    EmployeeNameDTO emp2 = new EmployeeNameDTO("emp2", "Jane Doe", false);

    when(accountClient.getEmployeeNamesById(anyList())).thenReturn(List.of(emp1, emp2));

    List<String> result = projectService.validateAndFetchEmployees(List.of("emp1", "emp2"));
    assertTrue(result.isEmpty());
  }

  @Test
  void testFetchEmployees_WarnForMissingEmployee() {
    EmployeeNameDTO emp = new EmployeeNameDTO("e1", "Employee One", true);
    when(accountClient.getEmployeeNamesById(anyList())).thenReturn(List.of(emp));

    List<EmployeeNameDTO> result = projectService.fetchEmployees(List.of("e1", "e2"), "project123");

    assertEquals(1, result.size());
    assertEquals("e1", result.get(0).getEmployeeId());
  }

  @Test
  void testFetchEmployees_EmptyList() {
    List<EmployeeNameDTO> result = projectService.fetchEmployees(Collections.emptyList(), "p1");
    assertTrue(result.isEmpty());
  }

  @Test
  void testFetchEmployees_AllInactive() {
    EmployeeNameDTO emp1 = new EmployeeNameDTO("e1", "Emp One", false);
    when(accountClient.getEmployeeNamesById(anyList())).thenReturn(List.of(emp1));

    List<EmployeeNameDTO> result = projectService.fetchEmployees(List.of("e1"), "p1");
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetAllProjectsInOrganization_WithPagination() {
    Project project1 = new Project();
    Project project2 = new Project();

    when(mongoTemplate.find(any(Query.class), eq(Project.class))).thenReturn(List.of(project1, project2));

    List<Project> projects = projectService.getAllProjectsInOrganization("org123", 1, 2, null, null);

    assertEquals(2, projects.size());
  }

  @Test
  void testGetAllProjectsInOrganization_WithFilter() {
    Project p1 = new Project();
    p1.setProjectId("p1");
    p1.setStatus(ProjectStatus.COMPLETED);
    p1.setName("FilteredProject");

    when(mongoTemplate.find(any(Query.class), eq(Project.class))).thenReturn(List.of(p1));

    List<Project> projects = projectService.getAllProjectsInOrganization("org123", 1, 10, "FilteredProject", ProjectStatus.COMPLETED);
    assertEquals(1, projects.size());
  }

  @Test
  void testGetAllProjectsInOrganization_EmptyList() {
    when(mongoTemplate.find(any(Query.class), eq(Project.class))).thenReturn(Collections.emptyList());
    List<Project> projects = projectService.getAllProjectsInOrganization("org123", 1, 10, null, null);
    assertTrue(projects.isEmpty());
  }

  @Test
  void testGetTotalProjectsInOrganization() {
    when(mongoTemplate.count(any(Query.class), eq(Project.class))).thenReturn(5L);

    Long total = projectService.getTotalProjectsInOrganization("org123", null, null);
    assertEquals(5, total);
  }

  @Test
  void testGetAllProjects_ResponseDTO() {
    Project p1 = new Project();
    p1.setProjectId("p1");
    p1.setProjectManagers(List.of("emp1"));
    p1.setClientId("c1");

    Project p2 = new Project();
    p2.setProjectId("p2");
    p2.setProjectManagers(List.of("emp2"));
    p2.setClientId("c2");

    when(mongoTemplate.find(any(Query.class), eq(Project.class))).thenReturn(List.of(p1, p2));
    when(accountClient.getEmployeeNamesById(anyList())).thenReturn(List.of(
            new EmployeeNameDTO("emp1", "Emp One", true),
            new EmployeeNameDTO("emp2", "Emp Two", true)
    ));

    when(clientRepository.findByClientIdAndOrganizationId("c1", "org123")).thenReturn(new Client() {{
      setClientName("Client 1");
    }});
    when(clientRepository.findByClientIdAndOrganizationId("c2", "org123")).thenReturn(new Client() {{
      setClientName("Client 2");
    }});

    List result = projectService.getAllProjects("org123", 1, 2, null, null);
    assertEquals(2, result.size());
  }

  @Test
  void testCreateProjectForClient_EmptyEmployeesList() {
    ProjectRequest request = new ProjectRequest();
    request.setClientId("client1");
    request.setProjectManagers(List.of("pm1"));
    request.setProjectResources(List.of("res1"));

    Client client = new Client();
    client.setClientId("client1");
    when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(client);
    when(accountClient.getEmployeeNamesById(anyList())).thenReturn(Collections.emptyList());
    when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArguments()[0]);

    Project result = projectService.createProjectForClient(request);
    assertTrue(result.getProjectManagers().isEmpty());
    assertTrue(result.getProjectResources().isEmpty());
  }

  @Test
  void testCreateProjectForClient_FeignClientException() {
    ProjectRequest request = new ProjectRequest();
    request.setClientId("client1");
    request.setProjectManagers(List.of("pm1"));

    Client client = new Client();
    client.setClientId("client1");
    when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(client);
    when(accountClient.getEmployeeNamesById(anyList())).thenThrow(new FeignClientException("Feign failed"));

    assertThrows(FeignClientException.class, () -> projectService.createProjectForClient(request));
  }

  @Test
  void testValidateAndFetchEmployees_NullList() {
    List<String> result = projectService.validateAndFetchEmployees(null);
    assertTrue(result.isEmpty());
  }

}
