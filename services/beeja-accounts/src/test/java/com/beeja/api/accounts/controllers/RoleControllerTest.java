package com.beeja.api.accounts.controllers;

import static org.bouncycastle.asn1.x509.X509ObjectIdentifiers.organization;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.requests.AddRoleRequest;
import com.beeja.api.accounts.service.RoleService;
import com.beeja.api.accounts.utils.UserContext;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

class RoleControllerTest {

    @InjectMocks private RoleController roleController;

    @Mock private RoleService roleService;

    @Mock private BindingResult MockBindingResult;

    private Organization organization;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleController = new RoleController();
        roleController.roleService = roleService;
        Organization organization = new Organization();
        organization.setId("org1");
        UserContext.setLoggedInUserOrganization(organization);
    }

    @Test
    void testGetAllRolesOfOrganization() throws Exception {
        Role role1 = new Role();
        role1.setId("role1");
        role1.setName("Admin");

        Role role2 = new Role();
        role2.setId("role2");
        role2.setName("User");

        List<Role> roles = Arrays.asList(role1, role2);
        when(roleService.getAllRolesOfOrganization(any(Organization.class))).thenReturn(roles);
        ResponseEntity<List<Role>> response = roleController.getAllRolesOfOrganization();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Admin", response.getBody().get(0).getName());
        assertEquals("User", response.getBody().get(1).getName());
    }

    @Test
    void testGetAllRolesOfOrganization_ThrowsException() throws Exception {
        when(roleService.getAllRolesOfOrganization(any(Organization.class)))
                .thenThrow(new Exception("Service error"));
        Exception exception =
                assertThrows(Exception.class, () -> roleController.getAllRolesOfOrganization());
        assertEquals("Service error", exception.getMessage());
    }

    @Test
    void testAddRolesToOrganization_Success() throws Exception {
        AddRoleRequest newRoleRequest = new AddRoleRequest();
        newRoleRequest.setName("New Role");

        Role expectedRole = new Role();
        expectedRole.setId("role1");
        expectedRole.setName("New Role");
        when(roleService.addRoleToOrganization(newRoleRequest)).thenReturn(expectedRole);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        ResponseEntity<Role> response =
                roleController.addRolesToOrganization(newRoleRequest, bindingResult);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Role", response.getBody().getName());
    }

    @Test
    void testUpdateRolesOfOrganization_Success() throws Exception {
        String roleId = "role1";
        AddRoleRequest updatedRoleRequest = new AddRoleRequest();
        updatedRoleRequest.setName("Updated Role");

        Role updatedRole = new Role();
        updatedRole.setId(roleId);
        updatedRole.setName("Updated Role");
        when(roleService.updateRolesOfOrganization(roleId, updatedRoleRequest)).thenReturn(updatedRole);
        ResponseEntity<Role> response =
                roleController.updateRolesOfOrganization(roleId, updatedRoleRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Role", response.getBody().getName());
    }

    @Test
    void testUpdateRolesOfOrganization_InvalidRequest() throws Exception {
        String roleId = "role1";
        AddRoleRequest updatedRoleRequest = new AddRoleRequest();
        updatedRoleRequest.setName("");
        doThrow(new IllegalArgumentException("Invalid role name"))
                .when(roleService)
                .updateRolesOfOrganization(roleId, updatedRoleRequest);
        Exception exception = null;
        try {
            roleController.updateRolesOfOrganization(roleId, updatedRoleRequest);
        } catch (Exception ex) {
            exception = ex;
        }
        assertNotNull(exception);
        assertTrue(exception instanceof IllegalArgumentException);
        assertEquals("Invalid role name", exception.getMessage());
    }

    @Test
    void testDeleteRoleOfOrganizationById_Success() throws Exception {
        String roleId = "role1";
        Role deletedRole = new Role();
        deletedRole.setId(roleId);
        deletedRole.setName("Admin");
        when(roleService.deleteRolesOfOrganization(roleId)).thenReturn(deletedRole);
        ResponseEntity<?> response = roleController.deleteRoleOfOrganizationById(roleId);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(deletedRole, response.getBody());
        verify(roleService, times(1)).deleteRolesOfOrganization(roleId);
    }
}