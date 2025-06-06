package com.beeja.api.financemanagementservice.client;

import com.beeja.api.financemanagementservice.modals.clients.finance.EmployeeNameDTO;
import com.beeja.api.financemanagementservice.modals.clients.finance.OrganizationPattern;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

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
