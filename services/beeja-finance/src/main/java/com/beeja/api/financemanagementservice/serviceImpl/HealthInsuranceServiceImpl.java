package com.beeja.api.financemanagementservice.serviceImpl;

import com.beeja.api.financemanagementservice.Utils.BuildErrorMessage;
import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.enums.ErrorCode;
import com.beeja.api.financemanagementservice.enums.ErrorType;
import com.beeja.api.financemanagementservice.enums.InstalmentType;
import com.beeja.api.financemanagementservice.exceptions.HealthInsuranceCreationException;
import com.beeja.api.financemanagementservice.exceptions.HealthInsuranceNotFoundException;
import com.beeja.api.financemanagementservice.modals.HealthInsurance;
import com.beeja.api.financemanagementservice.repository.HealthInsuranceRepository;
import com.beeja.api.financemanagementservice.requests.HealthInsuranceRequest;
import com.beeja.api.financemanagementservice.service.HealthInsuranceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the HealthInsuranceService interface providing CRUD operations for managing
 * health insurance details.
 */
@Service
@Slf4j
public class HealthInsuranceServiceImpl implements HealthInsuranceService {

  @Autowired private HealthInsuranceRepository healthInsuranceRepository;

  /**
   * Saves health insurance details for a new employee.
   *
   * @param healthInsuranceRequest The request object containing health insurance details.
   * @return The saved HealthInsurance entity.
   * @throws Exception If an error occurs during health insurance creation.
   */
  @Override
  public HealthInsurance saveHealthInsurance(HealthInsuranceRequest healthInsuranceRequest)
      throws Exception {
    String loggedInOrganizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
    Optional<HealthInsurance> existingHealthInsurance =
        healthInsuranceRepository.findByEmployeeId(healthInsuranceRequest.getEmployeeId());
    if (existingHealthInsurance.isPresent()) {
      throw new HealthInsuranceCreationException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.CONFLICT_ERROR,
              ErrorCode.RESOURCE_EXISTS_ERROR,
              Constants.ERROR_HEALTH_INSURANCE_ALREADY_FOUND));
    }
    HealthInsurance healthInsurance = new HealthInsurance();
    healthInsurance.setEmployeeId(healthInsuranceRequest.getEmployeeId());
    healthInsurance.setOrganizationId(loggedInOrganizationId);
    healthInsurance.setGrossPremium(healthInsuranceRequest.getGrossPremium());
    healthInsurance.setInstalmentType(healthInsuranceRequest.getInstalmentType());
    healthInsurance.setInstalmentAmount(healthInsuranceRequest.getInstalmentAmount());
    if (healthInsuranceRequest.getInstalmentType() == InstalmentType.MONTHLY) {
      healthInsurance.setInstalmentFrequency(12);
    } else if (healthInsuranceRequest.getInstalmentType() == InstalmentType.QUARTERLY) {
      healthInsurance.setInstalmentFrequency(4);
    }
    healthInsurance.setCreatedBy(UserContext.getLoggedInUserEmail());
    try {
      healthInsuranceRepository.save(healthInsurance);
    } catch (Exception e) {
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_CREATING_ERROR,
              Constants.ERROR_CREATING_HEALTH_INSURANCE + Constants.APPEND_EXCEPTION_MESSAGE));
    }
    return healthInsurance;
  }

  /**
   * Updates existing health insurance details for an employee.
   *
   * @param healthInsuranceRequest The request object containing updated health insurance details.
   * @param employeeID The ID of the employee whose health insurance details are to be updated.
   * @return The updated HealthInsurance entity.
   * @throws Exception If an error occurs during health insurance update or if the health insurance
   *     details are not found.
   */
  @Override
  public HealthInsurance updateHealthInsurance(
      HealthInsuranceRequest healthInsuranceRequest, String employeeID) throws Exception {
    Optional<HealthInsurance> healthInsuranceOptional =
        healthInsuranceRepository.findByEmployeeIdAndOrganizationId(
            employeeID, UserContext.getLoggedInUserOrganization().get("id").toString());

    if (healthInsuranceOptional.isEmpty()) {
      throw new HealthInsuranceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.RESOURCE_NOT_FOUND,
              Constants.HEALTH_INSURANCE_NOT_FOUND + " for employeeId " + employeeID));
    }
    HealthInsurance healthInsurance = healthInsuranceOptional.get();
    healthInsurance.setGrossPremium(healthInsuranceRequest.getGrossPremium());
    healthInsurance.setInstalmentType(healthInsuranceRequest.getInstalmentType());
    healthInsurance.setInstalmentAmount(healthInsuranceRequest.getInstalmentAmount());
    if (healthInsuranceRequest.getInstalmentType() == InstalmentType.MONTHLY) {
      healthInsurance.setInstalmentFrequency(12);
    } else if (healthInsuranceRequest.getInstalmentType() == InstalmentType.QUARTERLY) {
      healthInsurance.setInstalmentFrequency(4);
    }
    healthInsurance.setModifiedBy(UserContext.getLoggedInUserEmail());
    try {
      healthInsuranceRepository.save(healthInsurance);
      return healthInsurance;
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.ERROR_UPDATING_HEALTH_INSURANCE
                  + Constants.APPEND_EXCEPTION_MESSAGE
                  + e.getMessage()));
    }
  }

  /**
   * Deletes health insurance details for an employee.
   *
   * @param employeeId The ID of the employee whose health insurance details are to be deleted.
   * @return The deleted HealthInsurance entity.
   * @throws Exception If an error occurs during health insurance deletion or if the health
   *     insurance details are not found.
   */
  @Override
  public HealthInsurance deleteByEmployeeIdAndOrganizationId(String employeeId) throws Exception {
    String loggedInOrganizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
    try {
      HealthInsurance deletedHEalthInsurance =
          healthInsuranceRepository.deleteByEmployeeIdAndOrganizationId(
              employeeId, loggedInOrganizationId);
      if (deletedHEalthInsurance == null) {
        throw new HealthInsuranceNotFoundException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                ErrorCode.RESOURCE_NOT_FOUND,
                Constants.HEALTH_INSURANCE_NOT_FOUND + " for employeeId " + employeeId));
      }
      return deletedHEalthInsurance;
    } catch (HealthInsuranceNotFoundException e) {
      throw new HealthInsuranceNotFoundException(e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.RESOURCE_DELETING_ERROR,
              Constants.ERROR_DELETING_HEALTH_INSURANCE + Constants.APPEND_EXCEPTION_MESSAGE));
    }
  }

  /**
   * Finds health insurance details for an employee by employee ID.
   *
   * @param employeeId The ID of the employee whose health insurance details are to be retrieved.
   * @return The HealthInsurance entity corresponding to the given employee ID.
   * @throws Exception If an error occurs during health insurance retrieval or if the health
   *     insurance details are not found.
   */
  @Override
  public HealthInsurance findHealthInsuranceByEmployeeId(String employeeId) throws Exception {
    try {
      Optional<HealthInsurance> healthInsurance =
          healthInsuranceRepository.findByEmployeeIdAndOrganizationId(
              employeeId, UserContext.getLoggedInUserOrganization().get("id").toString());
      return healthInsurance.orElseThrow(
          () ->
              new HealthInsuranceNotFoundException(
                  BuildErrorMessage.buildErrorMessage(
                      ErrorType.RESOURCE_NOT_FOUND_ERROR,
                      ErrorCode.RESOURCE_NOT_FOUND,
                      Constants.HEALTH_INSURANCE_NOT_FOUND + " for employeeId " + employeeId)));
    } catch (HealthInsuranceNotFoundException e) {
      throw new HealthInsuranceNotFoundException(e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_FETCHING_HEALTH_INSURANCE
                  + Constants.APPEND_EXCEPTION_MESSAGE
                  + e.getMessage()));
    }
  }
}
