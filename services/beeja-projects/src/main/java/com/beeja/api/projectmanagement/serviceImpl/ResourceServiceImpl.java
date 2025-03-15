package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.EmployeeClient;
import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.repository.ResourceRepository;
import com.beeja.api.projectmanagement.responses.EmployeeDetailsResponse;
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
                Resource newResource = new Resource(null, empId, employeeName);
                finalResources.add(newResource);
                resourceRepository.save(newResource);
            }
        }

        return finalResources;
    }
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



}
