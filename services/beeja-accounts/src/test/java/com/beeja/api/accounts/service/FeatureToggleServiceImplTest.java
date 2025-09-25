package com.beeja.api.accounts.service;
import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import com.beeja.api.accounts.enums.FeatureToggles;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.featureFlags.FeatureToggle;
import com.beeja.api.accounts.repository.FeatureToggleRepository;
import com.beeja.api.accounts.serviceImpl.FeatureToggleServiceImpl;
import com.beeja.api.accounts.utils.BuildErrorMessage;
import com.beeja.api.accounts.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeatureToggleServiceImplTest {

    @InjectMocks
    private FeatureToggleServiceImpl featureToggleService;

    @Mock
    private FeatureToggleRepository featureToggleRepository;

    private FeatureToggle featureToggle;
    private String organizationId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        organizationId = "org123";
        featureToggle = new FeatureToggle();
        featureToggle.setId("ft123");
    }

    @Test
    void shouldReturnFeatureToggleWhenFound() {
        // Mocking the behavior of the repository
        when(featureToggleRepository.findByOrganizationId(organizationId)).thenReturn(featureToggle);

        // Call the service method
        FeatureToggle result = featureToggleService.getFeatureToggleByOrganizationId(organizationId);

        // Assertions
        assertNotNull(result);                      // Assert that result is not null
        assertEquals(featureToggle, result);        // Assert that the result matches the expected feature toggle

        // Verify that the repository's method is called exactly once
        verify(featureToggleRepository, times(1)).findByOrganizationId(organizationId);
    }




    @Test
    void shouldThrowResourceNotFoundExceptionWhenFeatureToggleNotFound() {
        when(featureToggleRepository.findByOrganizationId(organizationId)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                featureToggleService.getFeatureToggleByOrganizationId(organizationId));

        assertEquals(BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                ErrorCode.FEATURES_ARE_NOT_FOUND,
                Constants.RESOURCE_NOT_FOUND), exception.getMessage());
        verify(featureToggleRepository, times(1)).findByOrganizationId(organizationId);
    }

    @Test
    void shouldUpdateFeatureToggleSuccessfully() throws Exception {
        // Create an updated feature toggle with a Set of FeatureToggles
        Set<FeatureToggles> updatedToggles = Set.of(FeatureToggles.ORGANIZATION_SETTINGS_PROFILE); // Use appropriate enums here
        FeatureToggle updatedFeatureToggle = new FeatureToggle();
        updatedFeatureToggle.setFeatureToggles(updatedToggles);

        // Mock the behavior of the repository
        when(featureToggleRepository.findByOrganizationId(organizationId)).thenReturn(featureToggle);
        when(featureToggleRepository.save(featureToggle)).thenReturn(featureToggle);

        // Call the service method
        FeatureToggle result = featureToggleService.updateFeatureToggleByOrganizationId(organizationId, updatedFeatureToggle);

        // Assert the result
        assertNotNull(result);
        assertEquals(featureToggle, result);
        verify(featureToggleRepository, times(1)).findByOrganizationId(organizationId);
        verify(featureToggleRepository, times(1)).save(featureToggle);
    }


    @Test
    void shouldThrowResourceNotFoundExceptionWhenFeatureToggleToUpdateNotFound() throws Exception {
        // Create an updated feature toggle with a Set of FeatureToggles (correcting the data type)
        Set<FeatureToggles> updatedToggles = Set.of(FeatureToggles.ORGANIZATION_SETTINGS_PROFILE); // Use your actual toggle values
        FeatureToggle updatedFeatureToggle = new FeatureToggle();
        updatedFeatureToggle.setFeatureToggles(updatedToggles);

        // Mock the repository to return null for the specified organizationId
        when(featureToggleRepository.findByOrganizationId(organizationId)).thenReturn(null);

        // Perform the service call and assert that a ResourceNotFoundException is thrown
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                featureToggleService.updateFeatureToggleByOrganizationId(organizationId, updatedFeatureToggle));

        // Validate the exception message
        assertEquals(Constants.RESOURCE_NOT_FOUND, exception.getMessage());

        // Verify that the repository method was called once
        verify(featureToggleRepository, times(1)).findByOrganizationId(organizationId);
    }


    @Test
    void shouldThrowExceptionWhenUpdateFails() throws Exception {
        // Create an updated feature toggle with a Set of FeatureToggles (correcting the data type)
        Set<FeatureToggles> updatedToggles = Set.of(FeatureToggles.ORGANIZATION_SETTINGS_PROFILE); // Use your actual toggle values
        FeatureToggle updatedFeatureToggle = new FeatureToggle();
        updatedFeatureToggle.setFeatureToggles(updatedToggles);

        // Mock the repository to return an existing feature toggle for the specified organizationId
        when(featureToggleRepository.findByOrganizationId(organizationId)).thenReturn(featureToggle);

        // Mock the save method to throw an exception when trying to save the updated feature toggle
        when(featureToggleRepository.save(featureToggle)).thenThrow(new RuntimeException("Save failed"));
        Exception exception = assertThrows(Exception.class, () ->
                featureToggleService.updateFeatureToggleByOrganizationId(organizationId, updatedFeatureToggle));
        assertEquals(BuildErrorMessage.buildErrorMessage(
                ErrorType.API_ERROR,
                ErrorCode.RESOURCE_CREATING_ERROR,
                Constants.RESOURCE_UPDATING_ERROR_FEATURE_TOGGLE), exception.getMessage());

        // Verify that both the findByOrganizationId and save methods were called once
        verify(featureToggleRepository, times(1)).findByOrganizationId(organizationId);
        verify(featureToggleRepository, times(1)).save(featureToggle);
    }

}

