package com.beeja.api.financemanagementservice.repository;

import com.beeja.api.financemanagementservice.enums.LoanStatus;
import com.beeja.api.financemanagementservice.modals.Loan;
import com.beeja.api.financemanagementservice.response.LoanDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {

  List<Loan> findAllByEmployeeIdAndOrganizationId(String employeeId, String organizationId);

  List<Loan> findAllByOrganizationId(String organizationId);

  Loan findByIdAndOrganizationId(String loanId, String organizationId);

  long countByOrganizationId(String organizationId);

  long countByOrganizationIdAndStatus(String organizationId, LoanStatus status);

  List<LoanDTO> findAllByOrganizationId(String organizationId, Pageable pageable);

  List<LoanDTO> findAllByOrganizationIdAndStatus(String organizationId, LoanStatus status, Pageable pageable);

}
