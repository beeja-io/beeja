package com.beeja.api.projectmanagement.client;

import com.beeja.api.projectmanagement.model.dto.EmployeeNameDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "account-service", url = "${client-urls.accountsService}")
public interface AccountClient {

  @GetMapping("/")
  String hello();

  @RequestMapping(value = "/v1/users/me", method = RequestMethod.GET)
  @ResponseBody
  ResponseEntity<Object> getMe();

  @RequestMapping(value = "/v1/users", method = RequestMethod.GET)
  @ResponseBody
  ResponseEntity<Object> getAllUsers();

  @GetMapping("v1/users/email/{email}")
  ResponseEntity<?> getEmployeeByEmail(@PathVariable String email);

  @GetMapping("/v1/organizations/{organizationId}")
  ResponseEntity<Object> getOrganizationById(@PathVariable("organizationId") String organizationId);

  @PostMapping("/v1/users/validate-employees")
  List<String> checkEmployeesPresentOrNot(@RequestBody List<String> employeeIds);

  @PostMapping("/v1/users/details-by-ids")
  List<EmployeeNameDTO> getEmployeeNamesById(@RequestBody List<String> ids);

}
