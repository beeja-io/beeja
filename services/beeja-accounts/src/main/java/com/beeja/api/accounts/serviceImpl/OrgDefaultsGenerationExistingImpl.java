package com.beeja.api.accounts.serviceImpl;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.clients.ExpenseClient;
import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.employeeSettings.OrgValues;
import com.beeja.api.accounts.repository.OrgDefaultsRepository;
import com.beeja.api.accounts.response.EmployeeValuesDTO;
import com.beeja.api.accounts.response.ExpenseValuesDTO;
import com.beeja.api.accounts.utils.UserContext;
import java.util.HashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrgDefaultsGenerationExistingImpl {

  @Autowired ExpenseClient expenseClient;

  @Autowired EmployeeFeignClient employeeFeignClient;

  @Autowired OrgDefaultsRepository orgDefaultsRepository;

  public void generateExistingValuesOfExpenseType() {
    try {
      ExpenseValuesDTO expenseValuesDTOResponseEntity =
          expenseClient.getExpenseValues("Bearer " + UserContext.getAccessToken());
      OrgDefaults existingExpenseTypes =
          orgDefaultsRepository.findByOrganizationIdAndKey(
              UserContext.getLoggedInUserOrganization().getId(), "expenseTypes");
      if (existingExpenseTypes == null) {
        existingExpenseTypes = new OrgDefaults();
        existingExpenseTypes.setKey("expenseTypes");
      }
      if (existingExpenseTypes.getValues() == null) {
        existingExpenseTypes.setValues(new HashSet<>());
      }
      if (expenseValuesDTOResponseEntity != null) {
        for (String expenseType : expenseValuesDTOResponseEntity.getExpenseTypes()) {
          OrgValues expTypeValues = new OrgValues();
          expTypeValues.setValue(expenseType);
          expTypeValues.setDescription("Auto Generated");
          existingExpenseTypes.getValues().add(expTypeValues);
        }
      }
      existingExpenseTypes.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
      existingExpenseTypes.setValues(new HashSet<>(existingExpenseTypes.getValues()));
      orgDefaultsRepository.save(existingExpenseTypes);

      log.info("Existing Expense Types are generated successfully");

    } catch (Exception e) {
      log.error("Error while generating existing expense types : {}", e.getMessage());
    }
  }

  public void generateExistingValuesOfExpenseCategories() {
    try {
      ExpenseValuesDTO expenseValuesDTOResponseEntity =
          expenseClient.getExpenseValues("Bearer " + UserContext.getAccessToken());
      OrgDefaults existingExpenseCategories =
          orgDefaultsRepository.findByOrganizationIdAndKey(
              UserContext.getLoggedInUserOrganization().getId(), "expenseCategories");
      if (existingExpenseCategories == null) {
        existingExpenseCategories = new OrgDefaults();
        existingExpenseCategories.setKey("expenseCategories");
      }
      if (existingExpenseCategories.getValues() == null) {
        existingExpenseCategories.setValues(new HashSet<>());
      }
      if (expenseValuesDTOResponseEntity != null) {
        for (String expenseCategory : expenseValuesDTOResponseEntity.getExpenseCategories()) {
          OrgValues expCategoryValues = new OrgValues();
          expCategoryValues.setValue(expenseCategory);
          expCategoryValues.setDescription("Auto Generated");
          existingExpenseCategories.getValues().add(expCategoryValues);
        }
      }
      existingExpenseCategories.setOrganizationId(
          UserContext.getLoggedInUserOrganization().getId());
      existingExpenseCategories.setValues(new HashSet<>(existingExpenseCategories.getValues()));
      orgDefaultsRepository.save(existingExpenseCategories);

      log.info("Existing Expense Categories are generated successfully");

    } catch (Exception e) {
      log.error("Error while generating existing expense categories : {}", e.getMessage());
    }
  }

  public void generateExistingPaymentModes() {
    try {
      ExpenseValuesDTO expenseValuesDTOResponseEntity =
          expenseClient.getExpenseValues("Bearer " + UserContext.getAccessToken());
      OrgDefaults existingPaymentModes =
          orgDefaultsRepository.findByOrganizationIdAndKey(
              UserContext.getLoggedInUserOrganization().getId(), "paymentModes");
      if (existingPaymentModes == null) {
        existingPaymentModes = new OrgDefaults();
        existingPaymentModes.setKey("paymentModes");
      }
      if (existingPaymentModes.getValues() == null) {
        existingPaymentModes.setValues(new HashSet<>());
      }
      if (expenseValuesDTOResponseEntity != null) {
        for (String paymentMode : expenseValuesDTOResponseEntity.getExpenseModesOfPayment()) {
          OrgValues paymentModeValues = new OrgValues();
          paymentModeValues.setValue(paymentMode);
          paymentModeValues.setDescription("Auto Generated");
          existingPaymentModes.getValues().add(paymentModeValues);
        }
      }
      existingPaymentModes.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
      existingPaymentModes.setValues(new HashSet<>(existingPaymentModes.getValues()));
      orgDefaultsRepository.save(existingPaymentModes);

      log.info("Existing Payment Modes are generated successfully");

    } catch (Exception e) {
      log.error("Error while generating existing payment modes : {}", e.getMessage());
    }
  }

  public void generateExistingEmployeeTypes() {
    try {
      EmployeeValuesDTO employeeValuesDTO =
          employeeFeignClient.getEmployeeValues("Bearer " + UserContext.getAccessToken());
      OrgDefaults existingEmployeeTypes =
          orgDefaultsRepository.findByOrganizationIdAndKey(
              UserContext.getLoggedInUserOrganization().getId(), "employeeTypes");
      if (existingEmployeeTypes == null) {
        existingEmployeeTypes = new OrgDefaults();
        existingEmployeeTypes.setKey("employeeTypes");
      }
      if (existingEmployeeTypes.getValues() == null) {
        existingEmployeeTypes.setValues(new HashSet<>());
      }
      if (employeeValuesDTO != null) {
        for (String employeeType : employeeValuesDTO.getEmploymentTypes()) {
          OrgValues employeeTypeValues = new OrgValues();
          employeeTypeValues.setValue(employeeType);
          employeeTypeValues.setDescription("Auto Generated");
          existingEmployeeTypes.getValues().add(employeeTypeValues);
        }
      }
      existingEmployeeTypes.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
      existingEmployeeTypes.setValues(new HashSet<>(existingEmployeeTypes.getValues()));
      orgDefaultsRepository.save(existingEmployeeTypes);

      log.info("Existing Employee Types are generated successfully");

    } catch (Exception e) {
      log.error("Error while generating existing employee types : {}", e.getMessage());
    }
  }

  public void generateExistingEmployeeDepartments() {
    try {
      EmployeeValuesDTO employeeValuesDTO =
          employeeFeignClient.getEmployeeValues("Bearer " + UserContext.getAccessToken());
      OrgDefaults existingEmployeeDepartments =
          orgDefaultsRepository.findByOrganizationIdAndKey(
              UserContext.getLoggedInUserOrganization().getId(), "employeeDepartments");
      if (existingEmployeeDepartments == null) {
        existingEmployeeDepartments = new OrgDefaults();
        existingEmployeeDepartments.setKey("employeeDepartments");
      }
      if (existingEmployeeDepartments.getValues() == null) {
        existingEmployeeDepartments.setValues(new HashSet<>());
      }
      if (employeeValuesDTO != null) {
        for (String employeeDepartment : employeeValuesDTO.getDepartments()) {
          OrgValues employeeDepartmentValues = new OrgValues();
          employeeDepartmentValues.setValue(employeeDepartment);
          employeeDepartmentValues.setDescription("Auto Generated");
          existingEmployeeDepartments.getValues().add(employeeDepartmentValues);
        }
      }
      existingEmployeeDepartments.setOrganizationId(
          UserContext.getLoggedInUserOrganization().getId());
      existingEmployeeDepartments.setValues(new HashSet<>(existingEmployeeDepartments.getValues()));
      orgDefaultsRepository.save(existingEmployeeDepartments);

      log.info("Existing Employee Departments are generated successfully");

    } catch (Exception e) {
      log.error("Error while generating existing employee departments : {}", e.getMessage());
    }
  }

  public void generateExistingDesignations() {
    try {
      EmployeeValuesDTO employeeValuesDTO =
          employeeFeignClient.getEmployeeValues("Bearer " + UserContext.getAccessToken());
      OrgDefaults existingDesignations =
          orgDefaultsRepository.findByOrganizationIdAndKey(
              UserContext.getLoggedInUserOrganization().getId(), "jobTitles");
      if (existingDesignations == null) {
        existingDesignations = new OrgDefaults();
        existingDesignations.setKey("jobTitles");
      }
      if (existingDesignations.getValues() == null) {
        existingDesignations.setValues(new HashSet<>());
      }
      if (employeeValuesDTO != null) {
        for (String designation : employeeValuesDTO.getDesignations()) {
          OrgValues designationValues = new OrgValues();
          designationValues.setValue(designation);
          designationValues.setDescription("Auto Generated");
          existingDesignations.getValues().add(designationValues);
        }
      }
      existingDesignations.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
      existingDesignations.setValues(new HashSet<>(existingDesignations.getValues()));
      orgDefaultsRepository.save(existingDesignations);

      log.info("Existing Designations are generated successfully");

    } catch (Exception e) {
      log.error("Error while generating existing designations : {}", e.getMessage());
    }
  }
}
