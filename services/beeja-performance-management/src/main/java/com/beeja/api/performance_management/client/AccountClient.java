package com.beeja.api.performance_management.client;

import com.beeja.api.performance_management.model.dto.BasicUserInfoDTO;
import com.beeja.api.performance_management.model.dto.EmployeeIdNameDTO;
import com.beeja.api.performance_management.model.dto.EmployeeName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "account-service", url = "${client-urls.accountsService}")
public interface AccountClient {

  @GetMapping("/v1/users/name/{employeeId}")
  EmployeeName getEmployeeName(@PathVariable String employeeId);

  @GetMapping("/v1/users/organization")
  List<BasicUserInfoDTO> getUsersByLoggedInUserOrganization();

  @GetMapping("v1/users/email/{email}")
  ResponseEntity<?> getEmployeeByEmail(@PathVariable String email);

    @PostMapping("/v1/users/details-by-ids")
    List<EmployeeIdNameDTO> getEmployeeNamesById(@RequestBody List<String> ids);
}
