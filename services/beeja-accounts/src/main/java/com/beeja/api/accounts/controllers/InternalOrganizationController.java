package com.beeja.api.accounts.controllers;

import com.beeja.api.accounts.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/api/organizations")
@RequiredArgsConstructor
public class InternalOrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/birthday-notifications/enabled")
    public List<String> getEnabledOrganizations() throws Exception {
        return organizationService.getOrganizationsWithBirthdayNotificationsEnabled();
    }

    @GetMapping("/{orgId}/birthday-webhook")
    public String getBirthdayWebhookUrl(@PathVariable String orgId) throws Exception {
        return organizationService.getBirthdayWebhookUrl(orgId);
    }
}
