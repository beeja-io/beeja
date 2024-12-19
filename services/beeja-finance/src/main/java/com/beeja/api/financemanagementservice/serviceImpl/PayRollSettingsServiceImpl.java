package com.beeja.api.financemanagementservice.serviceImpl;

import com.beeja.api.financemanagementservice.modals.payrollsettings.PayRollSettings;
import com.beeja.api.financemanagementservice.repository.PayRollSettingsRepository;
import com.beeja.api.financemanagementservice.service.PayRollSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayRollSettingsServiceImpl implements PayRollSettingsService {

    @Autowired
    private PayRollSettingsRepository payRollSettingsRepository;

    @Override
    public PayRollSettings createPayrollSetting(PayRollSettings payRollSettings) {
        return payRollSettingsRepository.save(payRollSettings); // Save the payroll setting
    }
}
