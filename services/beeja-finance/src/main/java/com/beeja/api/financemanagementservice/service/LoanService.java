package com.beeja.api.financemanagementservice.service;

import com.beeja.api.financemanagementservice.modals.Loan;
import com.beeja.api.financemanagementservice.requests.BulkPayslipRequest;
import com.beeja.api.financemanagementservice.requests.SubmitLoanRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoanService {

  void uploadBulkPaySlips(BulkPayslipRequest bulkPayslipRequest, String authorizationHeader)
      throws Exception;

  void changeLoanStatus(String loanId, String status, String message);

  Loan submitLoanRequest(SubmitLoanRequest loanRequest) throws Exception;

  List<Loan> getAllLoans() throws Exception;

  List<Loan> getAllLoansByEmployeeId(String employeeId) throws Exception;
}
