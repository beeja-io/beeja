package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Project;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for performing CRUD operations on {@link Project} documents in MongoDB. */
@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

  /**
   * Retrieves a project by project ID and organization ID.
   *
   * @param projectId the unique ID of the project
   * @param organizationId the ID of the organization the project belongs to
   * @return the matching {@link Project}, or {@code null} if not found
   */
  Project findByProjectIdAndOrganizationId(String projectId, String organizationId);

  /**
   * Retrieves all projects associated with a given client and organization.
   *
   * @param clientId the ID of the client
   * @param organizationId the ID of the organization
   * @return a list of {@link Project} objects linked to the specified client and organization
   */
  List<Project> findByClientIdAndOrganizationId(String clientId, String organizationId);

  /**
   * Retrieves all projects associated with a given organization.
   *
   * @param organizationId the ID of the organization
   * @return a list of {@link Project} objects linked to the specified organization
   */
  List<Project> findByOrganizationId(String organizationId);

  /**
   * Retrieves a project by project ID, client ID, and organization ID.
   *
   * @param projectId the unique ID of the project
   * @param clientId the ID of the client
   * @param organizationId the ID of the organization the project belongs to
   * @return the matching {@link Project}, or {@code null} if not found
   */
  Project findByProjectIdAndClientIdAndOrganizationId(
      String projectId, String clientId, String organizationId);

  Optional<Project> findByProjectId(String projectId, String organizationId);

  long countByOrganizationId(String organizationId);

  boolean existsByProjectIdAndOrganizationId(String projectId, String organizationId);

  List<Project> findByOrganizationIdAndProjectResourcesContaining(String employeeId, String organizationId);

  List<Project> findByOrganizationIdAndProjectManagersContaining(String employeeId, String organizationId);

}
