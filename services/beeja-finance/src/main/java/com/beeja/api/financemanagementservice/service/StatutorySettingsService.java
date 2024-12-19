package com.beeja.api.financemanagementservice.service;

import com.beeja.api.financemanagementservice.modals.payrollsettings.StatutoryComponent;

import java.util.List;
import java.util.Map;

public interface StatutorySettingsService {
    StatutoryComponent addStatutoryComponent(Map<String, Object> request);
    StatutoryComponent updateStatutoryComponent(String componentId, Map<String, Object> requestBody);
    List<StatutoryComponent> getAllStatutoryComponents();
}
