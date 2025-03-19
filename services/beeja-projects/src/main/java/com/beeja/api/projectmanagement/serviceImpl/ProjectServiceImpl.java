package com.beeja.api.projectmanagement.serviceImpl;


import com.beeja.api.projectmanagement.client.EmployeeClient;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.*;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.service.ProjectService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ResourceServiceImpl resourceService;

    @Autowired
    private EmployeeClient employeeClient;

    @Override
    public Project addProject(Project project) {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

        if (project.getClient() == null || project.getClient().getId() == null) {
            throw new ClientNotFoundException("Client information is required for the project.");
        }

        Client client = clientRepository.findById(project.getClient().getId())
                .orElseThrow(() -> new ClientNotFoundException("Client not found with ID: " + project.getClient().getId()));

        project.setClient(client);
        project.setOrganizationId(organizationId);
        project.setProjectId(generateNextProjectId());

        if ((project.getResources() != null && !project.getResources().isEmpty()) ||
                (project.getProjectManagers() != null && !project.getProjectManagers().isEmpty())) {

            List<String> employeeIds = Stream.concat(
                    project.getResources() != null ? project.getResources().stream().map(Resource::getEmployeeId) : Stream.empty(),
                    project.getProjectManagers() != null ? project.getProjectManagers().stream().map(Resource::getEmployeeId) : Stream.empty()
            ).distinct().collect(Collectors.toList());

            if (!employeeIds.isEmpty()) {
                Map<String, String> employeeNamesMap = resourceService.getEmployeeNamesByIds(employeeIds);

                if (project.getResources() != null && !project.getResources().isEmpty()) {
                    List<Resource> resources = resourceService.getOrCreateResources(project.getResources());
                    resources.forEach(resource -> resource.setFirstName(employeeNamesMap.get(resource.getEmployeeId())));
                    project.setResources(resources);
                }

                if (project.getProjectManagers() != null && !project.getProjectManagers().isEmpty()) {
                    List<Resource> projectManagers = resourceService.getOrCreateResources(project.getProjectManagers());
                    projectManagers.forEach(manager -> manager.setFirstName(employeeNamesMap.get(manager.getEmployeeId())));
                    project.setProjectManagers(projectManagers);
                }
            }
        }

        return projectRepository.save(project);
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


    @Override
    public Project getProjectById(String id) {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

        Project project = projectRepository.findByIdAndOrganizationId(id, organizationId);
        if (project == null) {
            throw new ResourceNotFoundException(new ErrorResponse(
                            ErrorType.NOT_FOUND,
                            ErrorCode.PROJECT_NOT_FOUND,
                            "Project not found.",
                            "v1/projects/get"
            ));
        }

        return project;
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
public Project updateProject(String projectId, Project updatedProject) {
    String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
    Project existingProject = projectRepository.findByIdAndOrganizationId(projectId, organizationId);

    if (existingProject == null) {
        throw new ResourceNotFoundException(new ErrorResponse(
                        ErrorType.NOT_FOUND,
                        ErrorCode.PROJECT_NOT_FOUND,
                        "Project not found.",
                        "v1/projects/update"
                )
        );
    }

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
        existingProject.setStatus(updatedProject.getStatus());
    }

    if (updatedProject.getDescription() != null) {
        existingProject.setDescription(updatedProject.getDescription());
    }
    if (updatedProject.getResources() != null) {
        if (updatedProject.getResources().isEmpty()) {
            existingProject.setResources(null);
        } else {
            List<Resource> resources = resourceService.getOrCreateResources(updatedProject.getResources());
            List<String> employeeIds = resources.stream()
                    .map(Resource::getEmployeeId)
                    .distinct()
                    .collect(Collectors.toList());

            if (!employeeIds.isEmpty()) {
                Map<String, String> employeeNamesMap = resourceService.getEmployeeNamesByIds(employeeIds);
                resources.forEach(resource -> resource.setFirstName(employeeNamesMap.get(resource.getEmployeeId())));
            }

            existingProject.setResources(resources);
        }
    }
    if (updatedProject.getProjectManagers() != null) {
        if (updatedProject.getProjectManagers().isEmpty()) {
            existingProject.setProjectManagers(null);
        } else {
            List<Resource> projectManagers = resourceService.getOrCreateResources(updatedProject.getProjectManagers());

            List<String> managerIds = projectManagers.stream()
                    .map(Resource::getEmployeeId)
                    .distinct()
                    .collect(Collectors.toList());

            if (!managerIds.isEmpty()) {
                Map<String, String> employeeNamesMap = resourceService.getEmployeeNamesByIds(managerIds);
                projectManagers.forEach(manager -> manager.setFirstName(employeeNamesMap.get(manager.getEmployeeId())));
            }

            existingProject.setProjectManagers(projectManagers);
        }
    }

    return projectRepository.save(existingProject);
}


}
