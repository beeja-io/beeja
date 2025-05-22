package com.beeja.api.financemanagementservice.client;

import com.beeja.api.financemanagementservice.modals.clients.finance.EmployeeNameDTO;
import com.beeja.api.financemanagementservice.modals.clients.finance.OrganizationPattern;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "account-service", url = "${client-urls.accountsService}")
public interface AccountClient {

  @PostMapping("/v1/users/names")
  List<EmployeeNameDTO> getEmployeeNamesByIds(@RequestBody List<String> employeeIds);

  @GetMapping("/v1/users/{employeeId}")
  ResponseEntity<?> getUserByEmployeeId(
      @PathVariable String employeeId, @RequestHeader("Authorization") String authorizationHeader);

  @GetMapping("v1/users/email/{email}")
  ResponseEntity<?> getEmployeeByEmail(@PathVariable String email);

  @GetMapping("/v1/organization/patterns/active")
  ResponseEntity<OrganizationPattern> getActivePatternByType(@RequestParam String patternType);
}
