package com.beeja.api.financemanagementservice.repository;

import com.beeja.api.financemanagementservice.modals.Loan;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {

  List<Loan> findAllByEmployeeIdAndOrganizationId(String employeeId, String organizationId);

  List<Loan> findAllByOrganizationId(String organizationId);

  Loan findByIdAndOrganizationId(String loanId, String organizationId);

  long countByOrganizationId(String organizationId);
}
