package com.beeja.api.employeemanagement.repository;

import com.beeja.api.employeemanagement.model.Employee;
import com.beeja.api.employeemanagement.response.EmployeeDefaultValues;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

  Employee findByEmployeeIdAndOrganizationId(String employeeId, String organizationId);

  @Query(
      value = "{ 'employeeId': ?0, 'organizationId': ?1 }",
      fields =
          "{ 'employeeId': 1, 'organizationId': 1, 'address': 1, 'personalInformation': 1, 'contact': 1 }")
  Employee getLimitedDataFindByEmployeeId(String employeeId, String organizationId);

  @Aggregation(pipeline = {
          "{ $match: { 'organizationId': ?0 } }",
          "{ $project: { _id: 0, employmentType: '$jobDetails.employementType', designation: '$jobDetails.designation', department: '$jobDetails.department' } }",
          "{ $group: { _id: null, uniqueValues: { $addToSet: { employmentType: '$employmentType', designation: '$designation', department: '$department' } } } }",
          "{ $unwind: '$uniqueValues' }",
          "{ $replaceRoot: { newRoot: '$uniqueValues' } }"
  })
  List<EmployeeDefaultValues> findDistinctTypeByOrganizationId(String organizationId);



}
