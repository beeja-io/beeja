package com.beeja.api.accounts.service;


import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.repository.OrgDefaultsRepository;
import com.beeja.api.accounts.serviceImpl.OrgDefaultsGenerationImpl;
import com.beeja.api.accounts.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrgDefaultsGenerationImplTest {

    @InjectMocks
    private OrgDefaultsGenerationImpl orgDefaultsGeneration;

    @Mock
    private OrgDefaultsRepository orgDefaultsRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up UserContexts
        Organization org = new Organization();
        org.setId("org123");
        UserContext.setLoggedInUserOrganization(org);
    }

    @Test
    public void testGenerateOrganizationDepartments() {
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "departments")).thenReturn(null);

        orgDefaultsGeneration.generateOrganizationDepartments();

        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }

    @Test
    public void testGenerateJobTitles() {
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "jobTitles")).thenReturn(null);

        orgDefaultsGeneration.generateJobTitles();

        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }

    @Test
    public void testGenerateEmploymentTypes() {
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "employmentTypes")).thenReturn(null);

        orgDefaultsGeneration.generateEmploymentTypes();

        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }

    @Test
    public void testGenerateExpenseCategories() {
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "expenseCategories")).thenReturn(null);

        orgDefaultsGeneration.generateExpenseCategories();

        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }

    @Test
    public void testGenerateExpenseTypes() {
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "expenseTypes")).thenReturn(null);

        orgDefaultsGeneration.generateExpenseTypes();

        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }

    @Test
    public void testGeneratePaymentModes() {
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "paymentModes")).thenReturn(null);

        orgDefaultsGeneration.generatePaymentModes();

        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }

    @Test
    public void testGenerateDepartments_WhenAlreadyExists() {
        OrgDefaults existingDefaults = new OrgDefaults();
        existingDefaults.setValues(new HashSet<>());

        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "departments")).thenReturn(existingDefaults);

        orgDefaultsGeneration.generateOrganizationDepartments();

        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }

    @Test
    public void testGenerateJobTitles_ThrowsDuplicateKeyException() {
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "jobTitles")).thenReturn(null);
        doThrow(new org.springframework.dao.DuplicateKeyException("Duplicate")).when(orgDefaultsRepository).save(any(OrgDefaults.class));

        orgDefaultsGeneration.generateJobTitles();

        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }
}

