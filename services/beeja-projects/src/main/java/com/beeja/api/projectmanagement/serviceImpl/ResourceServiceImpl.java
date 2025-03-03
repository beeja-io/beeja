package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.repository.ResourceRepository;
import com.beeja.api.projectmanagement.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ResourceServiceImpl implements ResourcesService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public List<Resource> getOrCreateResources(List<Resource> employees) {
        if (employees == null || employees.isEmpty()) {
            return List.of();
        }

        List<String> employeeIds = employees.stream()
                .map(Resource::getEmployeeId)
                .toList();

        List<Resource> existingResources = resourceRepository.findByEmployeeIdIn(employeeIds);
        Map<String, Resource> existingResourceMap = existingResources.stream()
                .collect(Collectors.toMap(Resource::getEmployeeId, resource -> resource));

        List<Resource> finalResources = new ArrayList<>();
        for (String empId : employeeIds) {
            if (existingResourceMap.containsKey(empId)) {
                finalResources.add(existingResourceMap.get(empId));
            } else {
                finalResources.add(new Resource(null, empId));
            }
        }

        List<Resource> newResources = finalResources.stream()
                .filter(resource -> resource.getId() == null)
                .toList();

        if (!newResources.isEmpty()) {
            resourceRepository.saveAll(newResources);
        }

        return finalResources;
    }

}
