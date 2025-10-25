package com.beeja.api.employeemanagement.repository;

import com.beeja.api.employeemanagement.model.Employee;
import com.beeja.api.employeemanagement.model.clients.accounts.EmployeeDepartmentDTO;
import com.beeja.api.employeemanagement.response.EmployeeDefaultValues;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
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

  @Aggregation(
      pipeline = {
        "{ $match: { 'organizationId': ?0 } }",
        "{ $project: { _id: 0, employmentType: '$jobDetails.employementType', designation: '$jobDetails.designation', "
            + "department: '$jobDetails.department' } }",
        "{ $group: { _id: null, uniqueValues: { $addToSet: { employmentType: '$employmentType', "
            + "designation: '$designation', department: '$department' } } } }",
        "{ $unwind: '$uniqueValues' }",
        "{ $replaceRoot: { newRoot: '$uniqueValues' } }"
      })
  List<EmployeeDefaultValues> findDistinctTypeByOrganizationId(String organizationId);


 List<Employee> findAllByOrganizationId(String organizationId);

 List<Employee> findAllByOrganizationIdAndJobDetailsDesignationIn(String organizationId, List<String> designations);

 Optional<Employee> findByEmployeeId(String employeeId);

 @Aggregation(pipeline = {
          "{ $match: { 'employeeId': { $in: ?0 }, 'organizationId': ?1 } }",
          "{ $project: { _id: 0, employeeId: 1, department: '$jobDetails.department' } }"
 })
 List<EmployeeDepartmentDTO> findDepartmentsByEmployeeIds(List<String> employeeIds, String organizationId);
}
