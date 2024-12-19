package com.beeja.api.financemanagementservice.service;

import com.beeja.api.financemanagementservice.modals.payrollsettings.PayRollSettings;
import org.springframework.stereotype.Service;

@Service
public interface PayRollSettingsService {
    PayRollSettings createPayrollSetting(PayRollSettings payRollSettings);
}
