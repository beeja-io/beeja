package com.beeja.api.accounts.repository;

import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.model.dto.EmployeeNameDTO;
import java.util.List;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
  List<User> findByEmployeeIdInAndOrganizations_Id(List<String> employeeIds, String organizationId);

  @Aggregation(
      pipeline = {
        "{ '$match': { 'employeeId': { '$in': ?0 }, 'organizations._id': ?1 } }",
        "{ '$project': { 'employeeId': 1, 'fullName': { '$concat': ['$firstName', ' ', '$lastName'] } } }"
      })
  List<EmployeeNameDTO> findEmployeeNamesByEmployeeIdInAndOrganizations_Id(
      List<String> employeeIds, String organizationId);

  List<User> findByEmployeeIdIn(List<String> employeeIds);

  User findByEmail(String email);

  User findByEmailAndOrganizations(String email, Organization organizations);

  User findByEmployeeIdAndOrganizations(String employeeId, Organization organizations);

  List<User> findByOrganizationsAndIsActive(Organization organizations, boolean isActive);

  List<User> findByOrganizationsId(String id);

  List<User> findByRoles(Role role);

  Long countByOrganizations(Organization organizations);

  Long countByOrganizationsAndIsActive(Organization organizations, boolean isActive);

  @Query(value = "{ 'organizations._id': ?0 }", count = true)
  long countByOrganizationId(String organizationId);
}
