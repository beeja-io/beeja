package com.beeja.api.financemanagementservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "account-service", url = "${client-urls.accountsService}")
public interface AccountClient {

  @GetMapping("/v1/users/{employeeId}")
  ResponseEntity<?> getUserByEmployeeId(
      @PathVariable String employeeId, @RequestHeader("Authorization") String authorizationHeader);

  @GetMapping("v1/users/email/{email}")
  ResponseEntity<?> getEmployeeByEmail(@PathVariable String email);
}
