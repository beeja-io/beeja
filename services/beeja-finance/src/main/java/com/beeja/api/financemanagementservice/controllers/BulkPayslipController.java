package com.beeja.api.financemanagementservice.controllers;

import static com.beeja.api.financemanagementservice.Utils.Constants.CREATE_BULK_PAYSLIPS;

import com.beeja.api.financemanagementservice.annotations.HasPermission;
import com.beeja.api.financemanagementservice.requests.BulkPayslipRequest;
import com.beeja.api.financemanagementservice.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payslips")
public class BulkPayslipController {

  @Autowired LoanService loanService;

  @PostMapping
  @HasPermission(CREATE_BULK_PAYSLIPS)
  public void uploadBulkPaySlips(
      @RequestHeader("Authorization") String authorizationHeader,
      BulkPayslipRequest bulkPayslipRequest)
      throws Exception {
    loanService.uploadBulkPaySlips(bulkPayslipRequest, authorizationHeader);
  }
}
