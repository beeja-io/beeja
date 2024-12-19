package com.beeja.api.financemanagementservice.controllers;

import com.beeja.api.financemanagementservice.modals.payrollsettings.StatutoryComponent;
import com.beeja.api.financemanagementservice.service.StatutorySettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/statutory-settings")
public class StatutorySettingsController {

    @Autowired
    private StatutorySettingsService statutorySettingsService;

    @PostMapping
//    @HasPermission(Constants.CREATE_PAYROLL)
    public ResponseEntity<StatutoryComponent> addStatutoryComponent(
            @RequestBody Map<String, Object> request) {
        StatutoryComponent addedComponent = statutorySettingsService.addStatutoryComponent(request);
        return ResponseEntity.ok(addedComponent);
    }

    @PutMapping("/{id}")
//    @HasPermission(Constants.UPDATE_PAYROLL)
    public ResponseEntity<StatutoryComponent> updateStatutoryComponent(
            @PathVariable("id") String componentId,
            @RequestBody Map<String, Object> updates) {

        StatutoryComponent updatedComponent = statutorySettingsService.updateStatutoryComponent(componentId, updates);
        return ResponseEntity.ok(updatedComponent);
    }

    @GetMapping
//    @HasPermission(Constants.READ_PAYROLL)
    public ResponseEntity<List<StatutoryComponent>> getAllStatutoryComponents() {
        List<StatutoryComponent> statutoryComponents = statutorySettingsService.getAllStatutoryComponents();
        return ResponseEntity.ok(statutoryComponents);
    }
}
