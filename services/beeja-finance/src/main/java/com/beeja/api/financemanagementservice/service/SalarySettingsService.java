package com.beeja.api.financemanagementservice.service;
import com.beeja.api.financemanagementservice.modals.payrollsettings.SalaryComponent;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public interface SalarySettingsService {

    SalaryComponent addSalaryComponent(SalaryComponent salaryComponent);

    Optional<SalaryComponent> getSalaryComponent(String componentId);

    List<SalaryComponent> getAllSalaryComponents();

    SalaryComponent updateSalaryComponent(String componentId, SalaryComponent salaryComponent);
}
