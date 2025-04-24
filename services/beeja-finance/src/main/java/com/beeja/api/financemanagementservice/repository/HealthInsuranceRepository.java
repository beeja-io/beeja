package com.beeja.api.financemanagementservice.repository;

import com.beeja.api.financemanagementservice.modals.HealthInsurance;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthInsuranceRepository extends MongoRepository<HealthInsurance, String> {
  Optional<HealthInsurance> findByEmployeeId(String employeeId);

  Optional<HealthInsurance> findByEmployeeIdAndOrganizationId(
      String employeeId, String organizationId);

  HealthInsurance deleteByEmployeeIdAndOrganizationId(String employeeId, String organizationId);
}
