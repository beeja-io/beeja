package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.repository.ResourceRepository;
import com.beeja.api.projectmanagement.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ResourceServiceImpl implements ResourcesService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public List<Resource> getOrCreateResources(List<Resource> employees) {
        if (employees == null || employees.isEmpty()) {
            return Collections.emptyList();
        }

        // Ensure no null values in the employees list
        employees = employees.stream()
                .filter(Objects::nonNull)  // Prevents NullPointerException
                .toList();

        List<String> employeeIds = employees.stream()
                .map(Resource::getEmployeeId)
                .filter(Objects::nonNull)  // Ensures only non-null employee IDs
                .distinct()
                .toList();

        if (employeeIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Resource> existingResources = resourceRepository.findByEmployeeIdIn(employeeIds);
        if (existingResources == null) {
            existingResources = new ArrayList<>();  // Prevent NullPointerException
        }

        Map<String, Resource> existingResourceMap = existingResources.stream()
                .collect(Collectors.toMap(Resource::getEmployeeId, resource -> resource, (r1, r2) -> r1));

        List<Resource> finalResources = new ArrayList<>();
        for (Resource emp : employees) {
            if (existingResourceMap.containsKey(emp.getEmployeeId())) {
                finalResources.add(existingResourceMap.get(emp.getEmployeeId()));
            } else {
                Resource newResource = new Resource();
                newResource.setEmployeeId(emp.getEmployeeId());
                newResource.setAllocation(emp.getAllocation());
                finalResources.add(newResource);
            }
        }

        // Save only new resources (if not already saved)
        List<Resource> newResources = finalResources.stream()
                .filter(resource -> resource.getId() == null)  // Ensure only unsaved resources are added
                .toList();

        if (!newResources.isEmpty()) {
            resourceRepository.saveAll(newResources);
        }

        return finalResources;
    }


    @Override
    public Resource allocateResource(String employeeId, double allocation) {
        List<Resource> existingAllocations = resourceRepository.findByEmployeeIdIn(List.of(employeeId));

        double totalAllocation = existingAllocations.stream()
                .mapToDouble(Resource::getAllocation)
                .sum();

        // Validate allocation percentage
        if (totalAllocation + allocation > 100) {
            throw new IllegalArgumentException("Total allocation for this employee exceeds 100%");
        }

        Resource newResource = new Resource(null, employeeId, allocation);
        return resourceRepository.save(newResource);
    }
}
