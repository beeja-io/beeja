package com.beeja.api.financemanagementservice.controllers;

import com.beeja.api.financemanagementservice.Utils.BuildErrorMessage;
import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.annotations.HasPermission;
import com.beeja.api.financemanagementservice.enums.ErrorCode;
import com.beeja.api.financemanagementservice.enums.ErrorType;
import com.beeja.api.financemanagementservice.enums.LoanStatus;
import com.beeja.api.financemanagementservice.exceptions.BadRequestException;
import com.beeja.api.financemanagementservice.modals.Loan;
import com.beeja.api.financemanagementservice.requests.SubmitLoanRequest;
import com.beeja.api.financemanagementservice.response.LoanResponse;
import com.beeja.api.financemanagementservice.service.LoanService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/loans")
public class LoanController {

  @Autowired LoanService loanService;

  @GetMapping
  @HasPermission(Constants.GET_ALL_LOANS)
  public LoanResponse getAllLoansBasedOnCount(
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection,
      @RequestParam(required = false) LoanStatus status) {
    return loanService.getLoansWithCount(pageNumber, pageSize, sortBy, sortDirection, status);
  }

  @GetMapping("/{employeeID}")
  @HasPermission(Constants.READ_LOAN)
  public ResponseEntity<List<Loan>> getLoansByEmployeeId(@PathVariable String employeeID)
      throws Exception {
    return ResponseEntity.ok(loanService.getAllLoansByEmployeeId(employeeID));
  }

  @PutMapping("/{loanId}/status")
  @HasPermission(Constants.STATUS_CHANGE_LOAN)
  public void changeLoanStatus(
      @PathVariable String loanId, @RequestParam String status, @RequestParam String message) {
    loanService.changeLoanStatus(loanId, status, message);
  }

  @PostMapping
  @HasPermission(Constants.CREATE_LOAN)
  public ResponseEntity<Loan> submitLoanRequest(
      @RequestBody @Valid SubmitLoanRequest loanRequest, BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      List<String> errorMessages =
          bindingResult.getAllErrors().stream()
              .map(ObjectError::getDefaultMessage)
              .collect(Collectors.toList());
      throw new BadRequestException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.VALIDATION_ERROR,
              ErrorCode.FIELD_VALIDATION_MISSING,
              errorMessages.toString()));
    }
    Loan newLoan = loanService.submitLoanRequest(loanRequest);
    return new ResponseEntity<>(newLoan, HttpStatus.CREATED);
  }
}
