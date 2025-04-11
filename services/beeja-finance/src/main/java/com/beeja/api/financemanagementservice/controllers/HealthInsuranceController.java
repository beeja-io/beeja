package com.beeja.api.financemanagementservice.controllers;

import static com.beeja.api.financemanagementservice.Utils.Constants.CREATE_HEALTH_INSURANCE;
import static com.beeja.api.financemanagementservice.Utils.Constants.DELETE_HEALTH_INSURANCE;
import static com.beeja.api.financemanagementservice.Utils.Constants.READ_HEALTH_INSURANCE;
import static com.beeja.api.financemanagementservice.Utils.Constants.UPDATE_HEALTH_INSURANCE;

import com.beeja.api.financemanagementservice.Utils.BuildErrorMessage;
import com.beeja.api.financemanagementservice.annotations.HasPermission;
import com.beeja.api.financemanagementservice.enums.ErrorCode;
import com.beeja.api.financemanagementservice.enums.ErrorType;
import com.beeja.api.financemanagementservice.exceptions.BadRequestException;
import com.beeja.api.financemanagementservice.modals.HealthInsurance;
import com.beeja.api.financemanagementservice.requests.HealthInsuranceRequest;
import com.beeja.api.financemanagementservice.service.HealthInsuranceService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/health-insurances")
public class HealthInsuranceController {

  @Autowired HealthInsuranceService healthInsuranceService;

  @PostMapping
  @HasPermission(CREATE_HEALTH_INSURANCE)
  public ResponseEntity<HealthInsurance> submitHealthInsurance(
      @RequestBody @Valid HealthInsuranceRequest healthInsuranceRequest,
      BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      List<String> errorMessages =
          bindingResult.getAllErrors().stream()
              .map(ObjectError::getDefaultMessage)
              .collect(Collectors.toList());
      throw new BadRequestException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.VALIDATION_ERROR,
              ErrorCode.FIELD_VALIDATION_MISSING,
              errorMessages.toString()));
    }
    HealthInsurance healthInsurance =
        healthInsuranceService.saveHealthInsurance(healthInsuranceRequest);
    return new ResponseEntity<>(healthInsurance, HttpStatus.CREATED);
  }

  @PutMapping("/employee/{employeeID}")
  @HasPermission(UPDATE_HEALTH_INSURANCE)
  public ResponseEntity<HealthInsurance> updateHealthInsurance(
      @RequestBody @Valid HealthInsuranceRequest healthInsuranceRequest,
      @PathVariable String employeeID,
      BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      List<String> errorMessages =
          bindingResult.getAllErrors().stream()
              .map(ObjectError::getDefaultMessage)
              .collect(Collectors.toList());
      throw new BadRequestException(errorMessages.toString());
    }
    HealthInsurance healthInsurance =
        healthInsuranceService.updateHealthInsurance(healthInsuranceRequest, employeeID);
    return new ResponseEntity<>(healthInsurance, HttpStatus.OK);
  }

  @DeleteMapping("/employee/{employeeID}")
  @HasPermission(DELETE_HEALTH_INSURANCE)
  public ResponseEntity<HealthInsurance> deleteHealthInsurance(@PathVariable String employeeID)
      throws Exception {
    HealthInsurance healthInsurance =
        healthInsuranceService.deleteByEmployeeIdAndOrganizationId(employeeID);
    return new ResponseEntity<>(healthInsurance, HttpStatus.OK);
  }

  @GetMapping("/{employeeID}")
  @HasPermission(READ_HEALTH_INSURANCE)
  public ResponseEntity<HealthInsurance> getHealthInsuranceByEmployeeId(
      @PathVariable String employeeID) throws Exception {
    return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceByEmployeeId(employeeID));
  }
}
