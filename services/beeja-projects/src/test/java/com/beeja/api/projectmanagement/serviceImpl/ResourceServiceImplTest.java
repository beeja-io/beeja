package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.when;



import static org.junit.jupiter.api.Assertions.*;

class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrCreateResources_NullList() {
        List<Resource> result = resourceService.getOrCreateResources(null);
        assertTrue(result.isEmpty(), "Result should be empty for null input.");
    }

    @Test
    void testGetOrCreateResources_EmptyList() {
        List<Resource> result = resourceService.getOrCreateResources(Collections.emptyList());
        assertTrue(result.isEmpty(), "Result should be empty for an empty list.");
    }

    @Test
    void testGetOrCreateResources_ListWithNullEntries() {
        List<Resource> employees = new ArrayList<>();
        employees.add(null);

        List<Resource> result = resourceService.getOrCreateResources(employees);

        assertTrue(result.isEmpty(), "Result should be empty for a list with null entries.");
        verify(resourceRepository, never()).findByEmployeeIdIn(anyList());
    }

    @Test
    void testGetOrCreateResources_ExistingResources() {
        List<Resource> employees = new ArrayList<>();
        employees.add(new Resource(null, "E001", 50));
        employees.add(new Resource(null, "E002", 100));

        List<Resource> existingResources = List.of(
                new Resource("1", "E001", 50),
                new Resource("2", "E002", 100)
        );

        when(resourceRepository.findByEmployeeIdIn(List.of("E001", "E002"))).thenReturn(existingResources);
        List<Resource> result = resourceService.getOrCreateResources(employees);
        assertEquals(2, result.size());
        assertEquals("E001", result.get(0).getEmployeeId());
        assertEquals("E002", result.get(1).getEmployeeId());

        verify(resourceRepository, never()).saveAll(anyList());
    }

    @Test
    void testGetOrCreateResources_NewResources() {
        List<Resource> employees = new ArrayList<>();
        employees.add(new Resource(null, "E003", 70));

        when(resourceRepository.findByEmployeeIdIn(List.of("E003"))).thenReturn(Collections.emptyList());
        List<Resource> result = resourceService.getOrCreateResources(employees);
        assertEquals(1, result.size());
        assertEquals("E003", result.get(0).getEmployeeId());
        assertEquals(70, result.get(0).getAllocation());

        verify(resourceRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testAllocateResource_SuccessfulAllocation() {
        String employeeId = "E001";
        double allocation = 30;

        when(resourceRepository.findByEmployeeIdIn(List.of(employeeId)))
                .thenReturn(Collections.emptyList());

        Resource savedResource = new Resource("1", employeeId, allocation);
        when(resourceRepository.save(any(Resource.class))).thenReturn(savedResource);
        Resource result = resourceService.allocateResource(employeeId, allocation);
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals(allocation, result.getAllocation());

        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    void testAllocateResource_Exact100PercentAllocation() {
        String employeeId = "E004";
        double allocation = 50;

        List<Resource> existingAllocations = List.of(
                new Resource("1", employeeId, 50)
        );

        when(resourceRepository.findByEmployeeIdIn(List.of(employeeId)))
                .thenReturn(existingAllocations);

        Resource savedResource = new Resource("3", employeeId, allocation);
        when(resourceRepository.save(any(Resource.class))).thenReturn(savedResource);
        Resource result = resourceService.allocateResource(employeeId, allocation);
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals(allocation, result.getAllocation());

        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    void testAllocateResource_PartialAllocationSuccess() {
        String employeeId = "E003";
        double allocation = 40;

        List<Resource> existingAllocations = List.of(
                new Resource("1", employeeId, 50)
        );

        when(resourceRepository.findByEmployeeIdIn(List.of(employeeId)))
                .thenReturn(existingAllocations);

        Resource savedResource = new Resource("2", employeeId, allocation);
        when(resourceRepository.save(any(Resource.class))).thenReturn(savedResource);
        Resource result = resourceService.allocateResource(employeeId, allocation);
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals(allocation, result.getAllocation());

        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

}