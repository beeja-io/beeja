package com.beeja.api.employeemanagement.utils.methods;

import com.beeja.api.employeemanagement.response.GetLimitedEmployee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServiceMethods {

  public static Optional<Map<String, Object>> combineEmployeeAndAccount(
      GetLimitedEmployee employee, List<Map<String, Object>> accountDataList) {

    return accountDataList.stream()
        .filter(accountData -> employee.getEmployeeId().equals(accountData.get("employeeId")))
        .findFirst()
        .map(
            accountData -> {
              Map<String, Object> combinedData = new HashMap<>();
              combinedData.put("employee", employee);
              combinedData.put("account", accountData);
              return combinedData;
            });
  }
}
