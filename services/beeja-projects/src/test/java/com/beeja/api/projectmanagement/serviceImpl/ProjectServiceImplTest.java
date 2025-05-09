package com.beeja.api.projectmanagement.serviceImpl;


import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class ProjectServiceImplTest {

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ClientRepository clientRepository;

    private static Map<String, Object> orgMap;
    private static MockedStatic<UserContext> userContextMock;

    @BeforeAll
    static void init() {
        orgMap = new HashMap<>();
        orgMap.put("id", "org123");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userContextMock = mockStatic(UserContext.class);
        when(UserContext.getLoggedInUserOrganization()).thenReturn(orgMap);
    }

    @AfterEach
    void closeStaticMock() {
        userContextMock.close();
    }

    @Test
    void testCreateProjectForClient_success() {
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Test Project");
        projectRequest.setClientId("client123");
        projectRequest.setDescription("Test Description");

        Client client = new Client();
        client.setClientId("client123");

        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(client);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0)); // no need to manually set description

        Project result = projectService.createProjectForClient(projectRequest);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        assertEquals("Test Description", result.getDescription());

        verify(clientRepository, times(1)).findByClientIdAndOrganizationId(anyString(), anyString());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testCreateProjectForClient_clientNotFound() {
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Test Project");
        projectRequest.setClientId("client123");

        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> projectService.createProjectForClient(projectRequest));

        assertTrue(exception.getMessage().contains("Client Not Found"));
    }

    @Test
    void testCreateProjectForClient_dbError() {
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Test Project");
        projectRequest.setClientId("client123");

        Client client = new Client();
        client.setClientId("client123");

        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(client);
        when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("DB error"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> projectService.createProjectForClient(projectRequest));

        assertTrue(exception.getMessage().contains("Project Not Saved"));
    }

    @Test
    void testCreateProjectForClient_nullRequest_shouldThrowException() {
        assertThrows(NullPointerException.class,
                () -> projectService.createProjectForClient(null));
    }

    @Test
    void testGetProjectByIdAndClientId_success() {
        Project project = new Project();
        project.setProjectId("project123");

        when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(anyString(), anyString(), anyString())).thenReturn(project);

        Project result = projectService.getProjectByIdAndClientId("project123", "client123");

        assertNotNull(result);
        assertEquals("project123", result.getProjectId());
    }

    @Test
    void testGetProjectByIdAndClientId_projectNotFound() {
        when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(anyString(), anyString(), anyString())).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> projectService.getProjectByIdAndClientId("project123", "client123"));

        assertTrue(exception.getMessage().contains("Project Not Found"));
    }

    @Test
    void testGetProjectsByClientIdInOrganization_success() {
        List<Project> projects = Arrays.asList(new Project(), new Project());

        when(projectRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(projects);

        List<Project> result = projectService.getProjectsByClientIdInOrganization("client123");

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetProjectsByClientIdInOrganization_projectsNotFound_returnsEmptyList() {
        when(projectRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(List.of());

        List<Project> result = projectService.getProjectsByClientIdInOrganization("client123");

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected an empty list when no projects are found");
    }

    @Test
    void testGetAllProjectsInOrganization_success() {
        List<Project> projects = Arrays.asList(new Project(), new Project());

        when(projectRepository.findByOrganizationId(anyString())).thenReturn(projects);

        List<Project> result = projectService.getAllProjectsInOrganization();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllProjectsInOrganization_noProjects_returnsEmptyList() {
        when(projectRepository.findByOrganizationId(anyString())).thenReturn(List.of());

        List<Project> result = projectService.getAllProjectsInOrganization();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty list when no projects found");
    }

    @Test
    void testUpdateProjectByProjectId_success() {
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Updated Project");
        projectRequest.setDescription("Updated Desc");

        Project existingProject = new Project();
        existingProject.setProjectId("project123");

        when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString())).thenReturn(existingProject);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project result = projectService.updateProjectByProjectId(projectRequest, "project123");

        assertNotNull(result);
        assertEquals("Updated Project", result.getName());
        assertEquals("Updated Desc", result.getDescription());

        verify(projectRepository, times(1)).findByProjectIdAndOrganizationId(anyString(), anyString());
        verify(projectRepository, times(1)).save(any(Project.class));
    }


    @Test
    void testUpdateProjectByProjectId_projectNotFound() {
        when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString())).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> projectService.updateProjectByProjectId(new ProjectRequest(), "project123"));

        assertTrue(exception.getMessage().contains("Project Not Found"));
    }

    @Test
    void testUpdateProjectByProjectId_dbError() {
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Updated Project");

        Project existingProject = new Project();
        existingProject.setProjectId("project123");

        when(projectRepository.findByProjectIdAndOrganizationId(anyString(), anyString())).thenReturn(existingProject);
        when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("DB error"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> projectService.updateProjectByProjectId(projectRequest, "project123"));

        assertTrue(exception.getMessage().contains("Project Not Updated"));
    }
}