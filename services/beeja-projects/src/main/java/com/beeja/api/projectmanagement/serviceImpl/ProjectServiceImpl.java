package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.exceptions.FeignClientException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.dto.EmployeeNameDTO;
import com.beeja.api.projectmanagement.model.dto.ResourceAllocation;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ProjectRequest;
import com.beeja.api.projectmanagement.responses.*;
import com.beeja.api.projectmanagement.service.ProjectService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ProjectService} interface.
 *
 * <p>This service handles the business logic for managing {@link Project} entities within an
 * organization, including creation, retrieval, and updating of projects associated with clients.
 */
@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

  @Autowired ClientRepository clientRepository;

  @Autowired ProjectRepository projectRepository;

    @Autowired
    AccountClient accountClient;

    @Autowired
  MongoTemplate mongoTemplate;

    @Autowired
    ContractRepository contractRepository;
  /**
   * Creates a new {@link Project} for a given {@link Client} based on the provided {@link
   * ProjectRequest}.
   *
   * @param employeeIds the {@link ProjectRequest} containing the details to create the {@link Project}
   * @return the newly created {@link Project}
   * @throws ResourceNotFoundException if the {@link Client} is not found with the provided clientId
   */

  public List<String> validateAndFetchEmployees(List<String> employeeIds) {
    if (employeeIds != null && !employeeIds.isEmpty()) {
      try {
        List<EmployeeNameDTO> employeeDTOs = accountClient.getEmployeeNamesById(employeeIds);
        List<String> inactiveEmployees = employeeDTOs.stream()
                .filter(dto -> !dto.isActive())
                .map(EmployeeNameDTO::getEmployeeId)
                .collect(Collectors.toList());

        if (!inactiveEmployees.isEmpty()) {
          log.warn("Some employees are inactive and excluded: {}", inactiveEmployees);
        }
        return employeeDTOs.stream()
                .filter(EmployeeNameDTO::isActive)
                .map(EmployeeNameDTO::getEmployeeId)
                .collect(Collectors.toList());

      } catch (FeignClientException e) {
        log.error("Error while validating employees: {}", e.getMessage(), e);
        throw new FeignClientException(Constants.SOMETHING_WENT_WRONG);
      }
    }
    return Collections.emptyList();
  }
  @Override
  public Project createProjectForClient(ProjectRequest project) {
    Client client =
        clientRepository.findByClientIdAndOrganizationId(
            project.getClientId(),
            UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
    if (client == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.CLIENT_NOT_FOUND,
              ErrorCode.RESOURCE_NOT_FOUND,
              Constants.CLIENT_NOT_FOUND));
    }
    Project newProject = new Project();
    if (project.getName() != null) {
      newProject.setName(project.getName());
    }
    if (project.getDescription() != null) {
      newProject.setDescription(project.getDescription());
    }
    if (project.getStartDate() != null) {
      newProject.setStartDate(project.getStartDate());
    }
    if (project.getEndDate() != null) {
      newProject.setEndDate(project.getEndDate());
    }
    if (project.getClientId() != null) {
      newProject.setClientId(project.getClientId());
    }
    newProject.setStatus(ProjectStatus.IN_PROGRESS);
    if(project.getProjectManagers() != null && !project.getProjectManagers().isEmpty()){
      try {
        List<String> validProjectManagers = validateAndFetchEmployees(project.getProjectManagers());
        newProject.setProjectManagers(validProjectManagers);
      } catch (FeignClientException e){
        log.error(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS,e.getMessage(), e);
        throw new FeignClientException(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS);
      }
    }
    if(project.getProjectResources() != null && !project.getProjectResources().isEmpty()){
      try {
        List<String> validProjectResources =  validateAndFetchEmployees(project.getProjectResources());
        newProject.setProjectResources(validProjectResources);
      } catch (FeignClientException e){
        log.error(Constants.ERROR_IN_VALIDATE_PROJECT_RESOURCES,e.getMessage(), e);
        throw new FeignClientException(Constants.ERROR_IN_VALIDATE_PROJECT_RESOURCES);
      }
    }
    newProject.setOrganizationId(
        UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());

    //        TODO:  PROJECT ID GENERATION
    newProject.setProjectId(UUID.randomUUID().toString().toUpperCase().substring(0, 6));
    try {
      newProject = projectRepository.save(newProject);
    } catch (Exception e) {
      log.error(Constants.ERROR_SAVING_PROJECT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_CREATION_ERROR,
              Constants.ERROR_SAVING_PROJECT));
    }
    return newProject;
  }

  /**
   * Retrieves a {@link Project} by its unique identifier and associated {@link Client} ID.
   *
   * @param projectId the unique identifier of the {@link Project}
   * @param clientId the unique identifier of the {@link Client}
   * @return the {@link Project} entity
   * @throws ResourceNotFoundException if no {@link Project} is found with the provided projectId
   *     and clientId
   */
  @Override
  public ProjectDetailViewResponseDTO getProjectByIdAndClientId(String projectId, String clientId) {
    String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();

    Project project;
    try {
      project = projectRepository.findByProjectIdAndClientIdAndOrganizationId(projectId, clientId, organizationId);
    } catch (Exception e) {
      log.error(Constants.PROJECT_NOT_FOUND_WITH_CLIENT, e.getMessage());
      throw new ResourceNotFoundException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.DB_ERROR,
                      ErrorCode.RESOURCE_NOT_FOUND,
                      Constants.PROJECT_NOT_FOUND_WITH_CLIENT));
    }

    if (project == null) {
      throw new ResourceNotFoundException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.DB_ERROR,
                      ErrorCode.RESOURCE_NOT_FOUND,
                      Constants.PROJECT_NOT_FOUND));
    }

    List<String> projectManagerIds = Optional.ofNullable(project.getProjectManagers()).orElse(Collections.emptyList());
    List<String> projectResourceIds = Optional.ofNullable(project.getProjectResources()).orElse(Collections.emptyList());

    List<Contract> contracts = contractRepository.findByProjectIdAndOrganizationId(projectId, organizationId);

    Set<String> contractManagerIds = contracts.stream()
            .flatMap(contract -> Optional.ofNullable(contract.getProjectManagers()).orElse(Collections.emptyList()).stream())
            .collect(Collectors.toSet());

    Set<String> contractResourceIds = contracts.stream()
            .flatMap(contract -> Optional.ofNullable(contract.getProjectResources()).orElse(Collections.emptyList()).stream())
            .map(ResourceAllocation::getEmployeeId)
            .collect(Collectors.toSet());

    Set<String> allEmployeeIds = new HashSet<>();
    allEmployeeIds.addAll(projectManagerIds);
    allEmployeeIds.addAll(projectResourceIds);
    allEmployeeIds.addAll(contractManagerIds);
    allEmployeeIds.addAll(contractResourceIds);

    Map<String, String> idToNameMap;
    if (!allEmployeeIds.isEmpty()) {
      List<EmployeeNameDTO> employeeDetails = accountClient.getEmployeeNamesById(new ArrayList<>(allEmployeeIds));
      log.info("Fetched employee details: {}", employeeDetails);
      idToNameMap = employeeDetails.stream()
              .collect(Collectors.toMap(EmployeeNameDTO::getEmployeeId, EmployeeNameDTO::getFullName));
    } else {
      idToNameMap = new HashMap<>();
    }
    List<ProjectManagerView> projectManagerViews = projectManagerIds.stream()
            .map(id -> new ProjectManagerView(id, idToNameMap.getOrDefault(id, "N/A"), null))
            .collect(Collectors.toList());

    List<ContractView> contractViews = new ArrayList<>();
    for (Contract contract : contracts) {
      List<ProjectManagerView> contractManagers = Optional.ofNullable(contract.getProjectManagers())
              .orElse(Collections.emptyList())
              .stream()
              .map(id -> new ProjectManagerView(id, idToNameMap.getOrDefault(id, "N/A"), contract.getContractTitle()))
              .collect(Collectors.toList());

      ContractView contractView = new ContractView();
      contractView.setContractId(contract.getContractId());
      contractView.setName(contract.getContractTitle());
      contractView.setStatus(contract.getStatus());
      contractView.setStartDate(contract.getStartDate());
      contractView.setProjectManagers(contractManagers);

      contractViews.add(contractView);
    }

    List<ResourceView> resourceViews = new ArrayList<>();
    for (Contract contract : contracts) {
      contract.setProjectResources(contract.normalizeProjectResources(contract.getRawProjectResources()));
      List<ResourceAllocation> allocations = Optional.ofNullable(contract.getProjectResources()).orElse(Collections.emptyList());
      for (ResourceAllocation resource : allocations) {
        ResourceView rv = new ResourceView();
        rv.setEmployeeId(resource.getEmployeeId());
        rv.setName(idToNameMap.getOrDefault(resource.getEmployeeId(), "N/A"));
        rv.setContractName(contract.getContractTitle());
        rv.setAllocationPercentage(resource.getAllocationPercentage());
        resourceViews.add(rv);
      }
    }

    List<String> projectManagerNames = projectManagerIds.stream()
            .map(idToNameMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    List<String> projectResourceNames = projectResourceIds.stream()
            .map(idToNameMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    String clientName = null;
    Client client = clientRepository.findByClientIdAndOrganizationId(clientId, organizationId);
    if (client != null) {
      clientName = client.getClientName();
    }

    ProjectDetailViewResponseDTO dto = new ProjectDetailViewResponseDTO();
    dto.setProjectId(project.getProjectId());
    dto.setName(project.getName());
    dto.setDescription(project.getDescription());
    dto.setStatus(project.getStatus());
    dto.setStartDate(project.getStartDate());
    dto.setEndDate(project.getEndDate());
    dto.setClientId(project.getClientId());
    dto.setClientName(clientName);
    dto.setBillingCurrency(project.getBillingCurrency());
    dto.setClientContact((client.getContact()));
    dto.setClientIndustries(client.getIndustry());
    dto.setClientEmail(client.getEmail());
    dto.setClientLogId(client.getLogoId());


    dto.setProjectManagerIds(projectManagerIds);
    dto.setProjectManagerNames(projectManagerNames);
    dto.setProjectResourceIds(projectResourceIds);
    dto.setProjectResourceNames(projectResourceNames);

    dto.setProjectManagers(projectManagerViews);
    dto.setContracts(contractViews);
    dto.setResources(resourceViews);

    return dto;
  }





  /**
   * Retrieves a list of {@link Project} entities associated with a specific {@link Client} in the
   * organization.
   *
   * @param clientId the unique identifier of the {@link Client}
   * @return a list of {@link Project} entities
   * @throws ResourceNotFoundException if no {@link Project} is found for the provided clientId
   */
  @Override
  public List<Project> getProjectsByClientIdInOrganization(String clientId) {
    List<Project> projects;
    try {
      projects =
          projectRepository.findByClientIdAndOrganizationId(
              clientId, UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
    } catch (Exception e) {
      log.error(Constants.ERROR_FETCHING_PROJECTS_WITH_CLIENT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_NOT_FOUND,
              Constants.ERROR_FETCHING_PROJECTS_WITH_CLIENT));
    }
    if (projects == null || projects.isEmpty()) {
      return List.of();
    }
    return projects;
  }

  /**
   * Retrieves a list of all {@link Project} entities in the current organization.
   *
   * @return a list of all {@link Project} entities
   * @throws ResourceNotFoundException if no {@link Project} entities are found for the organization
   */
  @Override
  public List<Project> getAllProjectsInOrganization(String organizationId,int pageNumber, int pageSize, String projectId, ProjectStatus status) {
    try {
      Query query = buildProjectQuery(organizationId, projectId, status);

      int skip = (pageNumber - 1) * pageSize;
      query.skip(skip).limit(pageSize);
      query.with(Sort.by(Sort.Direction.DESC, "createdAt"));

      return mongoTemplate.find(query, Project.class);
    } catch (Exception e) {
      log.error(Constants.ERROR_FETCHING_PROJECTS_WITH_ORGANIZATION, e.getMessage(), e);
      throw new ResourceNotFoundException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.DB_ERROR,
                      ErrorCode.RESOURCE_NOT_FOUND,
                      Constants.ERROR_FETCHING_PROJECTS_WITH_ORGANIZATION));
    }
  }
  private Query buildProjectQuery(String organizationId,String projectId, ProjectStatus status) {
    Query query = new Query();
    query.addCriteria(Criteria.where("organizationId")
            .is(organizationId));

    if (projectId != null && !projectId.isEmpty()) {
      query.addCriteria(Criteria.where("projectId").is(projectId));
    }

    if (status != null) {
      query.addCriteria(Criteria.where("status").is(status.name()));
    }
    return query;
  }
  @Override
  public Long getTotalProjectsInOrganization(String organizationId,String projectId, ProjectStatus status) {
    Query query = buildProjectQuery(organizationId,projectId, status);
    return mongoTemplate.count(query, Project.class);
  }

  /**
   * Updates an existing {@link Project} based on the provided {@link ProjectRequest}.
   *
   * @param project the {@link ProjectRequest} containing updated details for the {@link Project}
   * @param projectId the unique identifier of the {@link Project} to update
   * @return the updated {@link Project}
   * @throws ResourceNotFoundException if the {@link Project} is not found with the provided
   *     projectId
   */
  @Override
  public Project updateProjectByProjectId(ProjectRequest project, String projectId) {
    Project existingProject;
    try {
      existingProject =
          projectRepository.findByProjectIdAndOrganizationId(
              projectId, UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());
    } catch (Exception e) {
      log.error(Constants.ERROR_FETCHING_PROJECT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR, ErrorCode.RESOURCE_NOT_FOUND, Constants.ERROR_FETCHING_PROJECT));
    }
    if (existingProject == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR, ErrorCode.RESOURCE_NOT_FOUND, Constants.PROJECT_NOT_FOUND));
    }
    if (project.getName() != null) {
      existingProject.setName(project.getName());
    }
    if (project.getDescription() != null) {
      existingProject.setDescription(project.getDescription());
    }
    if (project.getStatus() != null) {
      existingProject.setStatus(project.getStatus());
    }
    if (project.getStartDate() != null) {
      existingProject.setStartDate(project.getStartDate());
    }
    if (project.getEndDate() != null) {
      existingProject.setEndDate(project.getEndDate());
    }
    if(project.getProjectManagers() != null && !project.getProjectManagers().isEmpty()){
          try {
              List<String> validProjectManagers = validateAndFetchEmployees(project.getProjectManagers());
              existingProject.setProjectManagers(validProjectManagers);
          } catch (FeignClientException e){
              log.error(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS,e.getMessage(), e);
              throw new FeignClientException(Constants.ERROR_IN_VALIDATE_PROJECT_MANAGERS);
          }
      }
      if(project.getProjectResources() != null && !project.getProjectResources().isEmpty()){
          try {
              List<String> validProjectResources =  validateAndFetchEmployees(project.getProjectResources());
              existingProject.setProjectResources(validProjectResources);
          } catch (FeignClientException e){
              log.error(Constants.ERROR_IN_VALIDATE_PROJECT_RESOURCES,e.getMessage(), e);
              throw new FeignClientException(Constants.ERROR_IN_VALIDATE_PROJECT_RESOURCES);
          }
      }
    try {
      existingProject = projectRepository.save(existingProject);
    } catch (Exception e) {
      log.error(Constants.ERROR_UPDATING_PROJECT, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_CREATION_ERROR,
              Constants.ERROR_UPDATING_PROJECT));
    }
    return existingProject;
  }
  @Override
  public Project changeProjectStatus(String projectId, ProjectStatus status) {
    Project project =
            projectRepository.findByProjectIdAndOrganizationId(
                    projectId, UserContext.getLoggedInUserOrganization().get(Constants.ID).toString());

    if (project == null) {
      throw new ResourceNotFoundException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.DB_ERROR,
                      ErrorCode.RESOURCE_NOT_FOUND,
                      Constants.PROJECT_NOT_FOUND));
    }

    project.setStatus(status);

    try {
      project = projectRepository.save(project);
    } catch (Exception e) {
      log.error("Error updating project status: {}", e.getMessage());
      throw new ResourceNotFoundException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.DB_ERROR,
                      ErrorCode.RESOURCE_CREATION_ERROR,
                      Constants.SOMETHING_WENT_WRONG));
    }

    return project;
  }
  @Override
  public List<ProjectResponseDTO> getAllProjects(String organizationId,int pageNumber, int pageSize, String projectId, ProjectStatus status) {
    List<Project> projects;
    try {
      projects = getAllProjectsInOrganization(organizationId, pageNumber, pageSize, projectId, status);
    } catch (Exception e) {
      log.error("Error fetching projects", e);
      throw new InternalServerErrorException(
              String.valueOf(BuildErrorMessage.buildErrorMessage(
                      ErrorType.DB_ERROR,
                      ErrorCode.DATA_FETCH_ERROR,
                      Constants.ERROR_FETCHING_PROJECT
              ))
      );

    }
    if (projects == null || projects.isEmpty()) {
      return Collections.emptyList();
    }

    Set<String> allPMIds = projects.stream()
            .flatMap(p -> Optional.ofNullable(p.getProjectManagers())
                    .orElse(Collections.emptyList())
                    .stream())
            .collect(Collectors.toSet());

    List<EmployeeNameDTO> employeeNameDTOs;
    try {
      employeeNameDTOs = accountClient.getEmployeeNamesById(new ArrayList<>(allPMIds));
    } catch (Exception e) {
      throw new ResourceNotFoundException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.FEIGN_CLIENT_ERROR,
                      ErrorCode.RESOURCE_NOT_FOUND,
                      Constants.SOMETHING_WENT_WRONG));
    }

    Map<String, String> idToNameMap = employeeNameDTOs.stream()
            .collect(Collectors.toMap(EmployeeNameDTO::getEmployeeId, EmployeeNameDTO::getFullName));

      return projects.stream().map(project -> {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setProjectId(project.getProjectId());
        dto.setName(project.getName());
        dto.setProjectManagerIds(project.getProjectManagers());
        dto.setProjectStatus(project.getStatus());
        dto.setClientId(project.getClientId());
        Client client = clientRepository.findByClientIdAndOrganizationId(project.getClientId(), project.getOrganizationId());
        if (client != null) {
          dto.setClientName(client.getClientName());
        }

        List<String> names = Optional.ofNullable(project.getProjectManagers())
                .orElse(Collections.emptyList())
                .stream()
                .map(idToNameMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        dto.setProjectManagerNames(names);
        return dto;
      }).collect(Collectors.toList());
  }

    @Override
    public ProjectEmployeeDTO getEmployeesByProjectId(String projectId) {
        String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();

        Project project = projectRepository.findByProjectIdAndOrganizationId(projectId, organizationId);
        if (project == null) {
            log.error(Constants.PROJECT_NOT_FOUND);
            throw new RuntimeException("Project not found with id: " + projectId);
        }

        List<EmployeeNameDTO> managers = fetchEmployees(project.getProjectManagers(),  projectId);
        List<EmployeeNameDTO> resources = fetchEmployees(project.getProjectResources(), projectId);

        return new ProjectEmployeeDTO(managers, resources);
    }

    public List<EmployeeNameDTO> fetchEmployees(List<String> employeeIds, String projectId) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<EmployeeNameDTO> employeeDTOs = accountClient.getEmployeeNamesById(employeeIds);

            List<EmployeeNameDTO> activeEmployees = employeeDTOs.stream()
                    .filter(EmployeeNameDTO::isActive)
                    .collect(Collectors.toList());

            Set<String> foundIds = employeeDTOs.stream()
                    .map(EmployeeNameDTO::getEmployeeId)
                    .collect(Collectors.toSet());
            employeeIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .forEach(id -> log.warn("Employee ID {} not found or inactive for projectId {}", id, projectId));

            return activeEmployees;
        } catch (Exception e) {
            log.error("Failed to fetch employees for projectId: {} with IDs: {}", projectId, employeeIds, e);
            return Collections.emptyList();
        }
    }

}
