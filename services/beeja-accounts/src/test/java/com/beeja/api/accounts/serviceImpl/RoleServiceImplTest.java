package com.beeja.api.accounts.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import com.beeja.api.accounts.exceptions.CustomAccessDenied;
import com.beeja.api.accounts.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.repository.OrganizationRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.requests.AddRoleRequest;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RoleServiceImplTest {

    @InjectMocks RoleServiceImpl roleService;

    @Mock RolesRepository rolesRepository;
    @Mock UserRepository userRepository;

    @Mock private AddRoleRequest newRoleRequest;
    private AddRoleRequest updatedRole;
    private Organization organization;
    private Role role;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        newRoleRequest = new AddRoleRequest();
        newRoleRequest.setName("Admin");
        newRoleRequest.setDescription("Administrator role");
        newRoleRequest.setPermissions(Set.of("READ", "WRITE"));

        updatedRole = new AddRoleRequest();
        updatedRole.setName("Admin Updated");
        updatedRole.setDescription("Updated administrator role");
        updatedRole.setPermissions(Set.of("READ", "WRITE", "EXECUTE"));

        role = new Role("roleId", "Admin", "Administrator role", Set.of("READ", "WRITE"), "org1");

        organization = new Organization();
        organization.setId("org1");
        organization.setName("Organization 1");
        organization.setEmail("org1@example.com");
        organization.setSubscriptionId("sub123");
        organization.setEmailDomain("example.com");
        organization.setContactMail("contact@example.com");
        organization.setWebsite("https://www.example.com");

        UserContext.setLoggedInUserOrganization(organization);
    }

    @Test
    void addRoleToOrganization_Success() throws Exception {
        when(rolesRepository.findByNameAndOrganizationId(newRoleRequest.getName(), organization.getId()))
                .thenReturn(null);
        when(rolesRepository.save(any(Role.class))).thenReturn(role);
        Role createdRole = roleService.addRoleToOrganization(newRoleRequest);

        assertNotNull(createdRole);
        assertEquals(newRoleRequest.getName(), createdRole.getName());
        assertEquals(newRoleRequest.getDescription(), createdRole.getDescription());
        assertEquals(newRoleRequest.getPermissions(), createdRole.getPermissions());
        verify(rolesRepository).save(any(Role.class));
    }


    @Test
    void addRoleToOrganization_RoleAlreadyExists() {
        when(rolesRepository.findByNameAndOrganizationId(newRoleRequest.getName(), organization.getId()))
                .thenReturn(role);
        Exception exception = assertThrows(ResourceAlreadyFoundException.class, () -> {
            roleService.addRoleToOrganization(newRoleRequest);
        });

        assertTrue(exception.getMessage().contains(Constants.ROLE_ALREADY_FOUND + newRoleRequest.getName()));
        verify(rolesRepository, never()).save(any(Role.class));
    }

    @Test
    void addRoleToOrganization_DatabaseErrorDuringFetch() {

        when(rolesRepository.findByNameAndOrganizationId(newRoleRequest.getName(), organization.getId()))
                .thenThrow(new RuntimeException("DB Error"));

        Exception exception = assertThrows(Exception.class, () -> {
            roleService.addRoleToOrganization(newRoleRequest);
        });
        assertTrue(exception.getMessage().contains(Constants.ERROR_IN_FETCHING_ROLES));
        verify(rolesRepository, never()).save(any(Role.class));
    }

    @Test
    void addRoleToOrganization_DatabaseErrorDuringSave() {
        when(rolesRepository.findByNameAndOrganizationId(newRoleRequest.getName(), organization.getId()))
                .thenReturn(null);
        when(rolesRepository.save(any(Role.class))).thenThrow(new RuntimeException("DB Error"));
        Exception exception = assertThrows(Exception.class, () -> {
            roleService.addRoleToOrganization(newRoleRequest);
        });
        assertTrue(exception.getMessage().contains(Constants.ERROR_IN_CREATING_ROLE_TO_ORGANIZATION));
    }

    @Test
    void updateRolesOfOrganization_Success() throws Exception {
        when(rolesRepository.findByIdAndOrganizationId(role.getId(), organization.getId()))
                .thenReturn(role);
        when(rolesRepository.findByNameAndOrganizationId(updatedRole.getName(), organization.getId()))
                .thenReturn(null);
        when(rolesRepository.save(role)).thenReturn(role);
        Role updated = roleService.updateRolesOfOrganization(role.getId(), updatedRole);
        assertNotNull(updated);
        assertEquals(updatedRole.getName(), updated.getName());
        assertEquals(updatedRole.getDescription(), updated.getDescription());
        assertEquals(updatedRole.getPermissions(), updated.getPermissions());
    }

    @Test
    void updateRolesOfOrganization_RoleNotFound() {
        when(rolesRepository.findByIdAndOrganizationId(role.getId(), organization.getId()))
                .thenReturn(null);
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            roleService.updateRolesOfOrganization(role.getId(), updatedRole);
        });

        assertTrue(exception.getMessage().contains(Constants.ROLE_NOT_FOUND + role.getId()));
    }

    @Test
    void updateRolesOfOrganization_DuplicateRoleName() {
        when(rolesRepository.findByIdAndOrganizationId(role.getId(), organization.getId()))
                .thenReturn(role);
        when(rolesRepository.findByNameAndOrganizationId(updatedRole.getName(), organization.getId()))
                .thenReturn(new Role("anotherRoleId", updatedRole.getName(), "Duplicate role", Set.of(), "org1"));

        Exception exception = assertThrows(ResourceAlreadyFoundException.class, () -> {
            roleService.updateRolesOfOrganization(role.getId(), updatedRole);
        });
        assertTrue(exception.getMessage().contains(Constants.ROLE_ALREADY_FOUND + updatedRole.getName()));
    }


    @Test
    void updateRolesOfOrganization_DatabaseErrorDuringSave() {
        when(rolesRepository.findByIdAndOrganizationId(role.getId(), organization.getId()))
                .thenReturn(role);
        when(rolesRepository.findByNameAndOrganizationId(updatedRole.getName(), organization.getId()))
                .thenReturn(null);
        when(rolesRepository.save(role)).thenThrow(new RuntimeException("DB Error"));
        Exception exception = assertThrows(Exception.class, () -> {
            roleService.updateRolesOfOrganization(role.getId(), updatedRole);
        });
        assertTrue(exception.getMessage().contains(Constants.ERROR_IN_UPDATING_ROLE_TO_ORGANIZATION));
        assertTrue(exception.getMessage().contains(ErrorType.DB_ERROR.toString()));
        assertTrue(exception.getMessage().contains(ErrorCode.CANNOT_SAVE_CHANGES.toString()));
        verify(rolesRepository).save(any(Role.class));
    }

    @Test
    void deleteRolesOfOrganization_Success() throws Exception {
        when(rolesRepository.findByIdAndOrganizationId(role.getId(), organization.getId()))
                .thenReturn(role);
        when(userRepository.findByRoles(role)).thenReturn(List.of());
        Role deletedRole = roleService.deleteRolesOfOrganization(role.getId());
        assertNotNull(deletedRole);
        assertEquals(role.getId(), deletedRole.getId());
        verify(rolesRepository).delete(role);
    }

    @Test
    void deleteRolesOfOrganization_RoleNotFound() {
        when(rolesRepository.findByIdAndOrganizationId(role.getId(), organization.getId()))
                .thenReturn(null);
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            roleService.deleteRolesOfOrganization(role.getId());
        });
        assertTrue(exception.getMessage().contains(Constants.ROLE_NOT_FOUND + role.getId()));
    }

    @Test
    void deleteRolesOfOrganization_DefaultRoleDeletionDenied() {
        Role superAdminRole = new Role("roleId", "Super Admin", "Default role", Set.of(), "org1");
        when(rolesRepository.findByIdAndOrganizationId(superAdminRole.getId(), organization.getId()))
                .thenReturn(superAdminRole);
        Exception exception = assertThrows(CustomAccessDenied.class, () -> {
            roleService.deleteRolesOfOrganization(superAdminRole.getId());
        });
        assertTrue(exception.getMessage().contains(Constants.CANT_DELETE_DEFAULT_ROLE));
    }


    @Test
    void getAllRolesOfOrganization_Success() throws Exception {
        when(rolesRepository.findByOrganizationId(organization.getId())).thenReturn(List.of(role));
        List<Role> roles = roleService.getAllRolesOfOrganization(organization);
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals(role.getId(), roles.get(0).getId());
    }


    @Test
    void getAllRolesOfOrganization_Exception() throws Exception {
        when(rolesRepository.findByOrganizationId("org1"))
                .thenThrow(new RuntimeException("DB error"));
        Exception exception = assertThrows(Exception.class, () -> {
            roleService.getAllRolesOfOrganization(organization);
        });
        assertFalse(exception.getMessage().contains("Unable to fetch roles"));
    }


}