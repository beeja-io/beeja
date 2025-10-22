package com.beeja.api.projectmanagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.serviceImpl.ProjectServiceImpl;
import com.beeja.api.projectmanagement.model.dto.EmployeeNameDTO;

import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.mockito.MockedStatic;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {


    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountClient accountClient;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ContractRepository contractRepository;

    private MockedStatic<com.beeja.api.projectmanagement.utils.UserContext> userContextStatic;

    private Map<String, Object> mockOrg;

    private Project project;

    private Client client;

    @BeforeEach
    void setup() {
        mockOrg = new HashMap<>();
        mockOrg.put("id", "org123");
        userContextStatic = mockStatic(com.beeja.api.projectmanagement.utils.UserContext.class);
        userContextStatic.when(com.beeja.api.projectmanagement.utils.UserContext::getLoggedInUserOrganization)
                .thenReturn(mockOrg);

        project = new Project();
        project.setProjectId("proj123");
        project.setName("Test Project");
        project.setClientId("client123");
        project.setOrganizationId("org123");
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setProjectManagers(List.of("emp1"));
        project.setProjectResources(List.of("emp2"));

        // Sample client
        client = new Client();
        client.setClientId("client123");
        client.setClientName("Test Client");
    }

    @AfterEach
    void tearDown() {
        userContextStatic.close();
    }

    @Test
    void testCreateProjectForClient() {
        ProjectRequest request = new ProjectRequest();
        request.setName("New Project");
        request.setClientId("client123");
        request.setProjectManagers(List.of("emp1"));
        request.setProjectResources(List.of("emp2"));

        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(client);
        when(accountClient.getEmployeeNamesById(anyList())).thenReturn(List.of(
                new EmployeeNameDTO("emp1", "John Doe", true),
                new EmployeeNameDTO("emp2", "Jane Doe", true)
        ));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project result = projectService.createProjectForClient(request);

        assertNotNull(result);
        assertEquals("Test Project", result.getName(), "Project name should match");
    }

    @Test
    void testUpdateProjectByProjectId() {
        ProjectRequest request = new ProjectRequest();
        request.setName("Updated Project");
        request.setProjectManagers(List.of("emp1"));

        when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString())).thenReturn(project);
        when(accountClient.getEmployeeNamesById(anyList())).thenReturn(List.of(
                new EmployeeNameDTO("emp1", "John Doe", true)
        ));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project updated = projectService.updateProjectByProjectId(request, "proj123");

        assertNotNull(updated);
        assertEquals("Updated Project", updated.getName());
    }

    @Test
    void testChangeProjectStatus() {
        when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString())).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project updated = projectService.changeProjectStatus("proj123", ProjectStatus.COMPLETED);

        assertNotNull(updated);
        assertEquals(ProjectStatus.COMPLETED, updated.getStatus());
    }

    @Test
    void testGetProjectByIdAndClientId_success() {
        Project project = new Project();
        project.setProjectId("proj123");
        project.setName("Test Project");
        project.setClientId("client123");
        project.setOrganizationId("org123");
        project.setProjectManagers(List.of("emp1"));
        project.setProjectResources(List.of("emp2"));

        Client client = new Client();
        client.setClientId("client123");
        client.setClientName("Test Client");

        when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(anyString(), anyString(), anyString()))
                .thenReturn(project);
        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(client);

        when(contractRepository.findByProjectIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        when(accountClient.getEmployeeNamesById(Mockito.anyList())).thenAnswer(invocation -> {
            List<String> ids = invocation.getArgument(0);
            List<EmployeeNameDTO> result = new ArrayList<>();
            if (ids.contains("emp1")) result.add(new EmployeeNameDTO("emp1", "John Doe", true));
            if (ids.contains("emp2")) result.add(new EmployeeNameDTO("emp2", "Jane Doe", true));
            return result;
        });

        var dto = projectService.getProjectByIdAndClientId("proj123", "client123");

        assertNotNull(dto);
        assertEquals("Test Project", dto.getName());
        assertEquals(List.of("emp1"), dto.getProjectManagerIds());
        assertEquals(List.of("emp2"), dto.getProjectResourceIds());
        assertEquals(List.of("John Doe"), dto.getProjectManagerNames());
        assertEquals(List.of("Jane Doe"), dto.getProjectResourceNames());
    }





    @Test
    void testGetAllProjectsInOrganization() {
        when(mongoTemplate.find(any(Query.class), eq(Project.class))).thenReturn(List.of(project));

        List<Project> projects = projectService.getAllProjectsInOrganization("org123", 1, 10, null, ProjectStatus.IN_PROGRESS);

        assertNotNull(projects);
        assertEquals(1, projects.size());
    }

    @Test
    void testGetTotalProjectsInOrganization() {
        when(mongoTemplate.count(any(Query.class), eq(Project.class))).thenReturn(1L);

        Long total = projectService.getTotalProjectsInOrganization("org123", null, ProjectStatus.IN_PROGRESS);

        assertEquals(1L, total);
    }

    @Test
    void testGetEmployeesByProjectId() {
        project.setProjectManagers(List.of("emp1"));
        project.setProjectResources(List.of("emp2"));

        when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(project);

        when(accountClient.getEmployeeNamesById(List.of("emp1"))).thenReturn(List.of(
                new EmployeeNameDTO("emp1", "John Doe", true)
        ));
        when(accountClient.getEmployeeNamesById(List.of("emp2"))).thenReturn(List.of(
                new EmployeeNameDTO("emp2", "Jane Doe", true)
        ));

        var dto = projectService.getEmployeesByProjectId("proj123");

        assertNotNull(dto);
        assertEquals(1, dto.getManagers().size());
        assertEquals(1, dto.getResources().size());
        assertEquals("John Doe", dto.getManagers().get(0).getFullName());
        assertEquals("Jane Doe", dto.getResources().get(0).getFullName());
    }

}
