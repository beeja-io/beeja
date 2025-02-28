package com.beeja.api.projectmanagement.serviceImpl;


import com.beeja.api.projectmanagement.client.EmployeeClient;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.exceptions.ClientNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ProjectNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ValidationException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.repository.ResourceRepository;
import com.beeja.api.projectmanagement.responses.EmployeeDetailsResponse;
import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private EmployeeClient employeeClient;

    @Transactional
    public Project addProject(Project project) {
        if (project.getClient() == null || project.getClient().getId() == null) {
            throw new ClientNotFoundException("Client information is required for the project.");
        }

        Client client = clientRepository.findById(project.getClient().getId())
                .orElseThrow(() -> new ClientNotFoundException("Client not found with ID: " + project.getClient().getId()));
        project.setClient(client);
        project.setProjectId(generateNextProjectId());

        List<EmployeeDetailsResponse> employeeDetails;
        try {
            employeeDetails = employeeClient.getEmployeeDetails();
            if (employeeDetails == null || employeeDetails.isEmpty()) {
                throw new IllegalArgumentException("Error fetching employee details. No employees found.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching employee details. Please try again later.", e);
        }

        if (project.getResources() != null && !project.getResources().isEmpty()) {
            project.setResources(getOrCreateResources(project.getResources()));
        }

        if (project.getProjectManagers() != null && !project.getProjectManagers().isEmpty()) {
            project.setProjectManagers(getOrCreateResources(project.getProjectManagers()));
        }

        return projectRepository.save(project);
    }

    private List<Resource> getOrCreateResources(List<Resource> employees) {
        if (employees == null || employees.isEmpty()) return List.of();

        List<String> employeeIds = employees.stream().map(Resource::getEmployeeId).toList();
        List<Resource> existingResources = resourceRepository.findByEmployeeIdIn(employeeIds);

        Map<String, Resource> existingResourceMap = existingResources.stream()
                .collect(Collectors.toMap(Resource::getEmployeeId, resource -> resource));
        List<Resource> finalResources = new ArrayList<>();
        for (String empId : employeeIds) {
            if (existingResourceMap.containsKey(empId)) {
                finalResources.add(existingResourceMap.get(empId));
            } else {
                finalResources.add(new Resource(null, empId));
            }
        }
        List<Resource> newResources = finalResources.stream()
                .filter(resource -> resource.getId() == null)
                .toList();

        if (!newResources.isEmpty()) {
            resourceRepository.saveAll(newResources);
        }

        return finalResources;
    }

    String generateNextProjectId() {
        String prefix = "P-";
        Project lastProject = projectRepository.findTopByOrderByProjectIdDesc();

        if (lastProject == null || lastProject.getProjectId() == null || !lastProject.getProjectId().startsWith(prefix)) {
            return prefix + "001";
        }
        String lastId = lastProject.getProjectId();
        int lastNumber = Integer.parseInt(lastId.substring(2));
        int nextNumber = lastNumber + 1;
        return String.format(prefix + "%03d", nextNumber);
    }

    @Transactional
    public Project updateProject(String projectId, Project updatedProject) {
        ObjectMapper objectMapper = new ObjectMapper();
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        new ErrorResponse(
                                ErrorType.NOT_FOUND,
                                ErrorCode.PROJECT_NOT_FOUND,
                                "Project not found with ID: " + projectId,
                                "v1/projects/update"
                        )));
        if (updatedProject.getProjectName() != null) {
            String projectName = updatedProject.getProjectName().trim();
            if (projectName.isEmpty()) {
                throw new ValidationException(new ErrorResponse(
                        ErrorType.VALIDATION_ERROR,
                        ErrorCode.FIELD_VALIDATION_ERROR,
                        "Project name cannot be empty.",
                        "v1/projects/update"
                ));
            }
            existingProject.setProjectName(projectName);
        }
        if (updatedProject.getClient() != null) {
            String clientId = updatedProject.getClient().getId();
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new ResourceNotFoundException(new ErrorResponse(
                            ErrorType.NOT_FOUND,
                            ErrorCode.CLIENT_NOT_FOUND,
                            "Client not found with ID: " + clientId,
                            "v1/projects/update"
                    )));
            existingProject.setClient(client);
        }
        if (updatedProject.getStartDate() != null) {
            Date startDate = updatedProject.getStartDate();
            if (startDate.before(new Date())) {
                throw new ValidationException(new ErrorResponse(
                        ErrorType.VALIDATION_ERROR,
                        ErrorCode.FIELD_VALIDATION_ERROR,
                        "Start date cannot be in the past.",
                        "v1/projects/update"
                ));
            }
            existingProject.setStartDate(startDate);
        }
        if (updatedProject.getStatus() != null) {
            try {
                ProjectStatus status = updatedProject.getStatus();
                existingProject.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(new ErrorResponse(
                        ErrorType.VALIDATION_ERROR,
                        ErrorCode.FIELD_VALIDATION_ERROR,
                        "Invalid project status value.",
                        "v1/projects/update"
                ));
            }
        }
        if (updatedProject.getDescription() != null) {
            existingProject.setDescription(updatedProject.getDescription());
        }
        if (updatedProject.getResources() != null) {
            try {
                List<Resource> resources = getOrCreateResources(updatedProject.getResources());
                existingProject.setResources(resources);
            } catch (Exception e) {
                throw new ValidationException(new ErrorResponse(
                        ErrorType.VALIDATION_ERROR,
                        ErrorCode.FIELD_VALIDATION_ERROR,
                        "Invalid resource data provided.",
                        "v1/projects/update"
                ));
            }
        }
        if (updatedProject.getProjectManagers() != null) {
            try {
                List<Resource> projectManagers = getOrCreateResources(updatedProject.getProjectManagers());
                existingProject.setProjectManagers(projectManagers);
            } catch (Exception e) {
                throw new ValidationException(new ErrorResponse(
                        ErrorType.VALIDATION_ERROR,
                        ErrorCode.FIELD_VALIDATION_ERROR,
                        "Invalid project manager data provided.",
                        "v1/projects/update"
                ));
            }
        }
        return projectRepository.save(existingProject);
    }

    @Override
    @Transactional(readOnly = true)
    public Project getProjectById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + id));
    }
    @Override
    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        if (projects.isEmpty()) {
            throw new ProjectNotFoundException("No projects available.");
        }

        return projects;
    }


}
