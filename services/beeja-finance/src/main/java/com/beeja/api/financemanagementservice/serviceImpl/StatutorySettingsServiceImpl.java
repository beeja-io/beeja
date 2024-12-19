package com.beeja.api.financemanagementservice.serviceImpl;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.exceptions.BadRequestException;
import com.beeja.api.financemanagementservice.exceptions.ResourceNotFoundException;
import com.beeja.api.financemanagementservice.modals.payrollsettings.PayRollSettings;
import com.beeja.api.financemanagementservice.modals.payrollsettings.StatutoryComponent;
import com.beeja.api.financemanagementservice.modals.payrollsettings.ProfessionalTax;
import com.beeja.api.financemanagementservice.modals.payrollsettings.StateInsurance;
import com.beeja.api.financemanagementservice.modals.payrollsettings.ProvidentFund;
import com.beeja.api.financemanagementservice.repository.PayRollSettingsRepository;
import com.beeja.api.financemanagementservice.service.StatutorySettingsService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class StatutorySettingsServiceImpl implements StatutorySettingsService {

    @Autowired
    private PayRollSettingsRepository payRollSettingsRepository;

    @Override
    public StatutoryComponent addStatutoryComponent(Map<String, Object> requestBody) {
        String componentType = (String) requestBody.get("componentType");
        if (componentType == null) {
            throw new IllegalArgumentException("Component type is required.");
        }

        StatutoryComponent component = createComponent(componentType, requestBody);
        component.setComponentType(componentType);
        component.setDeductionCycle((String) requestBody.getOrDefault("deductionCycle", "Monthly"));
        component.setIsActive((Boolean) requestBody.getOrDefault("isActive", true));
        component.setCreatedAt(new Date());
        component.setModifiedAt(new Date());
        component.setId(new ObjectId().toString());

        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        PayRollSettings payrollSettings = payRollSettingsRepository
                .findByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("No payroll settings found for organization: " + organizationId));

        if (payrollSettings.getStatutoryComponents() == null) {
            payrollSettings.setStatutoryComponents(new ArrayList<>());
        }
        payrollSettings.getStatutoryComponents().add(component);

        try {
            payRollSettingsRepository.save(payrollSettings);
            log.info("Successfully added statutory component: {}", component);
        } catch (Exception e) {
            log.error("Error saving statutory component: {}", e.getMessage());
            throw new BadRequestException("Failed to save statutory component due to a database error.");
        }

        return component;
    }

    private StatutoryComponent createComponent(String componentType, Map<String, Object> requestBody) {
        switch (componentType.toUpperCase()) {
            case "EPF":
                ProvidentFund providentFund = new ProvidentFund();
                validateProvidentFund(requestBody, providentFund);
                return providentFund;
            case "ESI":
                StateInsurance stateInsurance = new StateInsurance();
                validateStateInsurance(requestBody, stateInsurance);
                return stateInsurance;
            case "PT":
                ProfessionalTax professionalTax = new ProfessionalTax();
                validateProfessionalTax(requestBody, professionalTax);
                return professionalTax;
            default:
                throw new BadRequestException("Unknown component type: " + componentType);
        }
    }

    private void validateProvidentFund(Map<String, Object> requestBody, ProvidentFund providentFund) {
        if (requestBody.containsKey("epfNumber")) {
            providentFund.setEpfNumber((String) requestBody.get("epfNumber"));
        }
        if (requestBody.containsKey("restrictedPfWage")) {
            Double restrictedPFWage = (Double) requestBody.get("restrictedPfWage");
            if (restrictedPFWage < 0) {
                throw new BadRequestException("Restricted PF Wage cannot be negative.");
            }
            providentFund.setRestrictedPfWage(restrictedPFWage);
        }
        providentFund.setEmployeeContributionPercentage(Constants.EPF_EMPLOYEE_CONTRIBUTION_PERCENTAGE);
        providentFund.setEmployerContributionPercentage(Constants.EPF_EMPLOYER_CONTRIBUTION_PERCENTAGE);
    }

    private void validateStateInsurance(Map<String, Object> requestBody, StateInsurance stateInsurance) {
        if (requestBody.containsKey("esiNumber")) {
            stateInsurance.setEsiNumber((String) requestBody.get("esiNumber"));
        }
        if (requestBody.containsKey("restrictedPfWage")) {
            Double restrictedPfWage = (Double) requestBody.get("restrictedPfWage");
            if (restrictedPfWage < 0) {
                throw new BadRequestException("Restricted PF Wage cannot be negative.");
            }
            stateInsurance.setRestrictedPfWage(restrictedPfWage);
        }
        stateInsurance.setEmployerContributionPercentage(Constants.ESI_EMPLOYER_CONTRIBUTION_PERCENTAGE);
        stateInsurance.setEmployeeContributionPercentage(Constants.ESI_EMPLOYEE_CONTRIBUTION_PERCENTAGE);
    }

    private void validateProfessionalTax(Map<String, Object> requestBody, ProfessionalTax professionalTax) {
        if (requestBody.containsKey("taxAmount")) {
            Double taxAmount = (Double) requestBody.get("taxAmount");
            if (taxAmount < 0) {
                throw new BadRequestException("Tax amount cannot be negative.");
            }
            professionalTax.setTaxAmount(taxAmount);
        }
        if (requestBody.containsKey("state")) {
            professionalTax.setState((String) requestBody.get("state"));
        }
    }

    @Override
    public StatutoryComponent updateStatutoryComponent(String componentId, Map<String, Object> requestBody) {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        Optional<PayRollSettings> payrollSettingsOpt = payRollSettingsRepository.findByOrganizationId(organizationId);

        if (payrollSettingsOpt.isEmpty()) {
            throw new ResourceNotFoundException("No payroll settings found for organization: " + organizationId);
        }

        PayRollSettings payrollSettings = payrollSettingsOpt.get();
        Optional<StatutoryComponent> existingComponentOpt = payrollSettings.getStatutoryComponents().stream()
                .filter(component -> componentId.equals(component.getId()))
                .findFirst();

        if (existingComponentOpt.isEmpty()) {
            throw new ResourceNotFoundException("No statutory component found with ID: " + componentId);
        }

        StatutoryComponent existingComponent = existingComponentOpt.get();
        updateComponentDetails(existingComponent, requestBody);
        try {
            payRollSettingsRepository.save(payrollSettings);
        } catch (Exception e) {
            log.error("Error updating statutory component with ID: {}. Error: {}", componentId, e.getMessage(), e);
            throw new BadRequestException("Failed to update statutory component due to a database error.");
        }

        return existingComponent;
    }

    private void updateComponentDetails(StatutoryComponent component, Map<String, Object> requestBody) {
        if (requestBody.containsKey("deductionCycle")) {
            component.setDeductionCycle((String) requestBody.get("deductionCycle"));
        }
        if (requestBody.containsKey("isActive")) {
            component.setIsActive((Boolean) requestBody.get("isActive"));
        }
        if (component instanceof ProfessionalTax) {
            validateProfessionalTax(requestBody, (ProfessionalTax) component);
        } else if (component instanceof ProvidentFund) {
            validateProvidentFund(requestBody, (ProvidentFund) component);
        } else if (component instanceof StateInsurance) {
            validateStateInsurance(requestBody, (StateInsurance) component);
        }

        component.setModifiedAt(new Date());
    }

    @Override
    public List<StatutoryComponent> getAllStatutoryComponents() {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        PayRollSettings payrollSettings = payRollSettingsRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("No payroll settings found for organization: " + organizationId));

        return payrollSettings.getStatutoryComponents();
    }
}
