package com.beeja.api.financemanagementservice.service;

import com.beeja.api.financemanagementservice.modals.HealthInsurance;
import com.beeja.api.financemanagementservice.requests.HealthInsuranceRequest;

public interface HealthInsuranceService {

  HealthInsurance saveHealthInsurance(HealthInsuranceRequest healthInsuranceRequest)
      throws Exception;

  HealthInsurance updateHealthInsurance(
      HealthInsuranceRequest healthInsuranceRequest, String healthInsuranceId) throws Exception;

  HealthInsurance deleteByEmployeeIdAndOrganizationId(String healthInsuranceId) throws Exception;

  HealthInsurance findHealthInsuranceByEmployeeId(String employeeId) throws Exception;
}
