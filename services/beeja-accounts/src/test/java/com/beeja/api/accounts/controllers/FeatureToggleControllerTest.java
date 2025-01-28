package com.beeja.api.accounts.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.beeja.api.accounts.constants.PermissionConstants;
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
}