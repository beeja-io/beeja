package com.beeja.api.projectmanagement.controller;

import com.beeja.api.projectmanagement.controllers.ResourceController;
import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.service.ResourcesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


import java.util.ArrayList;
import java.util.List;

public class ResourceControllerTest {
    @Mock
    private ResourcesService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrGetResources_WithExistingResources() {
        List<Resource> employees = new ArrayList<>();
        employees.add(new Resource(null, "E001", 50));
        employees.add(new Resource(null, "E002", 100));

        List<Resource> resources = List.of(
                new Resource("1", "E001", 50),
                new Resource("2", "E002", 100)
        );

        when(resourceService.getOrCreateResources(employees)).thenReturn(resources);
        ResponseEntity<List<Resource>> response = resourceController.addOrGetResources(employees);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("E001", response.getBody().get(0).getEmployeeId());
        assertEquals("E002", response.getBody().get(1).getEmployeeId());

        verify(resourceService, times(1)).getOrCreateResources(employees);
    }

    @Test
    void testAllocateResource_Success() {
        String employeeId = "E004";
        double allocation = 30.0;

        Resource resource = new Resource("4", employeeId, allocation);

        when(resourceService.allocateResource(employeeId, allocation)).thenReturn(resource);
        Resource response = resourceController.allocateResource(employeeId, allocation);
        assertNotNull(response);
        assertEquals(employeeId, response.getEmployeeId());
        assertEquals(allocation, response.getAllocation());

        verify(resourceService, times(1)).allocateResource(employeeId, allocation);
    }

    @Test
    void testAllocateResource_AllocationExceedsLimit() {
        String employeeId = "E005";
        double allocation = 110.0;
        when(resourceService.allocateResource(employeeId, allocation))
                .thenThrow(new IllegalArgumentException("Total allocation for this employee exceeds 100%"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> resourceController.allocateResource(employeeId, allocation));

        assertEquals("Total allocation for this employee exceeds 100%", exception.getMessage());

        verify(resourceService, times(1)).allocateResource(employeeId, allocation);
    }
}
