package com.beeja.api.financemanagementservice.serviceImpl;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.exceptions.BadRequestException;
import com.beeja.api.financemanagementservice.exceptions.ResourceNotFoundException;
import com.beeja.api.financemanagementservice.modals.payrollsettings.PayRollSettings;
import com.beeja.api.financemanagementservice.modals.payrollsettings.SalaryComponent;
import com.beeja.api.financemanagementservice.repository.PayRollSettingsRepository;
import com.beeja.api.financemanagementservice.service.SalarySettingsService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SalarySettingsServiceImpl implements SalarySettingsService {

    @Autowired
    private PayRollSettingsRepository payRollSettingsRepository;

    @Override
    public SalaryComponent addSalaryComponent(SalaryComponent salaryComponent) {

        salaryComponent.setCreatedAt(new Date());
        salaryComponent.setModifiedAt(new Date());
        salaryComponent.setId(new ObjectId().toString());
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        Optional<PayRollSettings> payrollSettingsOpt = payRollSettingsRepository.findByOrganizationId(organizationId);

        if (payrollSettingsOpt.isPresent()) {
            PayRollSettings payrollSettings = payrollSettingsOpt.get();
            if (payrollSettings.getSalaryComponents() == null) {
                payrollSettings.setSalaryComponents(new ArrayList<>());
            }
            payrollSettings.getSalaryComponents().add(salaryComponent);
            try {
                payRollSettingsRepository.save(payrollSettings);
            } catch (Exception e) {
                throw new BadRequestException("Failed to save salary component due to a database error.");
            }
            return salaryComponent;
        } else {
            throw new ResourceNotFoundException("No payroll settings found for organization: " + organizationId);
        }
    }


    @Override
    public Optional<SalaryComponent> getSalaryComponent(String componentId) {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        return payRollSettingsRepository.findByOrganizationId(organizationId)
                .flatMap(payrollSettings -> payrollSettings.getSalaryComponents()
                        .stream()
                        .filter(component -> componentId.equals(component.getId()))
                        .findFirst());
    }

    @Override
    public List<SalaryComponent> getAllSalaryComponents() {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
        return payRollSettingsRepository.findByOrganizationId(organizationId)
                .map(PayRollSettings::getSalaryComponents)
                .orElse(new ArrayList<>());
    }

    @Override
    public SalaryComponent updateSalaryComponent(String componentId, SalaryComponent salaryComponent) {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

        PayRollSettings payrollSettings = payRollSettingsRepository
                .findByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("No payroll settings found for organization: " + organizationId));

        SalaryComponent existingComponent = payrollSettings.getSalaryComponents()
                .stream()
                .filter(component -> componentId.equals(component.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No salary component found with ID: " + componentId));

        updateSalaryComponentDetails(existingComponent, salaryComponent);

        try {
            payRollSettingsRepository.save(payrollSettings);
        } catch (Exception e) {
            throw new BadRequestException("Failed to update salary component due to a database error.");
        }
        return existingComponent;
    }

    private void updateSalaryComponentDetails(SalaryComponent existingComponent,SalaryComponent salaryComponent) {

        if (salaryComponent.getComponentName() != null) {
            existingComponent.setComponentName(salaryComponent.getComponentName());
        }
        if (salaryComponent.getCalculationType() != null) {
            existingComponent.setComponentType(salaryComponent.getComponentType());
        }
        if (salaryComponent.getIsActive() != null) {
            existingComponent.setIsActive(salaryComponent.getIsActive());
        }
        if (salaryComponent.getAmount() != null) {
            existingComponent.setAmount((Double) salaryComponent.getAmount());
        }
        existingComponent.setModifiedAt(new Date());
    }
}