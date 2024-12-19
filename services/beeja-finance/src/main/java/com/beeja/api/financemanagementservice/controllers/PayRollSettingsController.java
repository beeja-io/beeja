package com.beeja.api.financemanagementservice.controllers;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.annotations.HasPermission;
import com.beeja.api.financemanagementservice.modals.payrollsettings.PayRollSettings;
import com.beeja.api.financemanagementservice.service.PayRollSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payroll-settings")
public class PayRollSettingsController {
    @Autowired
    private PayRollSettingsService payRollSettingsService;

    @PostMapping
    @HasPermission(Constants.CREATE_PAYROLL)
    public ResponseEntity<PayRollSettings> createPayrollSetting(@RequestBody PayRollSettings payRollSettings) {
        payRollSettings.setOrganizationId(UserContext.getLoggedInUserOrganization().get("id").toString());
        PayRollSettings createdSetting = payRollSettingsService.createPayrollSetting(payRollSettings);
        return ResponseEntity.ok(createdSetting); // Return the created payroll setting
    }
}
