package com.beeja.api.employeemanagement.repository;

import com.beeja.api.employeemanagement.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

  Employee findByEmployeeIdAndOrganizationId(String employeeId, String organizationId);

  @Query(
      value = "{ 'employeeId': ?0, 'organizationId': ?1 }",
      fields =
          "{ 'employeeId': 1, 'organizationId': 1, 'address': 1, 'personalInformation': 1, 'contact': 1 }")
  Employee getLimitedDataFindByEmployeeId(String employeeId, String organizationId);
}
