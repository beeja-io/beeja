package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.service.ProjectService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Override
    public Project createProjectForClient(ProjectRequest project) {
        Client client = clientRepository.findByClientIdAndOrganizationId(project.getClientId(), UserContext.getLoggedInUserOrganization().get("id").toString());
        if(client == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.CLIENT_NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Client Not Found with provided clientId"
                    )
            );
        }
        Project newProject = new Project();
        if(project.getName() != null) {
            newProject.setName(project.getName());
        }
        if(project.getDescription() != null) {
            newProject.setDescription(project.getDescription());
        }
        if(project.getStatus() != null) {
            newProject.setStatus(project.getStatus());
        }
        if(project.getStartDate() != null) {
            newProject.setStartDate(project.getStartDate());
        }
        if(project.getEndDate() != null) {
            newProject.setEndDate(project.getEndDate());
        }
        if(project.getClientId() != null){
            newProject.setClientId(project.getClientId());
        }
        newProject.setOrganizationId(UserContext.getLoggedInUserOrganization().get("id").toString());

//        FIXME:  PROJECT ID GENERATION
        newProject.setProjectId(UUID.randomUUID().toString().toUpperCase().substring(0,6));
        try{
            newProject = projectRepository.save(newProject);
        } catch (Exception e) {
           log.error("Unable to add project to DB: {}", e.getMessage());
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_CREATION_ERROR,
                            "Project Not Saved with provided projectId"
                    )
            );
        }
        return newProject;
    }

    @Override
    public Project getProjectByIdAndClientId(String projectId, String clientId) {
        Project project;
        try{
            project = projectRepository.findByProjectIdAndClientIdAndOrganizationId(projectId, clientId,  UserContext.getLoggedInUserOrganization().get("id").toString());
        } catch (Exception e) {
            log.error("Error while fetching project: {}", e.getMessage());
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Project Not Found with provided projectId & corresponding clientId"
                    )
            );
        }
        if (project == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Project Not Found with provided projectId"
                    )
            );
        }
        return project;
    }

    @Override
    public List<Project> getProjectsByClientIdInOrganization(String clientId) {
        List<Project> projects;
        try{
            projects = projectRepository.findByClientIdAndOrganizationId(clientId, UserContext.getLoggedInUserOrganization().get("id").toString());
        } catch (Exception e) {
            log.error("Error while fetching projects: {}", e.getMessage());
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Error while fetching projects with provided clientId"
                    )
            );
        }
        if (projects == null || projects.isEmpty()) {
            return List.of();
        }
        return projects;
    }

    @Override
    public List<Project> getAllProjectsInOrganization() {
        List<Project> projects;
        try{
            projects = projectRepository.findByOrganizationId(UserContext.getLoggedInUserOrganization().get("id").toString());
        } catch (Exception e) {
            log.error("Error while fetching projects: {}", e.getMessage());
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Error while fetching projects with provided organizationId"
                    )
            );
        }
        if (projects == null || projects.isEmpty()) {
            return List.of();
        }
        return projects;
    }

    @Override
    public Project updateProjectByProjectId(ProjectRequest project, String projectId) {
        Project existingProject;
        try{
            existingProject = projectRepository.findByProjectIdAndOrganizationId(projectId, UserContext.getLoggedInUserOrganization().get("id").toString());
        } catch (Exception e) {
            log.error("Error while fetching project: {}", e.getMessage());
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Error While Fetching project with provided projectId"
                    )
            );
        }
        if (existingProject == null) {
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "Project Not Found with provided projectId"
                    )
            );
        }
        if(project.getName() != null) {
            existingProject.setName(project.getName());
        }
        if(project.getDescription() != null) {
            existingProject.setDescription(project.getDescription());
        }
        if(project.getStatus() != null) {
            existingProject.setStatus(project.getStatus());
        }
        if(project.getStartDate() != null) {
            existingProject.setStartDate(project.getStartDate());
        }
        if(project.getEndDate() != null) {
            existingProject.setEndDate(project.getEndDate());
        }
        try{
            existingProject = projectRepository.save(existingProject);
        } catch (Exception e) {
            log.error("Unable to update project in DB: {}", e.getMessage());
            throw new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.DB_ERROR,
                            ErrorCode.RESOURCE_CREATION_ERROR,
                            "Project Not Updated with provided projectId"
                    )
            );
        }
        return existingProject;
    }
}
