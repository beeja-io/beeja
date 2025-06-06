package com.beeja.api.accounts.serviceImpl;

import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.employeeSettings.OrgValues;
import com.beeja.api.accounts.repository.OrgDefaultsRepository;
import com.beeja.api.accounts.utils.UserContext;
import java.util.HashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrgDefaultsGenerationImpl {

  @Autowired private OrgDefaultsRepository orgDefaultsRepository;

  public void generateOrganizationDepartments() {
    OrgDefaults orgDepartments =
        orgDefaultsRepository.findByOrganizationIdAndKey(
            UserContext.getLoggedInUserOrganization().getId(), "departments");
    if (orgDepartments == null) {
      orgDepartments = new OrgDefaults();
    }
    orgDepartments.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
    orgDepartments.setKey("departments");

    OrgValues engineering = new OrgValues();
    engineering.setValue("Engineering");
    engineering.setDescription("Engineering Department");

    OrgValues hr = new OrgValues();
    hr.setValue("HR");
    hr.setDescription("Human Resources Department");

    OrgValues finance = new OrgValues();
    finance.setValue("Finance");
    finance.setDescription("Finance Department");

    OrgValues DevOps = new OrgValues();
    DevOps.setValue("DevOps");
    DevOps.setDescription("DevOps Department");

    if (orgDepartments.getValues() == null) {
      orgDepartments.setValues(new HashSet<>());
    }
    orgDepartments.getValues().add(hr);
    orgDepartments.getValues().add(engineering);
    orgDepartments.getValues().add(finance);
    orgDepartments.getValues().add(DevOps);

    orgDepartments.setValues(new HashSet<>(orgDepartments.getValues()));
    try {
      orgDefaultsRepository.save(orgDepartments);
    } catch (DuplicateKeyException e) {
      log.error(String.valueOf(e.getMessage()));
    }
  }

  public void generateJobTitles() {
    OrgDefaults orgJobTitles =
        orgDefaultsRepository.findByOrganizationIdAndKey(
            UserContext.getLoggedInUserOrganization().getId(), "jobTitles");
    if (orgJobTitles == null) {
      orgJobTitles = new OrgDefaults();
    }
    orgJobTitles.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
    orgJobTitles.setKey("jobTitles");

    OrgValues softwareEngineer = new OrgValues();
    softwareEngineer.setValue("Software Engineer");
    softwareEngineer.setDescription("Software Engineer");

    OrgValues hr = new OrgValues();
    hr.setValue("HR Manager");
    hr.setDescription("Human Resources");

    OrgValues finance = new OrgValues();
    finance.setValue("Finance Manager");
    finance.setDescription("Finance");

    OrgValues DevOps = new OrgValues();
    DevOps.setValue("DevOps Engineer");
    DevOps.setDescription("DevOps");

    if (orgJobTitles.getValues() == null) {
      orgJobTitles.setValues(new HashSet<>());
    }
    orgJobTitles.getValues().add(hr);
    orgJobTitles.getValues().add(softwareEngineer);
    orgJobTitles.getValues().add(finance);
    orgJobTitles.getValues().add(DevOps);

    orgJobTitles.setValues(new HashSet<>(orgJobTitles.getValues()));

    try {
      orgDefaultsRepository.save(orgJobTitles);
    } catch (DuplicateKeyException e) {
      log.error(String.valueOf(e.getMessage()));
    }
  }

  public void generateEmploymentTypes() {
    OrgDefaults orgEmploymentTypes =
        orgDefaultsRepository.findByOrganizationIdAndKey(
            UserContext.getLoggedInUserOrganization().getId(), "employmentTypes");
    if (orgEmploymentTypes == null) {
      orgEmploymentTypes = new OrgDefaults();
    }
    orgEmploymentTypes.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
    orgEmploymentTypes.setKey("employmentTypes");

    OrgValues fullTime = new OrgValues();
    fullTime.setValue("Full Time");
    fullTime.setDescription("Full Time Employment");

    OrgValues partTime = new OrgValues();
    partTime.setValue("Part Time");
    partTime.setDescription("Part Time Employment");

    OrgValues contract = new OrgValues();
    contract.setValue("Contract");
    contract.setDescription("Contract Employment");

    OrgValues intern = new OrgValues();
    intern.setValue("Intern");
    intern.setDescription("Internship");

    OrgValues unpaidIntern = new OrgValues();
    unpaidIntern.setValue("Unpaid Intern");
    unpaidIntern.setDescription("Unpaid Internship");

    if (orgEmploymentTypes.getValues() == null) {
      orgEmploymentTypes.setValues(new HashSet<>());
    }
    orgEmploymentTypes.getValues().add(fullTime);
    orgEmploymentTypes.getValues().add(partTime);
    orgEmploymentTypes.getValues().add(contract);
    orgEmploymentTypes.getValues().add(intern);
    orgEmploymentTypes.getValues().add(unpaidIntern);

    orgEmploymentTypes.setValues(new HashSet<>(orgEmploymentTypes.getValues()));
    try {
      orgDefaultsRepository.save(orgEmploymentTypes);
    } catch (DuplicateKeyException e) {
      log.error(String.valueOf(e.getMessage()));
    }
  }

  public void generateExpenseCategories() {
    OrgDefaults orgExpenseCategories =
        orgDefaultsRepository.findByOrganizationIdAndKey(
            UserContext.getLoggedInUserOrganization().getId(), "expenseCategories");
    if (orgExpenseCategories == null) {
      orgExpenseCategories = new OrgDefaults();
    }
    orgExpenseCategories.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
    orgExpenseCategories.setKey("expenseCategories");

    OrgValues travel = new OrgValues();
    travel.setValue("Travel");
    travel.setDescription("Travel Expenses");

    OrgValues officeSupplies = new OrgValues();
    officeSupplies.setValue("Office Supplies");
    officeSupplies.setDescription("Office Supplies Expenses");

    OrgValues utilities = new OrgValues();
    utilities.setValue("Utilities");
    utilities.setDescription("Utilities Expenses");

    OrgValues rent = new OrgValues();
    rent.setValue("Rent");
    rent.setDescription("Rent Expenses");

    OrgValues insurance = new OrgValues();
    insurance.setValue("Insurance");
    insurance.setDescription("Insurance Expenses");

    OrgValues others = new OrgValues();
    others.setValue("Others");
    others.setDescription("Other Expenses");

    if (orgExpenseCategories.getValues() == null) {
      orgExpenseCategories.setValues(new HashSet<>());
    }
    orgExpenseCategories.getValues().add(travel);
    orgExpenseCategories.getValues().add(officeSupplies);
    orgExpenseCategories.getValues().add(utilities);
    orgExpenseCategories.getValues().add(rent);
    orgExpenseCategories.getValues().add(insurance);
    orgExpenseCategories.getValues().add(others);

    orgExpenseCategories.setValues(new HashSet<>(orgExpenseCategories.getValues()));

    try {
      orgDefaultsRepository.save(orgExpenseCategories);
    } catch (DuplicateKeyException e) {
      log.error(String.valueOf(e.getMessage()));
    }
  }

  public void generateExpenseTypes() {
    OrgDefaults orgExpenseTypes =
        orgDefaultsRepository.findByOrganizationIdAndKey(
            UserContext.getLoggedInUserOrganization().getId(), "expenseTypes");
    if (orgExpenseTypes == null) {
      orgExpenseTypes = new OrgDefaults();
    }
    orgExpenseTypes.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
    orgExpenseTypes.setKey("expenseTypes");

    OrgValues airfare = new OrgValues();
    airfare.setValue("Airfare");
    airfare.setDescription("Airfare Expenses");

    OrgValues hotel = new OrgValues();
    hotel.setValue("Hotel");
    hotel.setDescription("Hotel Expenses");

    OrgValues gas = new OrgValues();
    gas.setValue("Gas");
    gas.setDescription("Gas Expenses");

    OrgValues electricity = new OrgValues();
    electricity.setValue("Electricity");
    electricity.setDescription("Electricity Expenses");

    OrgValues water = new OrgValues();
    water.setValue("Water");
    water.setDescription("Water Expenses");

    OrgValues others = new OrgValues();
    others.setValue("Others");
    others.setDescription("Other Expenses");

    if (orgExpenseTypes.getValues() == null) {
      orgExpenseTypes.setValues(new HashSet<>());
    }
    orgExpenseTypes.getValues().add(airfare);
    orgExpenseTypes.getValues().add(hotel);
    orgExpenseTypes.getValues().add(gas);
    orgExpenseTypes.getValues().add(electricity);
    orgExpenseTypes.getValues().add(water);
    orgExpenseTypes.getValues().add(others);
    orgExpenseTypes.setValues(new HashSet<>(orgExpenseTypes.getValues()));
    try {
      orgDefaultsRepository.save(orgExpenseTypes);
    } catch (DuplicateKeyException e) {
      log.error(String.valueOf(e.getMessage()));
    }
  }

  public void generatePaymentModes() {
    OrgDefaults orgPaymentModes =
        orgDefaultsRepository.findByOrganizationIdAndKey(
            UserContext.getLoggedInUserOrganization().getId(), "paymentModes");
    if (orgPaymentModes == null) {
      orgPaymentModes = new OrgDefaults();
    }
    orgPaymentModes.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
    orgPaymentModes.setKey("paymentModes");

    OrgValues cash = new OrgValues();
    cash.setValue("Cash");
    cash.setDescription("Cash Payment");

    OrgValues creditCard = new OrgValues();
    creditCard.setValue("Credit Card");
    creditCard.setDescription("Credit Card Payment");

    OrgValues debitCard = new OrgValues();
    debitCard.setValue("Debit Card");
    debitCard.setDescription("Debit Card Payment");

    OrgValues netBanking = new OrgValues();
    netBanking.setValue("Net Banking");
    netBanking.setDescription("Net Banking Payment");

    OrgValues others = new OrgValues();
    others.setValue("Others");
    others.setDescription("Other Payment Modes");

    if (orgPaymentModes.getValues() == null) {
      orgPaymentModes.setValues(new HashSet<>());
    }
    orgPaymentModes.getValues().add(cash);
    orgPaymentModes.getValues().add(creditCard);
    orgPaymentModes.getValues().add(debitCard);
    orgPaymentModes.getValues().add(netBanking);
    orgPaymentModes.getValues().add(others);

    orgPaymentModes.setValues(new HashSet<>(orgPaymentModes.getValues()));

    try {
      orgDefaultsRepository.save(orgPaymentModes);
    } catch (DuplicateKeyException e) {
      log.error(String.valueOf(e.getMessage()));
    }
  }
}
