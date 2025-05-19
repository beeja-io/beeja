package com.beeja.api.accounts.serviceImpl;

import com.beeja.api.accounts.enums.SubscriptionName;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.model.subscriptions.Permissions;
import com.beeja.api.accounts.repository.PermissionRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuperAdminPermissionUpdaterTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolesRepository rolesRepository;

    @InjectMocks
    @Spy
    private SuperAdminPermissionUpdater superAdminPermissionUpdater;

    private Permissions dbPermissions;
    private List<Role> superAdminRoles;
    private Set<String> jsonPermissions;

    @BeforeEach
    void setUp() {
        dbPermissions = new Permissions();
        dbPermissions.setId("1");
        dbPermissions.setName(SubscriptionName.ALL_PERMISSIONS);
        dbPermissions.setPermissions(new HashSet<>(Arrays.asList("CEMP", "UEMP", "REMP")));

        Role superAdminRole = new Role();
        superAdminRole.setId("2");
        superAdminRole.setName("Super Admin");
        superAdminRole.setPermissions(new HashSet<>(Arrays.asList("CEMP", "REMP")));

        superAdminRoles = List.of(superAdminRole);

        jsonPermissions = new HashSet<>(Arrays.asList("CEMP", "UEMP", "REMP", "IEM", "URAP"));

    }

    @Test
    void testUpdatePermissions() throws IOException {
        when(permissionRepository.findByName(SubscriptionName.ALL_PERMISSIONS)).thenReturn(dbPermissions);
        when(rolesRepository.findAllByName("Super Admin")).thenReturn(superAdminRoles);
        when(superAdminPermissionUpdater.getAllPermissions()).thenReturn(jsonPermissions);

        superAdminPermissionUpdater.updatePermissions();

        assertTrue(dbPermissions.getPermissions().containsAll(jsonPermissions));
        verify(permissionRepository, times(1)).save(dbPermissions);

        for (Role role : superAdminRoles) {
            assertTrue(role.getPermissions().containsAll(dbPermissions.getPermissions()));
            verify(rolesRepository, times(1)).save(role);
        }
    }

}
