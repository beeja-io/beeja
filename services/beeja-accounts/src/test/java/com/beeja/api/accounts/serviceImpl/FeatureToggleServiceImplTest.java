package com.beeja.api.accounts.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import com.beeja.api.accounts.enums.FeatureToggles;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.featureFlags.FeatureToggle;
import com.beeja.api.accounts.repository.FeatureToggleRepository;
import java.util.HashSet;
import java.util.Set;

import com.beeja.api.accounts.utils.BuildErrorMessage;
import com.beeja.api.accounts.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FeatureToggleServiceImplTest {

    @InjectMocks private FeatureToggleServiceImpl featureToggleService;

    @Mock private FeatureToggleRepository featureToggleRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFeatureToggleByOrganizationId_Success() {
        // Given
        String organizationId = "org123";
        Set<FeatureToggles> toggles = new HashSet<>();
        toggles.add(FeatureToggles.EMPLOYEE_MANAGEMENT);
        toggles.add(FeatureToggles.DOCUMENT_MANAGEMENT);

        FeatureToggle featureToggle =
                FeatureToggle.builder()
                        .id("toggle123")
                        .organizationId(organizationId)
                        .featureToggles(toggles)
                        .build();

        when(featureToggleRepository.findByOrganizationId(organizationId)).thenReturn(featureToggle);
        FeatureToggle result = featureToggleService.getFeatureToggleByOrganizationId(organizationId);
        assertEquals(organizationId, result.getOrganizationId());
        assertEquals(2, result.getFeatureToggles().size());
        assertEquals(toggles, result.getFeatureToggles());
        verify(featureToggleRepository, times(2)).findByOrganizationId(organizationId);
    }

    @Test
    public void testGetFeatureToggleByOrganizationId_NotFound() {
        String organizationId = "org123";
        when(featureToggleRepository.findByOrganizationId(organizationId)).thenReturn(null);
        String expectedErrorMessage = BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                ErrorCode.FEATURES_ARE_NOT_FOUND,
                Constants.RESOURCE_NOT_FOUND
        );
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> featureToggleService.getFeatureToggleByOrganizationId(organizationId)
        );
        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(featureToggleRepository, times(1)).findByOrganizationId(organizationId);
    }

    @Test
    public void testUpdateFeatureToggleByOrganizationId_Success() throws Exception {
        String organizationId = "org123";
        Set<FeatureToggles> newFeatureToggles = new HashSet<>();
        newFeatureToggles.add(FeatureToggles.EMPLOYEE_MANAGEMENT);
        newFeatureToggles.add(FeatureToggles.DOCUMENT_MANAGEMENT);

        FeatureToggle existingFeatureToggle =
                FeatureToggle.builder()
                        .id("toggle123")
                        .organizationId(organizationId)
                        .featureToggles(new HashSet<>())
                        .build();

        FeatureToggle updatedFeatureToggle =
                FeatureToggle.builder()
                        .organizationId(organizationId)
                        .featureToggles(newFeatureToggles)
                        .build();

        when(featureToggleRepository.findByOrganizationId(organizationId))
                .thenReturn(existingFeatureToggle);
        when(featureToggleRepository.save(any(FeatureToggle.class))).thenReturn(existingFeatureToggle);
        FeatureToggle result =
                featureToggleService.updateFeatureToggleByOrganizationId(
                        organizationId, updatedFeatureToggle);
        assertEquals(newFeatureToggles, result.getFeatureToggles());
        verify(featureToggleRepository, times(1)).findByOrganizationId(organizationId);
        verify(featureToggleRepository, times(1)).save(existingFeatureToggle);
        assertEquals(newFeatureToggles, existingFeatureToggle.getFeatureToggles());
    }


    @Test
    public void testUpdateFeatureToggleByOrganizationId_NotFound() {
        String organizationId = "org123";
        FeatureToggle updatedFeatureToggle =
                FeatureToggle.builder()
                        .organizationId(organizationId)
                        .featureToggles(new HashSet<>())
                        .build();

        when(featureToggleRepository.findByOrganizationId(organizationId)).thenReturn(null);
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> featureToggleService.updateFeatureToggleByOrganizationId(
                        organizationId, updatedFeatureToggle)
        );

        assertEquals(Constants.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(featureToggleRepository, times(1)).findByOrganizationId(organizationId);
        verify(featureToggleRepository, times(0)).save(any(FeatureToggle.class));
    }

    @Test
    public void testUpdateFeatureToggleByOrganizationId_SaveFailure() {
        String organizationId = "org123";
        Set<FeatureToggles> newFeatureToggles = new HashSet<>();
        newFeatureToggles.add(FeatureToggles.EMPLOYEE_MANAGEMENT);

        FeatureToggle existingFeatureToggle =
                FeatureToggle.builder()
                        .id("toggle123")
                        .organizationId(organizationId)
                        .featureToggles(new HashSet<>())
                        .build();

        FeatureToggle updatedFeatureToggle =
                FeatureToggle.builder()
                        .organizationId(organizationId)
                        .featureToggles(newFeatureToggles)
                        .build();

        when(featureToggleRepository.findByOrganizationId(organizationId))
                .thenReturn(existingFeatureToggle);
        when(featureToggleRepository.save(any(FeatureToggle.class)))
                .thenThrow(new RuntimeException("Database error"));
        Exception exception = assertThrows(
                Exception.class,
                () -> featureToggleService.updateFeatureToggleByOrganizationId(
                        organizationId, updatedFeatureToggle)
        );

        String expectedErrorMessage = BuildErrorMessage.buildErrorMessage(
                ErrorType.API_ERROR,
                ErrorCode.RESOURCE_CREATING_ERROR,
                Constants.RESOURCE_UPDATING_ERROR_FEATURE_TOGGLE);

        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(featureToggleRepository, times(1)).findByOrganizationId(organizationId);
        verify(featureToggleRepository, times(1)).save(existingFeatureToggle);
    }

}