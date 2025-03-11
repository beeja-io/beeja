//package com.beeja.api.projectmanagement.serviceImpl;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import com.beeja.api.projectmanagement.client.EmployeeClient;
//import com.beeja.api.projectmanagement.enums.ProjectStatus;
//import com.beeja.api.projectmanagement.exceptions.ClientNotFoundException;
//import com.beeja.api.projectmanagement.exceptions.ProjectNotFoundException;
//import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
//import com.beeja.api.projectmanagement.exceptions.ValidationException;
//import com.beeja.api.projectmanagement.model.Client;
//import com.beeja.api.projectmanagement.model.Project;
//import com.beeja.api.projectmanagement.model.Resource;
//import com.beeja.api.projectmanagement.repository.ClientRepository;
//import com.beeja.api.projectmanagement.repository.ProjectRepository;
//import com.beeja.api.projectmanagement.repository.ResourceRepository;
//import com.beeja.api.projectmanagement.responses.EmployeeDetailsResponse;
//
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.ResponseEntity;
//
//import java.util.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProjectServiceImplTest {
//
//
//    @Mock
//    private ProjectRepository projectRepository;
//
//    @Mock
//    private ClientRepository clientRepository;
//
//    @Mock
//    private ResourceRepository resourceRepository;
//
//    @Mock
//    private EmployeeClient employeeClient;
//
//    @InjectMocks
//    private ProjectServiceImpl projectService;
//
//    private Project existingProject;
//    private Project updatedProject;
//    private Client client;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        existingProject = new Project();
//        existingProject.setId("1");
//        existingProject.setProjectName("Old Project");
//        existingProject.setStartDate(new Date(System.currentTimeMillis() + 86400000));
//        existingProject.setStatus(ProjectStatus.IN_PROGRESS);
//        existingProject.setDescription("Old Description");
//
//        client = new Client();
//        client.setId("100");
//
//        updatedProject = new Project();
//        updatedProject.setProjectName("Updated Project");
//        updatedProject.setStartDate(new Date(System.currentTimeMillis() + 86400000));
//        updatedProject.setStatus(ProjectStatus.COMPLETED);
//        updatedProject.setDescription("Updated Description");
//        updatedProject.setClient(client);
//    }
//
////    @Test
////    void addProject_Success() {
////        Client client = new Client();
////        client.setId("C123");
////
////        Project project = new Project();
////        project.setClient(client);
////        project.setResources(List.of(new Resource(null, "E1"), new Resource(null, "E2")));
////        project.setProjectManagers(List.of(new Resource(null, "M1")));
////
////        List<EmployeeDetailsResponse> employeeDetails = List.of(
////                new EmployeeDetailsResponse("O1", "E1", "Emp One", "emp1@mail.com", "pic1"),
////                new EmployeeDetailsResponse("O2", "E2", "Emp Two", "emp2@mail.com", "pic2"),
////                new EmployeeDetailsResponse("O3", "M1", "Mgr One", "mgr1@mail.com", "pic3")
////        );
//
//        when(clientRepository.findById("C123")).thenReturn(Optional.of(client));
//        when(employeeClient.getEmployeeDetails()).thenReturn(ResponseEntity.ok(employeeDetails));
//        when(projectRepository.findTopByOrderByProjectIdDesc()).thenReturn(null);
//        when(resourceRepository.findByEmployeeIdIn(anyList())).thenReturn(new ArrayList<>());
//        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));
//        Project savedProject = projectService.addProject(project);
//        assertNotNull(savedProject);
//        assertEquals("P-001", savedProject.getProjectId());
//        verify(projectRepository, times(1)).save(any(Project.class));
//    }
//
//    @Test
//    void addProject_ShouldThrowException_WhenClientIsMissing() {
//        Project project = new Project();
//        ClientNotFoundException exception = assertThrows(ClientNotFoundException.class, () -> {
//            projectService.addProject(project);
//        });
//        assertEquals("Client information is required for the project.", exception.getMessage());
//    }
//
//    @Test
//    void testGenerateNextProjectId_NoExistingProjects() {
//        when(projectRepository.findTopByOrderByProjectIdDesc()).thenReturn(null);
//        String projectId = projectService.generateNextProjectId();
//        assertEquals("P-001", projectId);
//    }
//
//    @Test
//    void testGenerateNextProjectId_LastProjectHasNullId() {
//        when(projectRepository.findTopByOrderByProjectIdDesc()).thenReturn(new Project());
//        String projectId = projectService.generateNextProjectId();
//        assertEquals("P-001", projectId);
//    }
//
//    @Test
//    void testUpdateProject_SuccessfulUpdate() {
//        when(projectRepository.findById("1")).thenReturn(Optional.of(existingProject));
//        when(clientRepository.findById("100")).thenReturn(Optional.of(client));
//        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);
//
//        Project result = projectService.updateProject("1", updatedProject);
//
//        assertNotNull(result);
//        assertEquals("Updated Project", result.getProjectName());
//        assertEquals("Updated Description", result.getDescription());
//        assertEquals(ProjectStatus.COMPLETED, result.getStatus());
//        assertEquals(client, result.getClient());
//    }
//
//    @Test
//    void testUpdateProject_ProjectNotFound() {
//        when(projectRepository.findById("1")).thenReturn(Optional.empty());
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.updateProject("1", updatedProject);
//        });
//        assertEquals("Project not found with ID: 1", exception.getErrorResponse().getMessage());
//    }
//
//
//    @Test
//    void testUpdateProject_EmptyProjectName() {
//        updatedProject.setProjectName(" ");
//        when(projectRepository.findById("1")).thenReturn(Optional.of(existingProject));
//
//        ValidationException exception = assertThrows(ValidationException.class, () -> {
//            projectService.updateProject("1", updatedProject);
//        });
//
//        assertEquals("Project name cannot be empty.", exception.getErrorResponse().getMessage());
//    }
//
//
//    @Test
//    void testUpdateProject_ClientNotFound() {
//        when(projectRepository.findById("1")).thenReturn(Optional.of(existingProject));
//        when(clientRepository.findById("100")).thenReturn(Optional.empty());
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.updateProject("1", updatedProject);
//        });
//
//        assertEquals("Client not found with ID: 100", exception.getErrorResponse().getMessage());
//    }
//
//    @Test
//    void testUpdateProject_InvalidStartDate() {
//        // Set an invalid past start date
//        updatedProject.setStartDate(new Date(System.currentTimeMillis() - 86400000)); // Past date
//
//        // Mock dependencies
//        when(projectRepository.findById("1")).thenReturn(Optional.of(existingProject));
//        when(clientRepository.findById("100")).thenReturn(Optional.of(client)); // Ensure client exists
//
//        // Assert that a ValidationException is thrown for past start date
//        assertThrows(ValidationException.class, () -> projectService.updateProject("1", updatedProject));
//    }
//
//
//
//    @Test
//    void testUpdateProject_SaveThrowsException() {
//        when(projectRepository.findById("1")).thenReturn(Optional.of(existingProject));
//        when(clientRepository.findById("100")).thenReturn(Optional.of(client));
//        when(projectRepository.save(any(Project.class))).thenThrow(new DataIntegrityViolationException("Database Error"));
//
//        assertThrows(DataIntegrityViolationException.class, () -> projectService.updateProject("1", updatedProject));
//    }
//
//    @Test
//    void testGetProjectById_Success() {
//        // Given
//        String projectId = "123";
//        Project project = new Project();
//        project.setId(projectId);
//        project.setProjectName("Test Project");
//
//        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
//
//        // When
//        Project result = projectService.getProjectById(projectId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(projectId, result.getId());
//        assertEquals("Test Project", result.getProjectName());
//    }
//
//    @Test
//    void testGetProjectById_ProjectNotFound() {
//        // Given
//        String projectId = "999";
//        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(projectId));
//    }
//
//    @Test
//    void testGetAllProjects_Success() {
//        // Given
//        Project project1 = new Project();
//        project1.setId("1");
//        project1.setClient(client); // Setting a valid client
//        project1.setProjectName("Project A");
//
//        Project project2 = new Project();
//        project2.setId("2");
//        project2.setClient(client);
//        project2.setProjectName("Project B");
//
//        List<Project> projects = List.of(project1, project2);
//
//        Mockito.when(projectRepository.findAll()).thenReturn(projects);
//
//        // When
//        List<Project> result = projectService.getAllProjects();
//
//        // Then
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals("Project A", result.get(0).getProjectName());
//        assertEquals("Project B", result.get(1).getProjectName());
//    }
//
//    @Test
//    void testGetAllProjects_NoProjectsFound() {
//        // Given
//        Mockito.when(projectRepository.findAll()).thenReturn(Collections.emptyList());
//
//        // When & Then
//        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class, () -> {
//            projectService.getAllProjects();
//        });
//
//        assertEquals("No projects available.", exception.getMessage());
//    }
//
//    @Test
//    void testGetAllProjects_DatabaseError() {
//        // Given
//        Mockito.when(projectRepository.findAll()).thenThrow(new RuntimeException("Database error"));
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            projectService.getAllProjects();
//        });
//
//        assertEquals("Database error", exception.getMessage());
//    }
//
//
//}
