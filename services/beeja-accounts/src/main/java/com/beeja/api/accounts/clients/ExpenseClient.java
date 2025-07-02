package com.beeja.api.accounts.clients;

import com.beeja.api.accounts.response.ExpenseValuesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "expense-service", url = "${client-urls.expenseService}")
public interface ExpenseClient {
  @GetMapping("/v1/expense-values")
  ExpenseValuesDTO getExpenseValues(@RequestHeader("Authorization") String authorizationHeader);
}
