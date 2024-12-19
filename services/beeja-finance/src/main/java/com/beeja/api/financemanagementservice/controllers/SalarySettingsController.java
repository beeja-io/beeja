package com.beeja.api.financemanagementservice.controllers;

import com.beeja.api.financemanagementservice.modals.payrollsettings.SalaryComponent;
import com.beeja.api.financemanagementservice.service.SalarySettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping("/v1/salary-settings")
public class SalarySettingsController {

    @Autowired
    private SalarySettingsService salarySettingsService;

    @PostMapping
    public ResponseEntity<SalaryComponent> addSalaryComponent(@RequestBody SalaryComponent salaryComponent) {
        SalaryComponent newComponent = salarySettingsService.addSalaryComponent(salaryComponent);
        return ResponseEntity.ok(newComponent);
    }

    @GetMapping("/{componentId}")
    public ResponseEntity<SalaryComponent> getSalaryComponent(@PathVariable String componentId) {
        return salarySettingsService.getSalaryComponent(componentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SalaryComponent>> getAllSalaryComponents() {
        List<SalaryComponent> components = salarySettingsService.getAllSalaryComponents();
        return ResponseEntity.ok(components);
    }

    @PutMapping("/{componentId}")
    public ResponseEntity<SalaryComponent> updateSalaryComponent(@PathVariable String componentId,
                                                                 @RequestBody SalaryComponent salaryComponent) {
        SalaryComponent updatedComponent = salarySettingsService.updateSalaryComponent(componentId, salaryComponent);
        return ResponseEntity.ok(updatedComponent);
    }
}
