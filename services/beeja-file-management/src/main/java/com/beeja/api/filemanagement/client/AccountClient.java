package com.beeja.api.filemanagement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "account-service", url = "${client-urls.accountsService}")
public interface AccountClient {

  @RequestMapping(value = "/v1/users/me", method = RequestMethod.GET)
  @ResponseBody
  ResponseEntity<?> getMe();

  @GetMapping("/v1/users/{employeeId}")
  ResponseEntity<?> getUserByEmployeeId(@PathVariable String employeeId);

  @GetMapping("/v1/users/email/{email}")
  ResponseEntity<?> getUserByEmail(@PathVariable String email);
}
