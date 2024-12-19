package com.beeja.api.expense.client;

import com.beeja.api.expense.response.CountryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(value = "account-service", url = "${client-urls.accountsService}")
public interface AccountClient {

  @RequestMapping(value = "/v1/users/me", method = RequestMethod.GET)
  @ResponseBody
  ResponseEntity<Object> getMe();

  @RequestMapping(value = "/v1/users", method = RequestMethod.GET)
  @ResponseBody
  ResponseEntity<Object> getAllUsers();

  @GetMapping("v1/users/email/{email}")
  ResponseEntity<?> getEmployeeByEmail(@PathVariable String email);

  @GetMapping("/v1/organizations/{organizationId}/country")
  ResponseEntity<String> getCountryByOrganizationId(@PathVariable String organizationId);

  @GetMapping("api/country/get-expense-types-categories/{countryName}")
  ResponseEntity<CountryResponse> getExpenseTypesByCountryName(@PathVariable String countryName);

  @GetMapping("/api/country/mode-of-payment/{countryName}")
  ResponseEntity<CountryResponse> getModeOfPaymentByCountry(@PathVariable String countryName);
}
