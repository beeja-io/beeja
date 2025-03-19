package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.EmployeeClient;
import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.repository.ResourceRepository;
import com.beeja.api.projectmanagement.responses.EmployeeDetailsResponse;
import com.beeja.api.projectmanagement.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ResourceServiceImpl implements ResourcesService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private EmployeeClient employeeClient;

    @Override
    public List<Resource> getOrCreateResources(List<Resource> employees) {
        if (employees == null || employees.isEmpty()) {
            return List.of();
        }
        List<String> employeeIds = employees.stream()
                .map(Resource::getEmployeeId)
                .distinct()
                .toList();
        Map<String, Resource> existingResourceMap = resourceRepository.findByEmployeeIdIn(employeeIds)
                .stream()
                .collect(Collectors.toMap(Resource::getEmployeeId, resource -> resource));
        Map<String, String> employeeNameMap = getEmployeeNamesByIds(employeeIds);

        List<Resource> finalResources = new ArrayList<>();

        for (String empId : employeeIds) {
            if (existingResourceMap.containsKey(empId)) {
                finalResources.add(existingResourceMap.get(empId));
            } else {
                String employeeName = employeeNameMap.get(empId);
                Resource newResource = new Resource(null, empId, employeeName, 0.0);
                finalResources.add(newResource);
                resourceRepository.save(newResource);
            }
        }

        return finalResources;
    }

    @Override
    public Map<String, String> getEmployeeNamesByIds(List<String> employeeIds) {
        List<EmployeeDetailsResponse> employeeDetails = employeeClient.getEmployeeDetails();
        Map<String, String> employeeNamesMap = employeeDetails.stream()
                .filter(emp -> employeeIds.contains(emp.getEmployeeId()))
                .collect(Collectors.toMap(
                        EmployeeDetailsResponse::getEmployeeId,
                        EmployeeDetailsResponse::getFirstName
                ));
        return employeeNamesMap;
    }

    @Override
    public List<Resource> updateResourceAllocations(List<Resource> resources, Map<String, String> employeeNamesMap) {
        if (resources.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> employeeIds = resources.stream()
                .map(Resource::getEmployeeId)
                .distinct()
                .collect(Collectors.toList());

        List<Resource> existingResources = resourceRepository.findByEmployeeIdIn(employeeIds);

        Map<String, Resource> existingResourcesMap = existingResources.stream()
                .collect(Collectors.toMap(Resource::getEmployeeId, resource -> resource));

        List<Resource> resourcesToSave = new ArrayList<>();

        for (Resource requestResource : resources) {
            String employeeId = requestResource.getEmployeeId();
            Resource existingResource = existingResourcesMap.get(employeeId);
            double requestedAllocation = requestResource.getAllocation();

            if (existingResource != null) {  // Resource exists, update the allocation
                double newTotalAllocation = existingResource.getAllocation() + requestedAllocation;

                if (newTotalAllocation > 100.0) {
                    throw new IllegalArgumentException("Total allocation for employee ID " + employeeId + " exceeds 100%");
                }

                existingResource.setAllocation(newTotalAllocation);  // Update the allocation to new total
                existingResource.setFirstName(employeeNamesMap.get(employeeId));
                resourcesToSave.add(existingResource);
            } else {  // Resource doesn't exist, create a new one
                if (requestedAllocation > 100.0) {
                    throw new IllegalArgumentException("Allocation for new employee ID " + employeeId + " exceeds 100%");
                }

                requestResource.setFirstName(employeeNamesMap.get(employeeId));
                resourcesToSave.add(requestResource);
            }
        }

        return resourceRepository.saveAll(resourcesToSave);
    }



}
