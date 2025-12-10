package com.beeja.api.employeemanagement.client;

import com.beeja.api.employeemanagement.model.DTO.EmployeeName;
import com.beeja.api.employeemanagement.model.clients.accounts.EmployeeNameDTO;
import com.beeja.api.employeemanagement.requests.EmployeeOrgRequest;
import com.beeja.api.employeemanagement.requests.EmployeeUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@FeignClient(value = "account-service", url = "${client-urls.accountsService}")
public interface AccountClient {

  @PostMapping("/v1/users/names")
  List<EmployeeNameDTO> getEmployeeNamesByIds(@RequestBody List<String> employeeIds);

  @RequestMapping(value = "/v1/users", method = RequestMethod.GET)
  @ResponseBody
  ResponseEntity<Object> getAllUsers();

  @GetMapping("v1/users/email/{email}")
  ResponseEntity<?> getEmployeeByEmail(@PathVariable String email);

  @GetMapping("v1/users/{employeeId}")
  ResponseEntity<Object> getUserByEmployeeId(@PathVariable String employeeId);

  @PutMapping("v1/users/{employeeId}")
  ResponseEntity<?> updateUser(
      @PathVariable String employeeId, @RequestBody EmployeeUpdateRequest updatedUser);

  @GetMapping("v1/users/exists/{email}")
  Boolean isUserPresentWithMail(@PathVariable String email);

  @RequestMapping(value = "/v1/users/emp-ids", method = RequestMethod.POST)
  @ResponseBody
  ResponseEntity<Object> getUsersByEmployeeIds(@RequestBody EmployeeOrgRequest employeeOrgRequest);

  @GetMapping("/v1/users/name/{employeeId}")
  EmployeeName getEmployeeName(@PathVariable String employeeId);
}
