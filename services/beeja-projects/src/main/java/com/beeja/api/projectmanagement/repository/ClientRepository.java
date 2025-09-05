package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Client;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for performing CRUD operations on {@link Client} documents in MongoDB. */
@Repository
public interface    ClientRepository extends MongoRepository<Client, String> {

  /**
   * Retrieves a client by email and organization ID.
   *
   * @param email the email of the client
   * @param organizationId the ID of the organization the client belongs to
   * @return the matching {@link Client}, or {@code null} if not found
   */
  Client findByEmailAndOrganizationId(String email, String organizationId);

  /**
   * Retrieves a client by client ID and organization ID.
   *
   * @param clientId the unique client ID
   * @param organizationId the ID of the organization the client belongs to
   * @return the matching {@link Client}, or {@code null} if not found
   */
  Client findByClientIdAndOrganizationId(String clientId, String organizationId);

  /**
   * Retrieves a client by database ID and organization ID.
   *
   * @param id the MongoDB document ID of the client
   * @param organizationId the ID of the organization the client belongs to
   * @return the matching {@link Client}, or {@code null} if not found
   */
  Client findByIdAndOrganizationId(String id, String organizationId);

  /**
   * Counts the number of clients belonging to a given organization.
   *
   * @param organizationId the ID of the organization
   * @return the total number of clients in the organization
   */
  long countByOrganizationId(String organizationId);

  /**
   * Retrieves all clients in a given organization, sorted by creation date in descending order.
   *
   * @param organizationId the ID of the organization
   * @return a list of {@link Client} objects ordered by {@code createdAt} descending
   */
  List<Client> findAllByOrganizationIdOrderByCreatedAtDesc(String organizationId);

    /**
     * Retrieves a paginated list of clients for a given organization, ordered by creation date descending.
     *
     * @param organizationId the ID of the organization whose clients are to be retrieved
     * @param pageable the pagination information (page number, page size, sorting)
     * @return a {@link Page} of {@link Client} objects for the specified organization
     */
  Page<Client> findAllByOrganizationIdOrderByCreatedAtDesc(String organizationId, Pageable pageable);
}
