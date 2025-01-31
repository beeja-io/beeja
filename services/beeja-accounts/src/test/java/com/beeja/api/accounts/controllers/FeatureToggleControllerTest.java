package com.beeja.api.accounts.controllers;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.featureFlags.FeatureToggle;
import com.beeja.api.accounts.service.FeatureToggleService;
import com.beeja.api.accounts.utils.UserContext;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class FeatureToggleControllerTest {

    @InjectMocks private FeatureToggleController featureToggleController;

    @Mock private FeatureToggleService featureToggleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnFeatureToggleForSpecifiedOrganizationIdWhenPermissionExists() {
        String organizationId = "org123";
        FeatureToggle mockFeatureToggle = new FeatureToggle();
        mockFeatureToggle.setId("ft123");

        Set<String> permissions = Set.of(PermissionConstants.READ_ALL_FEATURE_TOGGLES);
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserPermissions).thenReturn(permissions);
            when(featureToggleService.getFeatureToggleByOrganizationId(organizationId))
                    .thenReturn(mockFeatureToggle);
            ResponseEntity<FeatureToggle> response =
                    featureToggleController.getFeatureToggleByOrganizationId(organizationId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockFeatureToggle, response.getBody());
            verify(featureToggleService).getFeatureToggleByOrganizationId(organizationId);
        }
    }

    @Test
    void shouldUseLoggedInUserOrganizationIdWhenPermissionIsMissing() {
        String providedOrganizationId = "org123";
        String loggedInUserOrgId = "org456";
        FeatureToggle mockFeatureToggle = new FeatureToggle();
        mockFeatureToggle.setId("ft456");

        Set<String> permissions = Set.of();

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            Organization mockOrganization = mock(Organization.class);
            when(mockOrganization.getId()).thenReturn(loggedInUserOrgId);
            mockedUserContext.when(UserContext::getLoggedInUserPermissions).thenReturn(permissions);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            when(featureToggleService.getFeatureToggleByOrganizationId(loggedInUserOrgId))
                    .thenReturn(mockFeatureToggle);

            ResponseEntity<FeatureToggle> response =
                    featureToggleController.getFeatureToggleByOrganizationId(providedOrganizationId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockFeatureToggle, response.getBody());
            verify(featureToggleService).getFeatureToggleByOrganizationId(loggedInUserOrgId);
        }
    }

    @Test
    void shouldReturnOkWithNullWhenFeatureToggleDoesNotExist() {
        String organizationId = "org999";

        Set<String> permissions = Set.of(PermissionConstants.READ_ALL_FEATURE_TOGGLES);

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserPermissions).thenReturn(permissions);

            when(featureToggleService.getFeatureToggleByOrganizationId(organizationId))
                    .thenReturn(null);

            ResponseEntity<FeatureToggle> response =
                    featureToggleController.getFeatureToggleByOrganizationId(organizationId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNull(response.getBody());
            verify(featureToggleService).getFeatureToggleByOrganizationId(organizationId);
        }
    }


    @Test
    void testUpdateFeatureToggleByOrganizationId_Success() throws Exception {

        String organizationId = "org123";
        FeatureToggle featureToggle = new FeatureToggle();
        when(featureToggleService.updateFeatureToggleByOrganizationId(organizationId, featureToggle))
                .thenReturn(featureToggle);


        ResponseEntity<FeatureToggle> response =
                featureToggleController.updateFeatureToggleByOrganizationId(organizationId, featureToggle);

        assertEquals(ResponseEntity.ok(featureToggle), response);
        verify(featureToggleService, times(1))
                .updateFeatureToggleByOrganizationId(organizationId, featureToggle);
    }


    @Test
    void testUpdateFeatureToggleByOrganizationId_Failure() throws Exception {
        String organizationId = "org123";
        FeatureToggle featureToggle = new FeatureToggle();

        when(featureToggleService.updateFeatureToggleByOrganizationId(organizationId, featureToggle))
                .thenThrow(new RuntimeException("Failed to update feature toggle"));

        try {
            featureToggleController.updateFeatureToggleByOrganizationId(organizationId, featureToggle);
            fail("Expected an exception to be thrown");
        } catch (Exception ex) {

            assertTrue(ex instanceof RuntimeException);
            assertEquals("Failed to update feature toggle", ex.getMessage());
        }
    }



}